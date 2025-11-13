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
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final PetService service;
    private final ModelToDtoMapper mapper;

    public PetController(PetService service, ModelToDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(path = "/users-with-pet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsersWithPet(
            @RequestParam(defaultValue = "10") int results,
            @RequestParam(required = false) String nat
    ) {
        try {
            List<User> users = service.fetchUsersWithPets(results, nat);
            List<UserWithPetDTO> userDtos= users.stream().map(mapper::toDTO).toList();
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            logger.error("Error while fetching results.", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
