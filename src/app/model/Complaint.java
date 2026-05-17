package app.model;

public class Complaint {
    public int id;
    public String studentName;
    public String category;
    public String description;
    public String status;

    public Complaint(String studentName, String category, String description, String status) {
        this.studentName = studentName;
        this.category = category;
        this.description = description;
        this.status = status;
    }
}
