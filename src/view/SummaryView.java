package view;

import model.Rumour;
import model.RumourService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SummaryView extends JFrame {

    private final DefaultTableModel panicModel;
    private final JTable panicTable = new JTable();

    private final DefaultTableModel verifiedModel;
    private final JTable verifiedTable = new JTable();

    private final JButton btnRefresh = new JButton("Refresh Summary");
    private final JButton btnClose = new JButton("Close");

    public SummaryView() {
        super("Rumour Tracking - Summary");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);

        panicModel = new DefaultTableModel(new Object[]{"RumourId","Topic","Status","ReportCount","HotScore"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        verifiedModel = new DefaultTableModel(new Object[]{"RumourId","Topic","VerifiedResult","ReportCount"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        panicTable.setModel(panicModel);
        verifiedTable.setModel(verifiedModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnRefresh);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(panicTable),
                new JScrollPane(verifiedTable));
        split.setResizeWeight(0.5);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

    ((JScrollPane)split.getTopComponent()).setBorder(
        BorderFactory.createTitledBorder("Rumours in PANIC status"));
    ((JScrollPane)split.getBottomComponent()).setBorder(
        BorderFactory.createTitledBorder("Rumours verified as True/False"));
    }

    public void setPanicRows(List<RumourService.RumourDetail> rows) {
        panicModel.setRowCount(0);
        for (RumourService.RumourDetail d : rows) {
            Rumour r = d.rumour();
            panicModel.addRow(new Object[]{
                    r.getRumourId(),
                    r.getTopic(),
                    r.getStatus(),
                    d.reportCount(),
                    String.format("%.2f", d.hotScore())
            });
        }
    }

    public void setVerifiedRows(List<RumourService.RumourDetail> rows) {
        verifiedModel.setRowCount(0);
        for (RumourService.RumourDetail d : rows) {
            Rumour r = d.rumour();
            verifiedModel.addRow(new Object[]{
                    r.getRumourId(),
                    r.getTopic(),
                    r.getVerifiedResult(),
                    d.reportCount()
            });
        }
    }

    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnClose() { return btnClose; }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
