package com.spendit.benfit_service_project.repository;

import com.spendit.benfit_service_project.model.UserBenefitSchedule;
import com.spendit.benfit_service_project.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserBenefitScheduleRepository
        extends JpaRepository<UserBenefitSchedule, Long> {

    List<UserBenefitSchedule> findByUser(UserEntity user);
}
