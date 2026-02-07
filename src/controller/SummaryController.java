package controller;

import model.EventBus;
import model.RumourService;
import view.SummaryView;

import java.io.IOException;

public class SummaryController {

    private final SummaryView view;
    private final RumourService service;
    // keep a reference to the listener so we can unregister on close
    private final EventBus.Listener listener = this::refresh;

    public SummaryController(SummaryView view, RumourService service) {
        this.view = view;
        this.service = service;

        view.getBtnRefresh().addActionListener(e -> refresh());
        view.getBtnClose().addActionListener(e -> {
            // unregister listener before closing
            EventBus.getInstance().unregister(listener);
            view.dispose();
        });

        // register to receive data-change notifications
        EventBus.getInstance().register(listener);
    }

    public void init() {
        refresh();
    }

    public void refresh() {
        try {
            view.setPanicRows(service.getPanicRumours());
            view.setVerifiedRows(service.getVerifiedRumours());
        } catch (IOException ex) {
            view.showError("Failed to load summary: " + ex.getMessage());
        }
    }
}
