package com.example.todoapi.controller;

import com.example.todoapi.dto.CreateTodoItemDto;
import com.example.todoapi.dto.UpdateTodoItemDto;
import com.example.todoapi.model.TodoItem;
import com.example.todoapi.service.TodoItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/Todo")
@Tag(name = "Todo", description = "CRUD operations for to-do items")
@RequiredArgsConstructor
public class TodoController {

    private final TodoItemService service;

    @GetMapping
    @Operation(summary = "Get all to-do items")
    @ApiResponse(responseCode = "200", description = "List of to-do items")
    public ResponseEntity<List<TodoItem>> getAll(
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(service.getAll(includeDeleted));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single to-do item by ID")
    @ApiResponse(responseCode = "200", description = "To-do item found")
    @ApiResponse(responseCode = "404", description = "To-do item not found")
    public ResponseEntity<TodoItem> getById(@PathVariable int id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new to-do item")
    @ApiResponse(responseCode = "201", description = "To-do item created")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<TodoItem> create(@Valid @RequestBody CreateTodoItemDto dto) {
        TodoItem created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing to-do item")
    @ApiResponse(responseCode = "200", description = "To-do item updated")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "To-do item not found")
    public ResponseEntity<TodoItem> update(
            @PathVariable int id, @Valid @RequestBody UpdateTodoItemDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a to-do item (sets deleted = true)")
    @ApiResponse(responseCode = "204", description = "To-do item soft-deleted")
    @ApiResponse(responseCode = "404", description = "To-do item not found")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.softDelete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
