package app;

import app.dao.ComplaintDAO;
import app.model.Complaint;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== University Student Complaint System (Console) ===");

        while (running) {
            System.out.println("\n1. Submit New Complaint");
            System.out.println("2. View All Complaints");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    submitComplaint(scanner);
                    break;
                case 2:
                    viewComplaints();
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    private static void submitComplaint(Scanner scanner) {
        System.out.println("\n--- Submit Complaint ---");
        System.out.print("Enter Student Name: ");
        String studentName = scanner.nextLine();

        System.out.print("Enter Category (Academic/Hostel/Admin/etc): ");
        String category = scanner.nextLine();

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        Complaint complaint = new Complaint(studentName, category, description, "Pending");

        boolean success = ComplaintDAO.addComplaint(complaint);
        if (success) {
            System.out.println("✅ Complaint submitted successfully!");
        } else {
            System.out.println("❌ Failed to submit complaint.");
        }
    }

    private static void viewComplaints() {
        // We'll implement this after improving ComplaintDAO
        System.out.println("View complaints feature coming soon...");
    }
}