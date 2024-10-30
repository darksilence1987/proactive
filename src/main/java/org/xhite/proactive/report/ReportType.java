package org.xhite.proactive.report;

public enum ReportType {
    PROJECT_SUMMARY("Proje Özet Raporu"),
    TASK_COMPLETION("Görev Tamamlama Raporu"),
    USER_PERFORMANCE("Kullanıcı Performans Raporu");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
