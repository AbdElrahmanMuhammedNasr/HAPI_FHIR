package com.example.demo.Model;

import lombok.Data;

import java.util.Date;
@Data
public class CustomPatient {
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthDate;


    private String city;
    private String country;
    private String postalCode;
}
