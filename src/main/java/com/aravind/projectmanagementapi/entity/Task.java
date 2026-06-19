package com.aravind.projectmanagementapi.entity;

import java.time.LocalDateTime;

import com.aravind.projectmanagementapi.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Task{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

     @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}