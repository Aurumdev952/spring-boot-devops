package com.taskman.backend.services;

import com.taskman.backend.exceptions.ResourceNotFoundException;
import com.taskman.backend.models.Task;
import com.taskman.backend.models.dtos.CreateTaskDto;

import java.util.List;

public interface ITaskService {
    public Task createTask(CreateTaskDto dto);
    public Task updateTask(String id, CreateTaskDto dto) throws ResourceNotFoundException;;
    public Task getTaskById(String id) throws ResourceNotFoundException;
    public List<Task> getTasks();
    public void deleteTask(String id) throws ResourceNotFoundException;
}
