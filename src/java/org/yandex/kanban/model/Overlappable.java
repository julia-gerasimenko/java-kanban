package org.yandex.kanban.model;

import java.time.LocalDateTime;

public interface Overlappable {
    default boolean overlaps(Overlappable other) {
        if (other == null) {
            return false;
        }
        if (this.isEmpty() || other.isEmpty()) {
            return false;
        }
        if (this.getStartTime().isEqual(other.getStartTime())) {
            return true;
        }
        if (this.getStartTime().isBefore(other.getStartTime())) {
            return this.getEndTime().isAfter(other.getStartTime());
        } else return other.getEndTime().isAfter(this.getStartTime());
    }

    private boolean isEmpty() {
        return getStartTime() == null || getEndTime() == null;
    }
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
}
