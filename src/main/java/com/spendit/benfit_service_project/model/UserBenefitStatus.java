package com.spendit.benfit_service_project.model;

import jakarta.persistence.*;
import predef_java.Benefit;

@Entity
@Table(name = "user_benefit_status")
public class UserBenefitStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private Benefit benefit;

    private boolean active;

    protected UserBenefitStatus() {}

    public UserBenefitStatus(UserEntity user, Benefit benefit, boolean active) {
        this.user = user;
        this.benefit = benefit;
        this.active = active;
    }

    public UserEntity getUser() { return user; }
    public Benefit getBenefit() { return benefit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
