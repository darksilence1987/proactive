package org.xhite.proactive.report;

import net.sf.jasperreports.engine.JRException;
import org.xhite.proactive.user.AppUser;

import java.io.FileNotFoundException;
import java.util.List;

public interface ReportService {
    public Report generateProjectReport(Long projectId, String username);
    public Report generateTaskCompletionReport(Long projectId, String username);
    public Report generateUserPerformanceReport(String targetUsername, String requestingUsername);
    public List<Report> getReportsByProject(Long projectId);
    public List<Report> getReportsByUser(AppUser user);
    public Report getReportById(Long reportId);

    public List<Report> findAll();

}
