package com.eldercare.repository;

import com.eldercare.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorId(Long doctorId);
    List<DoctorAvailability> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}
