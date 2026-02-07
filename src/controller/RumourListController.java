package controller;

import model.RumourService;
import model.User;
import view.RumourDetailView;
import view.RumourListView;

import java.io.IOException;

public class RumourListController {

    private final RumourListView view;
    private final RumourService service;

    public RumourListController(RumourListView view, RumourService service) {
        this.view = view;
        this.service = service;

        view.getBtnRefresh().addActionListener(e -> loadDefault());
        // Listen to the dropdown selection and call the appropriate loader
        view.getSortCombo().addActionListener(e -> reloadCurrentSort());
        // Listen to verified filter changes and reload current sort when changed
        view.getVerifiedFilter().addActionListener(e -> reloadCurrentSort());
        view.getBtnOpenDetail().addActionListener(e -> openDetail());
    }

    public void init() {
        try {
            view.setUsers(service.getAllUsers());
            loadDefault();
        } catch (IOException ex) {
            view.showError("Failed to load users: " + ex.getMessage());
        }
    }

    public void loadDefault() {
        loadSortByReport();
    }

    private void loadSortByReport() {
        try {
            applyAndSetRows(service.getRumourRowsSortedByReportCountDesc());
        } catch (IOException ex) {
            view.showError("Failed to load rumours: " + ex.getMessage());
        }
    }

    private void loadSortByHot() {
        try {
            applyAndSetRows(service.getRumourRowsSortedByHotScoreDesc());
        } catch (IOException ex) {
            view.showError("Failed to load rumours: " + ex.getMessage());
        }
    }

    // Apply verified filter to rows then set into view
    private void applyAndSetRows(java.util.List<RumourService.RumourRow> rows) {
        String filter = (String) view.getVerifiedFilter().getSelectedItem();
        if (filter == null || "All".equals(filter)) {
            view.setRumourRows(rows);
            return;
        }

        java.util.List<RumourService.RumourRow> out = new java.util.ArrayList<>();
        for (RumourService.RumourRow rr : rows) {
            if ("Verified".equals(filter)) {
                if (rr.rumour().getVerifiedResult() != model.Rumour.VerifiedResult.UNVERIFIED) out.add(rr);
            } else if ("Unverified".equals(filter)) {
                if (rr.rumour().getVerifiedResult() == model.Rumour.VerifiedResult.UNVERIFIED) out.add(rr);
            }
        }
        view.setRumourRows(out);
    }

    private void reloadCurrentSort() {
        String sel = (String) view.getSortCombo().getSelectedItem();
        if (sel == null) sel = "Report Count (desc)";
        if (sel.startsWith("Report")) loadSortByReport(); else loadSortByHot();
    }

    private void openDetail() {
        String rumourId = view.getSelectedRumourIdOrNull();
        if (rumourId == null) {
            view.showError("Please select a rumour from the table first");
            return;
        }

        User current = view.getSelectedUser();
        if (current == null) {
            view.showError("Please select a user");
            return;
        }

        RumourDetailView dv = new RumourDetailView();
        RumourDetailController dc = new RumourDetailController(dv, service, current, rumourId);
        dc.init();
        dv.setVisible(true);
    }
}
