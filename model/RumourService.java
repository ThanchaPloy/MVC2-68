package model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class RumourService {

    // for the PANIC status
    private final int panicThreshold = 3;

    private final CsvRumourRepository rumourRepo;
    private final CsvReportRepository reportRepo;
    private final CsvUserRepository userRepo;

    public RumourService(CsvRumourRepository rumourRepo, CsvReportRepository reportRepo, CsvUserRepository userRepo) {
        this.rumourRepo = rumourRepo;
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
    }

 
    public static class RumourRow {
        private final Rumour rumour;
        private final long reportCount;
        private final double hotScore;

        public RumourRow(Rumour rumour, long reportCount, double hotScore) {
            this.rumour = rumour;
            this.reportCount = reportCount;
            this.hotScore = hotScore;
        }

        public Rumour rumour() { return rumour; }
        public long reportCount() { return reportCount; }
        public double hotScore() { return hotScore; }
    }

    public static class RumourDetail {
        private final Rumour rumour;
        private final long reportCount;
        private final double hotScore;

        public RumourDetail(Rumour rumour, long reportCount, double hotScore) {
            this.rumour = rumour;
            this.reportCount = reportCount;
            this.hotScore = hotScore;
        }

        public Rumour rumour() { return rumour; }
        public long reportCount() { return reportCount; }
        public double hotScore() { return hotScore; }
    }

    public List<User> getAllUsers() throws IOException {
        return userRepo.findAll();
    }

    public List<RumourRow> getRumourRowsSortedByReportCountDesc() throws IOException {
        List<Rumour> all = rumourRepo.findAll();
        List<RumourRow> rows = new ArrayList<>();

        for (Rumour r : all) {
            long c = reportRepo.countByRumour(r.getRumourId());
            rows.add(new RumourRow(r, c, computeHotScore(r, c)));
        }

        rows.sort(Comparator.comparingLong(RumourRow::reportCount).reversed());
        return rows;
    }

    public List<RumourRow> getRumourRowsSortedByHotScoreDesc() throws IOException {
        List<Rumour> all = rumourRepo.findAll();
        List<RumourRow> rows = new ArrayList<>();

        for (Rumour r : all) {
            long c = reportRepo.countByRumour(r.getRumourId());
            rows.add(new RumourRow(r, c, computeHotScore(r, c)));
        }

        rows.sort(Comparator.comparingDouble(RumourRow::hotScore).reversed());
        return rows;
    }

    // Hot Score แสดงระดับความร้อนแรงของข่าวลือ
    // คำนวณจากจำนวนรายงาน (น้ำหนักหลัก) และความน่าเชื่อถือ
    // รายงานมาก และความน่าเชื่อถือต่ำ => Hot Score สูง
    public double computeHotScore(Rumour r, long reportCount) {
        return reportCount * 10.0 + (100 - r.getCredibilityScore()) * 0.2;
    }

    public RumourDetail getRumourDetail(String rumourId) throws IOException {
    Rumour r = rumourRepo.findById(rumourId)
        .orElseThrow(() -> new IllegalArgumentException("Rumour not found: " + rumourId));

        long count = reportRepo.countByRumour(rumourId);
        double hot = computeHotScore(r, count);
        return new RumourDetail(r, count, hot);
    }

    public void addReport(String reporterUserId, String rumourId, Report.ReportType type) throws IOException {
    Rumour r = rumourRepo.findById(rumourId)
        .orElseThrow(() -> new IllegalArgumentException("Rumour not found"));

        //if the rumour has been verified already -> reporting is not allowed
        if (r.getVerifiedResult() != Rumour.VerifiedResult.UNVERIFIED) {
            throw new IllegalStateException("This rumour has already been verified; reporting is not allowed");
        }

        //single user cannot report the same rumour more than once
        if (reportRepo.existsByUserAndRumour(reporterUserId, rumourId)) {
            throw new IllegalStateException("You have already reported this rumour (duplicate reports not allowed)");
        }

    // Append a new Report
        reportRepo.append(new Report(reporterUserId, rumourId, LocalDate.now(), type));

    // if report count exceeds threshold => PANIC
        long newCount = reportRepo.countByRumour(rumourId);
        // Trigger PANIC when report count reaches or exceeds the configured threshold
        if (newCount >= panicThreshold) {
            r.setStatus(Rumour.Status.PANIC);
            rumourRepo.update(r);
        }
    }

    public void verifyRumour(String verifierUserId, String rumourId, Rumour.VerifiedResult result) throws IOException {
    User u = userRepo.findById(verifierUserId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (u.getRole() != User.Role.VERIFIER) {
            throw new IllegalStateException("Only verifiers can verify rumours");
        }

    Rumour r = rumourRepo.findById(rumourId)
        .orElseThrow(() -> new IllegalArgumentException("Rumour not found"));

        long count = reportRepo.countByRumour(rumourId);
        if (count < 1) {
            throw new IllegalStateException("Cannot verify rumour with zero reports");
        }

        r.setVerifiedResult(result);
        rumourRepo.update(r);
    }

    public List<RumourDetail> getPanicRumours() throws IOException {
        List<Rumour> all = rumourRepo.findAll();
        List<RumourDetail> out = new ArrayList<>();
        for (Rumour r : all) {
            if (r.getStatus() == Rumour.Status.PANIC) {
                out.add(getRumourDetail(r.getRumourId()));
            }
        }
        return out;
    }

    public List<RumourDetail> getVerifiedRumours() throws IOException {
        List<Rumour> all = rumourRepo.findAll();
        List<RumourDetail> out = new ArrayList<>();
        for (Rumour r : all) {
            if (r.getVerifiedResult() != Rumour.VerifiedResult.UNVERIFIED) {
                out.add(getRumourDetail(r.getRumourId()));
            }
        }
        return out;
    }
}
