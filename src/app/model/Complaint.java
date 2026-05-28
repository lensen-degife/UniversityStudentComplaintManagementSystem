package app.model;

import java.time.LocalDateTime;

public class Complaint {

    private int id;
    private int studentId;
    private String studentName;
    private ComplaintCategory category;
    private String title;
    private String description;
    private String attachmentPath;
    private ComplaintStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime lastUpdatedAt;

    public Complaint() {
    }

    public Complaint(int studentId, String title, ComplaintCategory category, String description, String attachmentPath) {
        this.studentId = studentId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.attachmentPath = attachmentPath;
        this.status = ComplaintStatus.PENDING;
    }

    public Complaint(int id, int studentId, String studentName, ComplaintCategory category, String title, String description,
                     String attachmentPath, ComplaintStatus status, LocalDateTime submittedAt, LocalDateTime lastUpdatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.category = category;
        this.title = title;
        this.description = description;
        this.attachmentPath = attachmentPath;
        this.status = status;
        this.submittedAt = submittedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
}
