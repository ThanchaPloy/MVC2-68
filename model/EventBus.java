package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    public interface Listener { void onDataChanged(); }

    private static final EventBus INSTANCE = new EventBus();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    private EventBus() {}

    public static EventBus getInstance() { return INSTANCE; }

    public void register(Listener l) { if (l != null) listeners.add(l); }
    public void unregister(Listener l) { listeners.remove(l); }

    public void publishDataChanged() {
        for (Listener l : listeners) {
            try { l.onDataChanged(); } catch (Throwable t) { /* ignore listener errors */ }
        }
    }
}
