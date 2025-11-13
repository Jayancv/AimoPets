package org.jcv.aimo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private Dob dob;
    private String phone;
    private String email;
    private String gender;
    private String country;
    private String petImage;

    public String getFuLLName() {
        return firstName + " " + lastName;
    }

}
