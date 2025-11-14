package org.jcv.aimo.controllers;

import org.jcv.aimo.dtos.UserWithPetDTO;
import org.jcv.aimo.mappers.ModelToDtoMapper;
import org.jcv.aimo.models.User;
import org.jcv.aimo.services.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserPetController
{
    private static final Logger logger = LoggerFactory.getLogger(UserPetController.class);

    private final PetService service;

    private final ModelToDtoMapper mapper;

    public UserPetController(PetService service, ModelToDtoMapper mapper)
    {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(path = "/users-with-pet")
    public ResponseEntity<?> getUsersWithPet(
        @RequestParam(defaultValue = "10") int results,
        @RequestParam(required = false) String nat
    )
    {
        try {
            List<User> users = service.fetchUsersWithPets(results, nat);
            List<UserWithPetDTO> userDtos = users.stream().map(mapper::toDTO).toList();
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            String msg = e.getMessage();    // "Exception : some custom message"
            logger.error("Error while fetching results. {}", msg);
            Map<String, Object> errorResponse = new HashMap<>();
            String customErrorMsg = msg.substring(msg.indexOf(":") + 1).trim();
            errorResponse.put("error", customErrorMsg);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
