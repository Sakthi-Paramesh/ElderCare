package com.eldercare.dto;

import com.eldercare.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private Role role;
    private String name;
}
