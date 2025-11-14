package org.jcv.aimo;

import org.jcv.aimo.models.Dob;
import org.jcv.aimo.models.User;
import org.jcv.aimo.utils.DataParser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataParserTest
{
    @Test
    void testMapToUser_withValidData()
    {
        Map<String, Object> idMap = Map.of("value", "123ABC");
        Map<String, Object> nameMap = Map.of("first", "John", "last", "Doe");
        Map<String, Object> dobMap = Map.of("date", "1990-01-01T00:00:00Z", "age", 34);

        Map<String, Object> input = new HashMap<>();
        input.put("id", idMap);
        input.put("name", nameMap);
        input.put("dob", dobMap);
        input.put("gender", "male");
        input.put("nat", "US");
        input.put("email", "john.doe@example.com");
        input.put("phone", "555-1234");

        User result = DataParser.mapToUser(input);

        assertEquals("123ABC", result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("male", result.getGender());
        assertEquals("US", result.getCountry());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("555-1234", result.getPhone());

        Dob dob = result.getDob();
        assertNotNull(dob);
        assertEquals("1990-01-01T00:00:00Z", dob.getDate());
        assertEquals(34, dob.getAge());
    }

    @Test
    void testMapToUser_missingIdGeneratesUUID()
    {
        Map<String, Object> nameMap = Map.of("first", "Jane", "last", "Smith");
        Map<String, Object> input = new HashMap<>();
        input.put("name", nameMap);

        User result = DataParser.mapToUser(input);

        assertNotNull(result.getId());
        assertDoesNotThrow(() -> UUID.fromString(result.getId())); // should be a valid UUID
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void testMapToUser_handlesAgeAsDouble()
    {
        Map<String, Object> dobMap = new HashMap<>();
        dobMap.put("date", "1980-05-10T00:00:00Z");
        dobMap.put("age", 42.0); // API might return double instead of int

        Map<String, Object> input = new HashMap<>();
        input.put("name", Map.of("first", "Alice", "last", "Wonderland"));
        input.put("dob", dobMap);

        User result = DataParser.mapToUser(input);
        assertEquals(42, result.getDob().getAge());
    }

    @Test
    void testMapToUser_handlesMissingFieldsSafely()
    {
        // Completely empty input map
        User result = DataParser.mapToUser(new HashMap<>());

        assertNotNull(result.getId());
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        assertEquals("", result.getGender());
        assertEquals("", result.getCountry());
        assertEquals("", result.getEmail());
        assertEquals("", result.getPhone());
        assertNotNull(result.getDob());
        assertEquals("", result.getDob().getDate());
        assertEquals(0, result.getDob().getAge());
    }

    @Test
    void testMapToUser_logsErrorForInvalidAgeType()
    {
        // This tests that the logger error path is triggered for wrong type
        Map<String, Object> dobMap = Map.of("date", "2000-01-01T00:00:00Z", "age", "invalid-type");

        Map<String, Object> input = Map.of(
            "name", Map.of("first", "Bob", "last", "Builder"),
            "dob", dobMap
        );

        // We can’t easily assert logs without using a framework like LogCaptor,
        // but we can assert the method handles it gracefully.
        User result = DataParser.mapToUser(input);

        assertEquals(0, result.getDob().getAge());
        assertEquals("Bob", result.getFirstName());
        assertEquals("Builder", result.getLastName());
    }
}