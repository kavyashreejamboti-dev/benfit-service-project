package com.spendit.benfit_service_project.service;

import com.spendit.benfit_service_project.model.*;
import com.spendit.benfit_service_project.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import predef_java.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class BenefitServiceImpl
        implements BenefitService<AssignResult, UnassignResult, ScheduleResult> {

    private final UserRepository userRepository;
    private final UserBenefitStatusRepository statusRepository;
    private final UserBenefitScheduleRepository scheduleRepository;

    public BenefitServiceImpl(UserRepository userRepository,
                              UserBenefitStatusRepository statusRepository,
                              UserBenefitScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.scheduleRepository = scheduleRepository;
    }

    private Long toDb(UserId id) {
        return id.value();
    }

    private UserId toUserId(Long id) {
        return new UserId(id);
    }

    @Override
    public List<UserResponse> getUsers(GetUsersFilter filter) {
        List<UserEntity> users = userRepository.findAll();
        List<UserResponse> result = new ArrayList<>();

        for (UserEntity user : users) {
            boolean include = false;

            if (filter == GetUsersFilter.HAS_BENEFITS) {
                include = !statusRepository.findByUserAndActiveTrue(user).isEmpty();
            }

            if (filter == GetUsersFilter.HAS_OR_WILL_HAVE_BENEFITS) {
                if (!statusRepository.findByUserAndActiveTrue(user).isEmpty()) {
                    include = true;
                }
                if (!scheduleRepository.findByUser(user).isEmpty()) {
                    include = true;
                }
            }

            if (filter == GetUsersFilter.HAD_BENEFITS) {
                if (!statusRepository.findByUser(user).isEmpty()
                        && statusRepository.findByUserAndActiveTrue(user).isEmpty()) {
                    include = true;
                }
            }

            if (include) {
                Set<Benefit> activeBenefits = new HashSet<>();
                List<UserBenefitStatus> active =
                        statusRepository.findByUserAndActiveTrue(user);

                for (UserBenefitStatus status : active) {
                    activeBenefits.add(status.getBenefit());
                }

                result.add(new UserResponse(
                        toUserId(user.getId()),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCreatedAt(),
                        activeBenefits
                ));
            }
        }

        return result;
    }

    @Override
    public AssignResult assignBenefit(Set<UserId> users, Benefit benefit) {
        Set<UserId> success = new HashSet<>();
        List<OperationError> errors = new ArrayList<>();

        for (UserId userId : users) {
            Optional<UserEntity> userOpt = userRepository.findById(toDb(userId));

            if (!userOpt.isPresent()) {
                errors.add(new OperationError(userId, "NOT_FOUND", "User not found"));
                continue;
            }

            UserEntity user = userOpt.get();
            UserBenefitStatus existing =
                    statusRepository.findByUserAndBenefitAndActiveTrue(user, benefit);

            if (existing != null) {
                errors.add(new OperationError(
                        userId, "HAS_BENEFIT", "User already has benefit"));
                continue;
            }

            statusRepository.save(new UserBenefitStatus(user, benefit, true));
            success.add(userId);
        }

        return new AssignResult(success, errors);
    }

    @Override
    public UnassignResult unassignBenefit(Set<UserId> users, Benefit benefit) {
        Set<UserId> success = new HashSet<>();
        List<OperationError> errors = new ArrayList<>();

        for (UserId userId : users) {
            Optional<UserEntity> userOpt = userRepository.findById(toDb(userId));

            if (!userOpt.isPresent()) {
                errors.add(new OperationError(userId, "NOT_FOUND", "User not found"));
                continue;
            }

            UserEntity user = userOpt.get();
            UserBenefitStatus existing =
                    statusRepository.findByUserAndBenefitAndActiveTrue(user, benefit);

            if (existing == null) {
                errors.add(new OperationError(
                        userId, "NO_BENEFIT", "User does not have benefit"));
                continue;
            }

            existing.setActive(false);
            statusRepository.save(existing);
            success.add(userId);
        }

        return new UnassignResult(success, errors);
    }

    @Override
    public ScheduleResult scheduleBenefitAssigning(Set<UserId> users,
                                                   Benefit benefit,
                                                   LocalDateTime at) {
        Set<UserId> success = new HashSet<>();
        List<OperationError> errors = new ArrayList<>();

        for (UserId userId : users) {
            Optional<UserEntity> userOpt = userRepository.findById(toDb(userId));

            if (!userOpt.isPresent()) {
                errors.add(new OperationError(userId, "NOT_FOUND", "User not found"));
                continue;
            }

            scheduleRepository.save(
                    new UserBenefitSchedule(userOpt.get(), benefit, at));

            success.add(userId);
        }

        return new ScheduleResult(success, errors);
    }
}
