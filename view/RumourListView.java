package view;

import model.Rumour;
import model.RumourService;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RumourListView extends JFrame {

    private final JTable table = new JTable();
    private final DefaultTableModel model;

    private final JComboBox<User> userCombo = new JComboBox<>();
    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Report Count (desc)", "Hot Score (desc)"});
    private final JComboBox<String> verifiedFilter = new JComboBox<>(new String[]{"All", "Verified", "Unverified"});
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnOpenDetail = new JButton("View Details");
    private final JButton btnOpenSummary = new JButton("Open Summary");

    public RumourListView() {
        super("Rumour Tracking - Rumour List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 540);
        setLocationRelativeTo(null);

    model = new DefaultTableModel(
        new Object[]{"RumourId", "Topic", "Source", "Created Date", "Credibility", "Status", "Verified", "ReportCount", "HotScore"},
        0
    ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(new JLabel("Current User:"));
        top.add(userCombo);
    top.add(btnRefresh);
    top.add(new JLabel("Sort by:"));
    top.add(sortCombo);
    top.add(new JLabel("Verified:"));
    top.add(verifiedFilter);
        top.add(btnOpenSummary);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnOpenDetail);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public void setUsers(List<User> users) {
        userCombo.removeAllItems();
        for (User u : users) userCombo.addItem(u);
        if (!users.isEmpty()) userCombo.setSelectedIndex(0);
    }

    public User getSelectedUser() {
        return (User) userCombo.getSelectedItem();
    }

    public String getSelectedRumourIdOrNull() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return String.valueOf(model.getValueAt(row, 0));
    }

    public void setRumourRows(List<RumourService.RumourRow> rows) {
        model.setRowCount(0);
        for (RumourService.RumourRow rr : rows) {
            Rumour r = rr.rumour();
            model.addRow(new Object[]{
                    r.getRumourId(),
                    r.getTopic(),
                    r.getSource(),
                    r.getCreatedDate(),
                    r.getCredibilityScore(),
                    r.getStatus(),
                    r.getVerifiedResult(),
                    rr.reportCount(),
                    String.format("%.2f", rr.hotScore())
            });
        }
    }

    public JComboBox<String> getSortCombo() { return sortCombo; }
    public JComboBox<String> getVerifiedFilter() { return verifiedFilter; }
    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnOpenDetail() { return btnOpenDetail; }
    public JButton getBtnOpenSummary() { return btnOpenSummary; }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
