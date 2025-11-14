package org.jcv.aimo.utils;

import org.jcv.aimo.models.Dob;
import org.jcv.aimo.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class DataParser
{
    private static final Logger logger = LoggerFactory.getLogger(DataParser.class);

    public static User mapToUser(Map<String, Object> u)
    {
        // Extract nested fields safely
        Map<String, Object> idObj = (Map<String, Object>) u.getOrDefault("id", Map.of());
        Map<String, Object> nameObj = (Map<String, Object>) u.getOrDefault("name", Map.of());
        Map<String, Object> dobObj = (Map<String, Object>) u.getOrDefault("dob", Map.of());

        // Generate ID if missing
        String id = idObj.get("value") != null ? idObj.get("value").toString() : UUID.randomUUID().toString();

        // Build full name safely
        String firstName = nameObj.getOrDefault("first", "").toString();
        String lastName = nameObj.getOrDefault("last", "").toString();
        String fullName = firstName + " " + lastName;

        // Map DOB safely
        String dobDate = dobObj.getOrDefault("date", "").toString();
        int dobAge = 0;
        try {
            dobAge = dobObj.get("age") != null ? (Integer) dobObj.get("age") : 0;
        } catch (ClassCastException e) {

            String msg = "Error while extracting user age " + fullName;
            logger.error(msg, e);
            Object ageObj = dobObj.get("age");
            if (ageObj instanceof Number) {
                dobAge = ((Number) ageObj).intValue();
            }
        }

        // Build User object
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDob(new Dob(dobDate, dobAge));
        user.setGender(u.getOrDefault("gender", "").toString());
        user.setCountry(u.getOrDefault("nat", "").toString());
        user.setEmail(u.getOrDefault("email", "").toString());
        user.setPhone(u.getOrDefault("phone", "").toString());

        return user;
    }
}
