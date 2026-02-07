package model;

import java.time.LocalDate;

public class Rumour {
    public enum Status { NORMAL, PANIC }
    public enum VerifiedResult { UNVERIFIED, TRUE_INFO, FALSE_INFO }

    private final String rumourId; // 8 digits, first digit must not be 0
    private final String topic;
    private final String source;
    private final LocalDate createdDate;
    private final int credibilityScore; // 0-100
    private Status status;
    private VerifiedResult verifiedResult;

    public Rumour(String rumourId, String topic, String source, LocalDate createdDate,
                  int credibilityScore, Status status, VerifiedResult verifiedResult) {
        this.rumourId = rumourId;
        this.topic = topic;
        this.source = source;
        this.createdDate = createdDate;
        this.credibilityScore = credibilityScore;
        this.status = status;
        this.verifiedResult = verifiedResult;
    }

    public String getRumourId() { return rumourId; }
    public String getTopic() { return topic; }
    public String getSource() { return source; }
    public LocalDate getCreatedDate() { return createdDate; }
    public int getCredibilityScore() { return credibilityScore; }
    public Status getStatus() { return status; }
    public VerifiedResult getVerifiedResult() { return verifiedResult; }

    public void setStatus(Status status) { this.status = status; }
    public void setVerifiedResult(VerifiedResult verifiedResult) { this.verifiedResult = verifiedResult; }
}
