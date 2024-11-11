package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(name = "role_name", unique = true, nullable = false)
    private String rolName;

    @OneToMany(mappedBy = "rol")
    private Set<Usuario> usuarios = new HashSet<>();


    public Rol(String rolName) {
        this.rolName = rolName;
    }

    }


