package org.xhite.proactive.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xhite.proactive.project.Project;
import org.xhite.proactive.user.AppUser;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectOrderByGeneratedAtDesc(Project project);
    List<Report> findByGeneratedByOrderByGeneratedAtDesc(AppUser user);
    List<Report> findByReportTypeOrderByGeneratedAtDesc(ReportType reportType);
}

