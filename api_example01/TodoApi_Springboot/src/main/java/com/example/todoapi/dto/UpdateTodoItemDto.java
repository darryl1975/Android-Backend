package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTodoItemDto(
        @NotBlank @Size(max = 255) String task,
        @Size(max = 255) String description,
        boolean deleted
) {}
