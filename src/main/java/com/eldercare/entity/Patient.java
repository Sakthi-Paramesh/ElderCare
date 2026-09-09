package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    private LocalDate dateOfBirth;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;

    private String emergencyContactName;

    private String emergencyContactNumber;

    private String profilePhotoUrl;
}
