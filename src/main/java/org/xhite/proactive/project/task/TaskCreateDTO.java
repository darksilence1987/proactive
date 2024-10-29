package org.xhite.proactive.project.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCreateDTO {
    @NotBlank(message = "Task name is required")
    private String taskName;

    private String taskDescription;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;
}