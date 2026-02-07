package model;

import java.time.LocalDate;

public class Report {
    public enum ReportType { DISTORTION, INCITEMENT, FALSE_INFO }

    private final String reporterUserId;
    private final String rumourId;
    private final LocalDate reportDate;
    private final ReportType reportType;

    public Report(String reporterUserId, String rumourId, LocalDate reportDate, ReportType reportType) {
        this.reporterUserId = reporterUserId;
        this.rumourId = rumourId;
        this.reportDate = reportDate;
        this.reportType = reportType;
    }

    public String getReporterUserId() { return reporterUserId; }
    public String getRumourId() { return rumourId; }
    public LocalDate getReportDate() { return reportDate; }
    public ReportType getReportType() { return reportType; }
}
