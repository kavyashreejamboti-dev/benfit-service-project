package com.spendit.benfit_service_project.repository;

import com.spendit.benfit_service_project.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
