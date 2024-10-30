package org.xhite.proactive.report;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.project.ProjectRepository;
import org.xhite.proactive.project.task.Task;
import org.xhite.proactive.project.task.TaskPriority;
import org.xhite.proactive.project.task.TaskRepository;
import org.xhite.proactive.project.task.TaskStatus;
import org.xhite.proactive.user.AppUser;
import org.xhite.proactive.user.UserService;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final JasperReportManager reportManager;
    private final UserService userService;
    private final TaskRepository taskRepository;
    @Override
    public Report generateProjectReport(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projectName", project.getProjectName());
        parameters.put("projectManager", project.getCreatedBy().getUsername());
        parameters.put("totalTasks", project.getProjectTasks().size());
        parameters.put("completedTasks", countTasksByStatus(project, TaskStatus.DONE));
        parameters.put("inProgressTasks", countTasksByStatus(project, TaskStatus.IN_PROGRESS));
        parameters.put("todoTasks", countTasksByStatus(project, TaskStatus.TODO));

        try {
            byte[] reportContent = reportManager.generatePdfReport(
                    "project_summary.jrxml", parameters);

            return reportRepository.save(Report.builder()
                    .reportType(ReportType.PROJECT_SUMMARY)
                    .pdfContent(reportContent)
                    .generatedBy(userService.getUserByUsername(username))
                    .project(project)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    @Override
    public Report generateTaskCompletionReport(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projectName", project.getProjectName());
        parameters.put("dateRange", getProjectDateRange(project));
        parameters.put("averageCompletionTime", calculateProjectAverageCompletionTime(project));
        parameters.put("completionRate", calculateCompletionRate(project));
        parameters.put("tasksByPriority", getTasksByPriority(project));
        parameters.put("completionTrend", calculateCompletionTrend(project));

        try {
            byte[] reportContent = reportManager.generatePdfReport(
                    "task_completion.jrxml", parameters);

            Report report = Report.builder()
                    .reportType(ReportType.TASK_COMPLETION)
                    .generatedBy(userService.getUserByUsername(username))
                    .project(project)
                    .build();

            report.setPdfContent(reportContent);
            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }



    @Override
    public Report generateUserPerformanceReport(String targetUsername, String requestingUsername) {
        AppUser targetUser = userService.getUserByUsername(targetUsername);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", targetUsername);
        parameters.put("totalTasksAssigned", countUserTasks(targetUser));
        parameters.put("completedTasks", countUserCompletedTasks(targetUser));
        parameters.put("averageCompletionTime", calculateAverageCompletionTime(targetUser));

        try {
            byte[] reportContent = reportManager.generatePdfReport(
                    "user_performance.jrxml", parameters);

            return reportRepository.save(Report.builder()
                    .reportType(ReportType.USER_PERFORMANCE)
                    .pdfContent(reportContent)
                    .generatedBy(userService.getUserByUsername(requestingUsername))
                    .targetUsername(targetUsername)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    @Override
    public List<Report> getReportsByProject(Long projectId) {
        return reportRepository.findByProjectOrderByGeneratedAtDesc(projectRepository.findById(projectId).orElse(null));
    }

    @Override
    public List<Report> getReportsByUser(AppUser user) {
        return reportRepository.findByGeneratedByOrderByGeneratedAtDesc(user);
    }

    @Override
    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    private String getProjectDateRange(Project project) {
        var tasks = project.getProjectTasks();
        if (tasks.isEmpty()) return "No tasks";

        var firstTask = tasks.stream()
                .min((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .get();
        var lastTask = tasks.stream()
                .max((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .get();

        return String.format("%s to %s",
                firstTask.getCreatedAt().format(DateTimeFormatter.ISO_DATE),
                lastTask.getCreatedAt().format(DateTimeFormatter.ISO_DATE));
    }

    private String calculateProjectAverageCompletionTime(Project project) {
        List<Task> completedTasks = project.getProjectTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .toList();

        if (completedTasks.isEmpty()) return "N/A";

        Duration averageDuration = completedTasks.stream()
                .map(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()))
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(completedTasks.size());

        return formatDuration(averageDuration);
    }

    private double calculateCompletionRate(Project project) {
        long totalTasks = project.getProjectTasks().size();
        if (totalTasks == 0) return 0.0;

        long completedTasks = countTasksByStatus(project, TaskStatus.DONE);
        return (double) completedTasks / totalTasks * 100;
    }

    private Map<TaskPriority, Long> getTasksByPriority(Project project) {
        return project.getProjectTasks().stream()
                .collect(Collectors.groupingBy(
                        Task::getPriority,
                        Collectors.counting()
                ));
    }

    private List<Map<String, Object>> calculateCompletionTrend(Project project) {
        return project.getProjectTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .sorted(Comparator.comparing(Task::getCompletedAt))
                .map(task -> {
                    Map<String, Object> trend = new HashMap<>();
                    trend.put("date", task.getCompletedAt().format(DateTimeFormatter.ISO_DATE));
                    trend.put("completionTime",
                            Duration.between(task.getCreatedAt(), task.getCompletedAt()).toDays());
                    return trend;
                })
                .collect(Collectors.toList());
    }

    private long countUserTasks(AppUser user) {
        return taskRepository.countByAssignedTo(user);
    }

    private long countUserCompletedTasks(AppUser user) {
        return taskRepository.countByAssignedToAndStatus(user, TaskStatus.DONE);
    }

    private long countTasksByStatus(Project project, TaskStatus status) {
        return project.getProjectTasks().stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    private String calculateAverageCompletionTime(AppUser user) {
        List<Task> completedTasks = taskRepository.findCompletedTasksByUser(user);

        if (completedTasks.isEmpty()) return "N/A";

        Duration averageDuration = completedTasks.stream()
                .map(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()))
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(completedTasks.size());

        return formatDuration(averageDuration);
    }

    private String formatDuration(Duration duration) {
        return String.format("%d days %d hours",
                duration.toDays(),
                duration.toHoursPart());
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

}
