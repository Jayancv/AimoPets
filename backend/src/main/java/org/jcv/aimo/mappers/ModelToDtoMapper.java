package org.jcv.aimo.mappers;

import org.jcv.aimo.dtos.DobDTO;
import org.jcv.aimo.dtos.UserWithPetDTO;
import org.jcv.aimo.models.User;
import org.springframework.stereotype.Component;

@Component
public class ModelToDtoMapper
{
    /**
     * Mapping User model to user Dto
     * @param user
     * @return UserWithPetDTO
     */
    public UserWithPetDTO toDTO(User user)
    {
        return new UserWithPetDTO(
            user.getId(),
            user.getFuLLName(),
            new DobDTO(user.getDob().getDate(), user.getDob().getAge()),
            user.getPhone(),
            user.getEmail(),
            user.getGender(),
            user.getCountry(),
            user.getPetImage()
        );
    }
}
