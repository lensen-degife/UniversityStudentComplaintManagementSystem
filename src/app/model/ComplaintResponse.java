package app.model;

import java.time.LocalDateTime;

public class ComplaintResponse {
    private int id;
    private int complaintId;
    private int responderUserId;
    private String responderName;
    private String message;
    private LocalDateTime createdAt;

    public ComplaintResponse() {
    }

    public ComplaintResponse(int complaintId, int responderUserId, String message) {
        this.complaintId = complaintId;
        this.responderUserId = responderUserId;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public int getResponderUserId() { return responderUserId; }
    public void setResponderUserId(int responderUserId) { this.responderUserId = responderUserId; }
    public String getResponderName() { return responderName; }
    public void setResponderName(String responderName) { this.responderName = responderName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
