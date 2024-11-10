package com.bookmytour.entity;
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
    private Integer usuarioId;

    @Column(length = 100)
    private String firstName;
    @Column(length = 100)
    private String lastName;
    @Column(length = 100)
    private String email;
    @Column(length = 100)
    private String password;
    @Column(length = 100)
    private String imageProfile;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Rol rol;

}
