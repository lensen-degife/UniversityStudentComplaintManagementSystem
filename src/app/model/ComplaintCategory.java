package app.model;

public enum ComplaintCategory {
    ACADEMIC,
    FACILITY,
    ADMINISTRATIVE,
    HARASSMENT,
    OTHERS;

    @Override
    public String toString() {
        return switch (this) {
            case ACADEMIC -> "Academic";
            case FACILITY -> "Facility";
            case ADMINISTRATIVE -> "Administrative";
            case HARASSMENT -> "Harassment";
            case OTHERS -> "Others";
        };
    }
}
