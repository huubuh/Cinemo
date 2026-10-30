package com.moviebooking.backend.model;

import com.moviebooking.backend.model.enums.AccountStatus;
import com.moviebooking.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String fullName;
    @Column(unique = true, length = 20)
    private String phone;
    private LocalDate dateOfBirth;
    @Column(length = 1000)
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.CUSTOMER;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status = AccountStatus.ACTIVE;
    private int loyaltyPoints;
    @ManyToOne(fetch = FetchType.LAZY)
    private Cinema cinema;
}
