package it.decimo.prenotation_service.model;

public enum TableStatus {
    empty(0), prenotated(1), ordering(2), checking(3);

    private TableStatus(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}