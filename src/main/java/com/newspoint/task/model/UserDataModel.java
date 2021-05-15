package com.newspoint.task.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class UserDataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userDataId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Long phoneNumber;
}
