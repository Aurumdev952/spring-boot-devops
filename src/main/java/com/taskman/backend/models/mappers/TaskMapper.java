package com.taskman.backend.models.mappers;

import com.taskman.backend.models.Task;
import com.taskman.backend.models.dtos.CreateTaskDto;

public class TaskMapper {
    public static Task toEntity(CreateTaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        return task;
    }
}
