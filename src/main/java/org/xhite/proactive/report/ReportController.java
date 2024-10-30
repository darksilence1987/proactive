package org.xhite.proactive.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xhite.proactive.project.ProjectService;
import org.xhite.proactive.user.UserDTO;
import org.xhite.proactive.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ROLE_PROJECT_MANAGER')")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public String showReportsPage(Model model, @AuthenticationPrincipal UserDetails principal) {
        model.addAttribute("reportTypes", ReportType.values());
        model.addAttribute("projects", projectService.getProjectsOwnedByUser(principal.getUsername()));
        model.addAttribute("reports", reportService.findAll());
        return "reports";
    }

    @GetMapping("/search-users")
    @ResponseBody
    public List<UserDTO> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query).stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }


    @PostMapping("/generate")
    public String generateReport(@RequestParam ReportType type,
                                 @RequestParam(required = false) Long projectId,
                                 @RequestParam(required = false) String targetUsername,
                                 @AuthenticationPrincipal UserDetails principal) {
        Report report;
        switch (type) {
            case PROJECT_SUMMARY:
                report = reportService.generateProjectReport(projectId, principal.getUsername());
                break;
            case TASK_COMPLETION:
                report = reportService.generateTaskCompletionReport(projectId, principal.getUsername());
                break;
            case USER_PERFORMANCE:
                report = reportService.generateUserPerformanceReport(targetUsername, principal.getUsername());
                break;
            default:
                throw new IllegalArgumentException("Invalid report type");
        }

        return "redirect:/reports";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadReport(@PathVariable Long id) {
        Report report = reportService.getReportById(id);

        ByteArrayResource resource = new ByteArrayResource(report.getPdfContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(report.getPdfContent().length)
                .body(resource);
    }

}
