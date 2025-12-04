package com.spendit.benfit_service_project.model;

import jakarta.persistence.*;
import predef_java.Benefit;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_benefit_schedule")
public class UserBenefitSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private Benefit benefit;

    private LocalDateTime scheduledAt;

    protected UserBenefitSchedule() {}

    public UserBenefitSchedule(UserEntity user, Benefit benefit, LocalDateTime scheduledAt) {
        this.user = user;
        this.benefit = benefit;
        this.scheduledAt = scheduledAt;
    }

    public UserEntity getUser() { return user; }
    public Benefit getBenefit() { return benefit; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
}
