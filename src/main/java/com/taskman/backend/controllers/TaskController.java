package com.taskman.backend.controllers;

import com.taskman.backend.exceptions.ResourceNotFoundException;
import com.taskman.backend.models.Task;
import com.taskman.backend.models.domains.ApiResponse;
import com.taskman.backend.models.dtos.CreateTaskDto;
import com.taskman.backend.services.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final ITaskService service;

    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@Valid @RequestBody CreateTaskDto dto) {
        Task response = service.createTask(dto);
        return new ApiResponse<>(response, messageSource.getMessage("responses.saveEntitySuccess", null, LocaleContextHolder.getLocale()), HttpStatus.CREATED).toResponseEntity();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@Valid @RequestBody CreateTaskDto dto, @PathVariable String id) throws ResourceNotFoundException {
        Task response = service.updateTask(id, dto);
        return new ApiResponse<>(response, messageSource.getMessage("responses.updateEntitySuccess", null, LocaleContextHolder.getLocale()), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> getTaskById(@PathVariable String id) throws ResourceNotFoundException {
        Task response = service.getTaskById(id);
        return new ApiResponse<>(response, messageSource.getMessage("responses.getEntitySuccess", null, LocaleContextHolder.getLocale()), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getTasks() {
        List<Task> response = service.getTasks();
        return new ApiResponse<>(response, messageSource.getMessage("responses.getEntitiesSuccess", null, LocaleContextHolder.getLocale()), HttpStatus.OK).toResponseEntity();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteTask(@PathVariable String id) throws ResourceNotFoundException {
        service.deleteTask(id);
        return new ApiResponse<>(null, messageSource.getMessage("responses.deleteEntitySuccess", null, LocaleContextHolder.getLocale()), HttpStatus.OK).toResponseEntity();
    }
}
