package main.model;

import java.util.Optional;

public class IntersectionAlerts {

    private final boolean intersection;
    private final Optional<String> alerts;

    public IntersectionAlerts(boolean intersection, Optional<String> alerts) {
        this.intersection = intersection;
        this.alerts = alerts;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public Optional<String> getAlerts() {
        return alerts;
    }
}
