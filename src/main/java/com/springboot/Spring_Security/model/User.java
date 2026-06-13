package com.springboot.Spring_Security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // For Getter Setter
@Entity // For Model
@Table(name="user_security") // For the Table name is user_security
public class User {
    @Id
    private int id;
    private String username;
    private String password;
}
