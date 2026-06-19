package com.example.todoapi.repository;

import com.example.todoapi.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Integer> {
    List<TodoItem> findByDeletedFalse();
}
