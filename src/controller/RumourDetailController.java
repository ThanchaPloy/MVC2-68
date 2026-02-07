package controller;

import model.Report;
import model.Rumour;
import model.RumourService;
import model.User;
import model.EventBus;
import view.RumourDetailView;

import java.io.IOException;

public class RumourDetailController {

    private final RumourDetailView view;
    private final RumourService service;
    private final User currentUser;
    private final String rumourId;

    public RumourDetailController(RumourDetailView view, RumourService service, User currentUser, String rumourId) {
        this.view = view;
        this.service = service;
        this.currentUser = currentUser;
        this.rumourId = rumourId;

        view.getBtnBack().addActionListener(e -> view.dispose());
        view.getBtnReport().addActionListener(e -> onReport());
        view.getBtnVerifyTrue().addActionListener(e -> onVerify(Rumour.VerifiedResult.TRUE_INFO));
        view.getBtnVerifyFalse().addActionListener(e -> onVerify(Rumour.VerifiedResult.FALSE_INFO));
    }

    public void init() {
        // Show/hide Verify buttons depending on user role
        view.setVerifyButtonsVisible(currentUser.getRole() == User.Role.VERIFIER);
        reloadDetail();
    }

    private void reloadDetail() {
        try {
            view.setDetail(service.getRumourDetail(rumourId));
        } catch (IOException ex) {
            view.showError("Failed to load details: " + ex.getMessage());
        }
    }

    private void onReport() {
        try {
            Report.ReportType type = view.getSelectedReportType();

            service.addReport(currentUser.getUserId(), rumourId, type);
            // notify others (e.g., Summary) that data changed
            EventBus.getInstance().publishDataChanged();
            view.showInfo("Report successful");
            reloadDetail(); // update report count/status immediately
        } catch (IllegalStateException ise) {
            view.showError(ise.getMessage());
        } catch (IOException ex) {
            view.showError("Failed to save report: " + ex.getMessage());
        }
    }

    private void onVerify(Rumour.VerifiedResult result) {
        try {
            service.verifyRumour(currentUser.getUserId(), rumourId, result);
            // notify others (e.g., Summary) that data changed
            EventBus.getInstance().publishDataChanged();
            view.showInfo("Rumour verification successful: " + result);
            reloadDetail();
        } catch (IllegalStateException ise) {
            view.showError(ise.getMessage());
        } catch (IOException ex) {
            view.showError("Failed to verify rumour: " + ex.getMessage());
        }
    }
}
