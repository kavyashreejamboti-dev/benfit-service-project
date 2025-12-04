package com.spendit.benfit_service_project.service;

import com.spendit.benfit_service_project.model.*;
import com.spendit.benfit_service_project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import predef_java.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BenefitServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBenefitStatusRepository statusRepository;

    @Autowired
    private UserBenefitScheduleRepository scheduleRepository;

    @Autowired
    private BenefitService<AssignResult, UnassignResult, ScheduleResult> service;

    private long u1Id;
    private long u2Id;
    private long u3Id;

    @BeforeEach
    void setup() {
        statusRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity u1 = userRepository.save(
                new UserEntity("Kavya", "Jamboti", LocalDateTime.now()));
        UserEntity u2 = userRepository.save(
                new UserEntity("Andi", "Lehmann", LocalDateTime.now()));
        UserEntity u3 = userRepository.save(
                new UserEntity("Maria", "Schmidt", LocalDateTime.now()));

        u1Id = u1.getId();
        u2Id = u2.getId();
        u3Id = u3.getId();
    }

    @Test
    void assignBenefit_success() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u1Id));

        AssignResult result = service.assignBenefit(users, Benefit.CARD);

        assertEquals(1, result.success().size());
        assertTrue(result.errors().isEmpty());

        List<UserBenefitStatus> statuses =
                statusRepository.findByUserAndActiveTrue(
                        userRepository.findById(u1Id).get());

        assertEquals(1, statuses.size());
        assertEquals(Benefit.CARD, statuses.get(0).getBenefit());
    }

    @Test
    void assignBenefit_userAlreadyHasBenefit_shouldReturnError() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u1Id));

        service.assignBenefit(users, Benefit.CARD);
        AssignResult second = service.assignBenefit(users, Benefit.CARD);

        assertEquals(0, second.success().size());
        assertEquals(1, second.errors().size());
    }

    @Test
    void unassignBenefit_success() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u2Id));

        service.assignBenefit(users, Benefit.LUNCH);
        UnassignResult result = service.unassignBenefit(users, Benefit.LUNCH);

        assertEquals(1, result.success().size());
        assertTrue(result.errors().isEmpty());

        List<UserBenefitStatus> active =
                statusRepository.findByUserAndActiveTrue(
                        userRepository.findById(u2Id).get());

        assertTrue(active.isEmpty());
    }

    @Test
    void unassignBenefit_userDoesNotHaveBenefit_shouldReturnError() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u3Id));

        UnassignResult result = service.unassignBenefit(users, Benefit.CARD);

        assertEquals(0, result.success().size());
        assertEquals(1, result.errors().size());
    }

    @Test
    void scheduleBenefit_success() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u1Id));

        ScheduleResult result = service.scheduleBenefitAssigning(
                users, Benefit.LUNCH, LocalDateTime.now().plusDays(3));

        assertEquals(1, result.success().size());
        assertTrue(result.errors().isEmpty());

        List<UserBenefitSchedule> schedules =
                scheduleRepository.findByUser(
                        userRepository.findById(u1Id).get());

        assertEquals(1, schedules.size());
    }

    @Test
    void getUsers_HAS_BENEFITS() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u1Id));

        service.assignBenefit(users, Benefit.CARD);

        List<UserResponse> result =
                service.getUsers(GetUsersFilter.HAS_BENEFITS);

        assertEquals(1, result.size());
        assertEquals(u1Id, result.get(0).id().value());
    }

    @Test
    void getUsers_HAD_BENEFITS() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u2Id));

        service.assignBenefit(users, Benefit.CARD);
        service.unassignBenefit(users, Benefit.CARD);

        List<UserResponse> result =
                service.getUsers(GetUsersFilter.HAD_BENEFITS);

        assertEquals(1, result.size());
        assertEquals(u2Id, result.get(0).id().value());
    }

    @Test
    void getUsers_HAS_OR_WILL_HAVE_BENEFITS() {
        Set<UserId> users = new HashSet<UserId>();
        users.add(new UserId(u3Id));

        service.scheduleBenefitAssigning(
                users, Benefit.LUNCH, LocalDateTime.now().plusDays(5));

        List<UserResponse> result =
                service.getUsers(GetUsersFilter.HAS_OR_WILL_HAVE_BENEFITS);

        assertEquals(1, result.size());
        assertEquals(u3Id, result.get(0).id().value());
    }
}
