package com.example.todoapi.service;

import com.example.todoapi.dto.CreateTodoItemDto;
import com.example.todoapi.dto.UpdateTodoItemDto;
import com.example.todoapi.model.TodoItem;
import com.example.todoapi.repository.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoItemService {

    private final TodoItemRepository repository;

    public List<TodoItem> getAll(boolean includeDeleted) {
        return includeDeleted ? repository.findAll() : repository.findByDeletedFalse();
    }

    public Optional<TodoItem> getById(int id) {
        return repository.findById(id);
    }

    public TodoItem create(CreateTodoItemDto dto) {
        TodoItem item = new TodoItem();
        item.setTask(dto.task());
        item.setDescription(dto.description());
        return repository.save(item);
    }

    public Optional<TodoItem> update(int id, UpdateTodoItemDto dto) {
        return repository.findById(id).map(item -> {
            item.setTask(dto.task());
            item.setDescription(dto.description());
            item.setDeleted(dto.deleted());
            return repository.save(item);
        });
    }

    public boolean softDelete(int id) {
        return repository.findById(id).map(item -> {
            item.setDeleted(true);
            repository.save(item);
            return true;
        }).orElse(false);
    }
}
