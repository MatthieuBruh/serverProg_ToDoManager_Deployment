package fi.haagahelia.serverprog.todomanager.domain.Model.tasks;

public enum TaskStatus {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private final String displayValue;

    private TaskStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
