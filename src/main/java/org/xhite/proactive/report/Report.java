package org.xhite.proactive.report;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Lob
    private byte[] pdfContent;

    private LocalDateTime generatedAt;

    @ManyToOne
    private AppUser generatedBy;

    @ManyToOne
    private Project project; // Optional, for project-specific reports

    private String targetUsername; // For user performance reports

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
