package org.jcv.aimo;

import org.jcv.aimo.controllers.UserPetController;
import org.jcv.aimo.dtos.DobDTO;
import org.jcv.aimo.dtos.UserWithPetDTO;
import org.jcv.aimo.mappers.ModelToDtoMapper;
import org.jcv.aimo.models.Dob;
import org.jcv.aimo.models.User;
import org.jcv.aimo.services.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

public class UserPetControllerTest
{
    private PetService service;

    private ModelToDtoMapper mapper;

    private WebTestClient client;

    @BeforeEach
    void setup()
    {
        service = mock(PetService.class);
        mapper = mock(ModelToDtoMapper.class);
        UserPetController controller = new UserPetController(service, mapper);
        client = WebTestClient.bindToController(controller).build();
    }

    @Test
    void getUsersWithPet_returnsOk()
    {
        User user = new User("id123", "", "John", "Doe",
            new Dob("1993-01-01T00:00:00Z", 32), "123-456", "john@example.com",
            "male", "FI", "https://images.dog/1.jpg");
        UserWithPetDTO userDto = new UserWithPetDTO("id123", "John Doe",
            new DobDTO("1993-01-01T00:00:00Z", 32), "123-456", "john@example.com",
            "male", "US", "https://images.dog/1.jpg");

        when(service.fetchUsersWithPets(1, "FI")).thenReturn(List.of(user));
        when(mapper.toDTO(user)).thenReturn(userDto);

        client.get().uri("/api/users-with-pet?results=1&nat=FI")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("John Doe")
            .jsonPath("$[0].email").isEqualTo("john@example.com")
            .jsonPath("$[0].petImage").isEqualTo("https://images.dog/1.jpg");
    }

    @Test
    void getUsersWithPet_returnsError()
    {
        when(service.fetchUsersWithPets(1, "FI"))
            .thenThrow(new RuntimeException("Downstream API failed"));

        client.get().uri("/api/users-with-pet?results=1&nat=FI")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.error").isEqualTo("Downstream API failed");
    }
}
