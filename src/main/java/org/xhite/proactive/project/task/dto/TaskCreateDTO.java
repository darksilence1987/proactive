package org.xhite.proactive.project.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.xhite.proactive.project.task.TaskPriority;

@Data
public class TaskCreateDTO {
    @NotBlank(message = "Task name is required")
    private String taskName;

    private String taskDescription;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;
}