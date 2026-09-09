package com.eldercare.service;

import com.eldercare.entity.Hospital;
import com.eldercare.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public List<Hospital> searchHospitalsByCity(String city) {
        return hospitalRepository.findByCityContainingIgnoreCase(city);
    }

    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
    }
}
