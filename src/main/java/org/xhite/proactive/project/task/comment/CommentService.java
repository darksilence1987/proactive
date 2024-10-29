package org.xhite.proactive.project.task.comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Long taskId, CommentDTO commentDTO, String username);
    List<Comment> getTaskComments(Long taskId);
    void deleteComment(Long commentId, String username);
}
