package app.model;

public class Complaint {

    private int id;
    private String studentName;
    private String category;
    private String description;
    private String status;

    // Constructor without ID (for new complaints)
    public Complaint(String studentName, String category, String description, String status) {
        this.studentName = studentName;
        this.category = category;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}