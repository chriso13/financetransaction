package com.chriso.financetransaction.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth,
        String membershipNumber,
        LocalDate joinDate,
        String gender,
        String maritalStatus,
        String membershipStatus,
        boolean baptized,
        boolean active,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String emergencyContactName,
        String emergencyContactPhone
) {}
