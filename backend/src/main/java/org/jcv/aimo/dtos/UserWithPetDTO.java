package org.jcv.aimo.dtos;

public record UserWithPetDTO(String id,
                             String name,
                             DobDTO dob,
                             String phone,
                             String email,
                             String gender,
                             String country,
                             String petImage) {
}
