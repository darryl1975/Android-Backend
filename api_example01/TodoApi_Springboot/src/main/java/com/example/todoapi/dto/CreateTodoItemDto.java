package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoItemDto(
        @NotBlank @Size(max = 255) String task,
        @Size(max = 255) String description
) {}
