package com.taskman.backend.models.dtos;

import com.taskman.backend.models.enums.TaskStatus;

public record CreateTaskDto(
        String title,
        String description,
        TaskStatus status
) {
}
