package com.bookmytour.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private Integer role_id;
    private String image_profile;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Rol rol;

}
