package org.xhite.proactive.project.task.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {
    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}

