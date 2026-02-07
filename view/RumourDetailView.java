package view;

import model.Report;
import model.Rumour;
import model.RumourService;

import javax.swing.*;
import java.awt.*;

public class RumourDetailView extends JFrame {

    private final JLabel lblId = new JLabel("-");
    private final JLabel lblTopic = new JLabel("-");
    private final JLabel lblSource = new JLabel("-");
    private final JLabel lblDate = new JLabel("-");
    private final JLabel lblCred = new JLabel("-");
    private final JLabel lblStatus = new JLabel("-");
    private final JLabel lblVerified = new JLabel("-");
    private final JLabel lblReportCount = new JLabel("-");
    private final JLabel lblHotScore = new JLabel("-");

    private final JComboBox<Report.ReportType> reportTypeCombo =
            new JComboBox<>(Report.ReportType.values());

    private final JButton btnReport = new JButton("Report Rumour");
    private final JButton btnVerifyTrue = new JButton("Verify as True");
    private final JButton btnVerifyFalse = new JButton("Verify as False");
    private final JButton btnBack = new JButton("Back");

    public RumourDetailView() {
        super("Rumour Tracking - Rumour Detail");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 440);
        setLocationRelativeTo(null);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

    center.add(new JLabel("RumourId:")); center.add(lblId);
    center.add(new JLabel("Topic:")); center.add(lblTopic);
    center.add(new JLabel("Source:")); center.add(lblSource);
    center.add(new JLabel("Created Date:")); center.add(lblDate);
    center.add(new JLabel("Credibility Score:")); center.add(lblCred);
    center.add(new JLabel("Status:")); center.add(lblStatus);
    center.add(new JLabel("Verified Result:")); center.add(lblVerified);
    center.add(new JLabel("Report Count:")); center.add(lblReportCount);
    center.add(new JLabel("Hot Score:")); center.add(lblHotScore);

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Report Type:"));
        north.add(reportTypeCombo);
        north.add(btnReport);

        JPanel verifyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        verifyPanel.add(btnVerifyTrue);
        verifyPanel.add(btnVerifyFalse);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBack);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(verifyPanel, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);
    }

    public void setDetail(RumourService.RumourDetail d) {
        Rumour r = d.rumour();
        lblId.setText(r.getRumourId());
        lblTopic.setText(r.getTopic());
        lblSource.setText(r.getSource());
        lblDate.setText(String.valueOf(r.getCreatedDate()));
        lblCred.setText(String.valueOf(r.getCredibilityScore()));
        lblStatus.setText(String.valueOf(r.getStatus()));
        lblVerified.setText(String.valueOf(r.getVerifiedResult()));
        lblReportCount.setText(String.valueOf(d.reportCount()));
        lblHotScore.setText(String.format("%.2f", d.hotScore()));
    }

    public Report.ReportType getSelectedReportType() {
        return (Report.ReportType) reportTypeCombo.getSelectedItem();
    }

    public JButton getBtnReport() { return btnReport; }
    public JButton getBtnVerifyTrue() { return btnVerifyTrue; }
    public JButton getBtnVerifyFalse() { return btnVerifyFalse; }
    public JButton getBtnBack() { return btnBack; }

    public void setVerifyButtonsVisible(boolean visible) {
        btnVerifyTrue.setVisible(visible);
        btnVerifyFalse.setVisible(visible);
        revalidate();
        repaint();
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
