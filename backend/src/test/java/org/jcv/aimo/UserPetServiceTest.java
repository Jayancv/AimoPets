package org.jcv.aimo;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jcv.aimo.models.User;
import org.jcv.aimo.services.PetService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class UserPetServiceTest {

    static MockWebServer randomUserServer;
    static MockWebServer dogServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        randomUserServer = new MockWebServer();
        randomUserServer.start();

        dogServer = new MockWebServer();
        dogServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        randomUserServer.shutdown();
        dogServer.shutdown();
    }

    @Test
    void fetchUsersWithPets_happyPath() throws Exception {
        // Mock RandomUser response
        String ruJson = """
                {
                  "results": [
                    {
                      "gender": "female",
                      "name": { "first": "Jane", "last": "Mac" },
                      "email": "jane@example.com",
                      "dob": { "date": "1993-01-01T00:00:00.000Z", "age": 32 },
                      "phone": "123-456",
                      "nat": "GB",
                      "id": { "value": "ID123" },
                      "login": { "uuid": "uuid-1" }
                    }
                  ]
                }
                """;

        // Mock Dog API response
        String dogJson = """
                {
                  "message": ["https://images.dog/1.jpg"],
                  "status": "success"
                }
                """;

        randomUserServer.enqueue(new MockResponse().setBody(ruJson).setHeader("Content-Type", "application/json"));
        dogServer.enqueue(new MockResponse().setBody(dogJson).setHeader("Content-Type", "application/json"));


        // Instantiate PetService
        PetService service = new PetService(WebClient.builder(), randomUserServer.url("/").toString(),
                dogServer.url("/").toString());


        // Call method under test
        List<User> users = service.fetchUsersWithPets(1, "US");

        // Validate response
        assertNotNull(users);
        assertEquals(1, users.size());
        User u = users.get(0);
        assertEquals("Jane", u.getFirstName());
        assertEquals("Mac", u.getLastName());
        assertEquals("GB", u.getCountry());
        assertEquals("https://images.dog/1.jpg", u.getPetImage());

        assert (randomUserServer.getRequestCount() == 1 || randomUserServer.getRequestCount() == 0);
    }

}
