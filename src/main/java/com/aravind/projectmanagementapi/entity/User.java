package com.aravind.projectmanagementapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User{
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "owner",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Project> projects = new ArrayList<>();

     @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}

