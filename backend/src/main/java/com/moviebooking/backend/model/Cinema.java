package com.moviebooking.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
public class Cinema extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1000)
    private String address;
    @Column(nullable = false)
    private String city;
    @Column(length = 20)
    private String phone;
    private boolean active = true;
}
