package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class CsvReportRepository {
    private final File file;

    public CsvReportRepository(String path) {
        this.file = new File(path);
    }

    public List<Report> findAll() throws IOException {
        List<Report> reports = new ArrayList<>();
        if (!file.exists()) return reports;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String header = null;
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                if (t.startsWith("//")) continue;
                header = line;
                break;
            }
            if (header == null) return reports;


            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                if (t.startsWith("//")) continue;

                String[] p = line.split(",", -1);
                if (p.length < 4) continue; // malformed/partial line, skip

                String userId = p[0].trim();
                String rumourId = p[1].trim();
                LocalDate date = LocalDate.parse(p[2].trim());
                Report.ReportType type = Report.ReportType.valueOf(p[3].trim());

                reports.add(new Report(userId, rumourId, date, type));
            }
        }
        return reports;
    }

    public void append(Report report) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();

        boolean exists = file.exists();
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {

            if (!exists) {
                bw.write("reporterUserId,rumourId,reportDate,reportType\n");
            }
            bw.write(String.join(",",
                    report.getReporterUserId(),
                    report.getRumourId(),
                    report.getReportDate().toString(),
                    report.getReportType().name()
            ));
            bw.write("\n");
        }
    }

    public long countByRumour(String rumourId) throws IOException {
        return findAll().stream().filter(r -> r.getRumourId().equals(rumourId)).count();
    }

    public boolean existsByUserAndRumour(String userId, String rumourId) throws IOException {
        // Rule: a user cannot report the same rumour more than once
        return findAll().stream().anyMatch(r ->
                r.getReporterUserId().equals(userId) && r.getRumourId().equals(rumourId)
        );
    }
}
