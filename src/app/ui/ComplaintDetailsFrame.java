package app.ui;

import app.dao.ComplaintDAO;
import app.dao.ComplaintResponseDAO;
import app.model.Complaint;
import app.model.ComplaintResponse;
import app.model.ComplaintStatus;
import app.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ComplaintDetailsFrame extends JFrame {

    private final User currentUser;
    private Complaint complaint;
    private final boolean adminView;
    private final ComplaintDAO complaintDAO;
    private final ComplaintResponseDAO responseDAO;
    private final Runnable onUpdated;

    private final JLabel statusLabel = new JLabel();
    private final JLabel categoryLabel = new JLabel();
    private final JLabel studentLabel = new JLabel();
    private final JTextArea descriptionArea = new JTextArea();

    private final DefaultTableModel responseModel = new DefaultTableModel(new Object[]{"Sender Name", "Message Text", "Timestamp Record"}, 0);
    private final JTable responseTable = new JTable(responseModel);

    public ComplaintDetailsFrame(User currentUser, Complaint complaint, boolean adminView,
                                 ComplaintDAO complaintDAO, ComplaintResponseDAO responseDAO, Runnable onUpdated) {
        this.currentUser = currentUser;
        this.complaint = complaint;
        this.adminView = adminView;
        this.complaintDAO = complaintDAO;
        this.responseDAO = responseDAO;
        this.onUpdated = onUpdated;

        setTitle("Incident Ledger Ticket View — #" + complaint.getId());
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UiTheme.APP_BACKGROUND);

        setLayout(new BorderLayout(0, 16));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        loadComplaintDetails();
        loadResponses();
    }

    private JPanel createHeaderPanel() {
        JPanel block = new JPanel(new GridLayout(2, 2, 16, 12));
        block.setBackground(UiTheme.CARD_BACKGROUND);
        block.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        setupHeaderLabel(studentLabel);
        setupHeaderLabel(categoryLabel);
        setupHeaderLabel(statusLabel);

        JLabel titleLabel = new JLabel("Topic Title: " + complaint.getTitle());
        setupHeaderLabel(titleLabel);

        block.add(studentLabel);
        block.add(categoryLabel);
        block.add(titleLabel);
        block.add(statusLabel);
        return block;
    }

    private void setupHeaderLabel(JLabel lbl) {
        lbl.setFont(UiTheme.HEADER_FONT);
        lbl.setForeground(UiTheme.PRIMARY);
    }

    private JPanel createBodyPanel() {
        JPanel mainSplitGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        mainSplitGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        mainSplitGrid.setBackground(UiTheme.APP_BACKGROUND);

        // Description Panel Wrapper Card
        JPanel leftCard = new JPanel(new BorderLayout(0, 8));
        leftCard.setBackground(UiTheme.CARD_BACKGROUND);
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(12, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel leftTitle = new JLabel("Original Complaint Details Log");
        leftTitle.setFont(UiTheme.HEADER_FONT);
        leftCard.add(leftTitle, BorderLayout.NORTH);

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UiTheme.FIELD_FONT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(new UiTheme.RoundedBorder(8, UiTheme.BORDER_COLOR));
        leftCard.add(descScroll, BorderLayout.CENTER);

        // Response History Wrapper Card
        JPanel rightCard = new JPanel(new BorderLayout(0, 8));
        rightCard.setBackground(UiTheme.CARD_BACKGROUND);
        rightCard.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(12, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel rightTitle = new JLabel("Official Response Timeline Thread");
        rightTitle.setFont(UiTheme.HEADER_FONT);
        rightCard.add(rightTitle, BorderLayout.NORTH);

        UiTheme.styleTable(responseTable);
        JScrollPane tableScroll = new JScrollPane(responseTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER_COLOR));
        rightCard.add(tableScroll, BorderLayout.CENTER);

        mainSplitGrid.add(leftCard);
        mainSplitGrid.add(rightCard);
        return mainSplitGrid;
    }

    private JPanel createActionPanel() {
        JPanel utilityTray = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        utilityTray.setBackground(UiTheme.CARD_BACKGROUND);
        utilityTray.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 20, 4, 20)
        ));

        JTextField responseField = new JTextField();
        responseField.setPreferredSize(new Dimension(280, 38));
        responseField.setFont(UiTheme.FIELD_FONT);
        responseField.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(6, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        utilityTray.add(responseField);

        JButton addResponseButton = new JButton("Post Reply");
        UiTheme.stylePrimaryButton(addResponseButton);
        addResponseButton.setPreferredSize(new Dimension(120, 38));
        addResponseButton.addActionListener(e -> {
            String message = responseField.getText().trim();
            if (message.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter message characters to submit response logs.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ComplaintResponse res = new ComplaintResponse(complaint.getId(), currentUser.getId(), message);
            if (responseDAO.addResponse(res)) {
                responseField.setText("");
                loadResponses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit message to the system thread.", "Database Sync Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        utilityTray.add(addResponseButton);

        if (adminView) {
            JComboBox<ComplaintStatus> statusCombo = new JComboBox<>(ComplaintStatus.values());
            statusCombo.setFont(UiTheme.FIELD_FONT);
            statusCombo.setPreferredSize(new Dimension(140, 38));
            statusCombo.setSelectedItem(complaint.getStatus());
            utilityTray.add(statusCombo);

            JButton updateStatusButton = new JButton("Modify Status");
            UiTheme.styleSecondaryButton(updateStatusButton);
            updateStatusButton.setPreferredSize(new Dimension(135, 38));
            updateStatusButton.addActionListener(e -> {
                ComplaintStatus status = (ComplaintStatus) statusCombo.getSelectedItem();
                if (status == null) return;
                if (complaintDAO.updateStatus(complaint.getId(), status)) {
                    complaint = complaintDAO.getById(complaint.getId());
                    loadComplaintDetails();
                    onUpdated.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not persist state modifications across database tables.", "Persistence Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            utilityTray.add(updateStatusButton);
        }

        JButton closeButton = new JButton("Exit View");
        UiTheme.styleMutedButton(closeButton);
        closeButton.setPreferredSize(new Dimension(110, 38));
        closeButton.addActionListener(e -> dispose());
        utilityTray.add(closeButton);

        return utilityTray;
    }

    private void loadComplaintDetails() {
        studentLabel.setText("Student Account: " + complaint.getStudentName());
        categoryLabel.setText("Classification: " + complaint.getCategory());
        statusLabel.setText("Workflow State: " + complaint.getStatus());
        descriptionArea.setText(complaint.getDescription() + (complaint.getAttachmentPath() == null ? "" : "\n\n========================================\nDocument Attachment Reference Path:\n" + complaint.getAttachmentPath()));
    }

    private void loadResponses() {
        responseModel.setRowCount(0);
        List<ComplaintResponse> responses = responseDAO.getByComplaintId(complaint.getId());
        for (ComplaintResponse response : responses) {
            responseModel.addRow(new Object[]{response.getResponderName(), response.getMessage(), response.getCreatedAt()});
        }
    }
}