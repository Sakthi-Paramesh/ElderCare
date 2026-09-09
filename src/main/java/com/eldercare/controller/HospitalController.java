package com.eldercare.controller;

import com.eldercare.entity.Hospital;
import com.eldercare.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals(@RequestParam(required = false) String city) {
        if (city != null && !city.isEmpty()) {
            return ResponseEntity.ok(hospitalService.searchHospitalsByCity(city));
        }
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }
}
