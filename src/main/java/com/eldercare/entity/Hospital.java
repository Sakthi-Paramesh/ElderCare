package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String about;
    
    private String imageUrl;

    private String openingHours; // e.g. "Mon-Sun, 24 Hours"

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "hospital_departments",
        joinColumns = @JoinColumn(name = "hospital_id"),
        inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @Builder.Default
    private Set<Department> departments = new HashSet<>();
}
