package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department specialization;

    private String qualification;

    private Integer experienceYears;

    @Column(columnDefinition = "TEXT")
    private String about;

    private String profilePhotoUrl;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private BigDecimal consultationFee;

    @Builder.Default
    @Column(nullable = false)
    private boolean isApproved = false; // Admin must approve
}
