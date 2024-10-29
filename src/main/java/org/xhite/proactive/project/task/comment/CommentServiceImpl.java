package org.xhite.proactive.project.task.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.xhite.proactive.project.task.Task;
import org.xhite.proactive.project.task.TaskService;
import org.xhite.proactive.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;

    @Override
    public Comment addComment(Long taskId, CommentDTO commentDTO, String username) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .task(task)
                .createdBy(userService.getUserByUsername(username))
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getTaskComments(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return commentRepository.findByTaskOrderByCreatedAtDesc(task);
    }

    @Override
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getCreatedBy().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }
}
