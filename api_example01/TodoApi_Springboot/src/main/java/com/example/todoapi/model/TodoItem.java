package com.example.todoapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "to_do")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "task", length = 255)
    private String task;
}
