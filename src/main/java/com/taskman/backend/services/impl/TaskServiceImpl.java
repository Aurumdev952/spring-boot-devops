package com.taskman.backend.services.impl;

import com.taskman.backend.exceptions.ResourceNotFoundException;
import com.taskman.backend.models.Task;
import com.taskman.backend.models.dtos.CreateTaskDto;
import com.taskman.backend.models.mappers.TaskMapper;
import com.taskman.backend.repositories.ITaskRepository;
import com.taskman.backend.services.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements ITaskService {

    private final ITaskRepository repository;

    @Override
    public Task createTask(CreateTaskDto dto) {
        Task task = TaskMapper.toEntity(dto);
        repository.save(task);
        return task;
    }

    @Override
    public Task updateTask(String id, CreateTaskDto dto) throws ResourceNotFoundException {
        Task task = repository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        repository.save(task);
        return task;
    }

    @Override
    public Task getTaskById(String id) throws ResourceNotFoundException {
        return repository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    @Override
    public List<Task> getTasks() {
        return repository.findAll();
    }

    @Override
    public void deleteTask(String id) throws ResourceNotFoundException {
        if (!repository.existsById(UUID.fromString(id))) {
            throw new ResourceNotFoundException("Task", "id", id);
        }
        repository.deleteById(UUID.fromString(id));
    }
}
