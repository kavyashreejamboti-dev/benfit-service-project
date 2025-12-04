package com.spendit.benfit_service_project.repository;

import com.spendit.benfit_service_project.model.UserBenefitStatus;
import com.spendit.benfit_service_project.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import predef_java.Benefit;
import java.util.List;

public interface UserBenefitStatusRepository
        extends JpaRepository<UserBenefitStatus, Long> {

    List<UserBenefitStatus> findByUser(UserEntity user);
    List<UserBenefitStatus> findByUserAndActiveTrue(UserEntity user);
    UserBenefitStatus findByUserAndBenefitAndActiveTrue(UserEntity user, Benefit benefit);
}
