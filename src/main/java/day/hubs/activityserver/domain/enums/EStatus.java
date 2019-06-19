package day.hubs.activityserver.domain.enums;

public enum EStatus {
    COMPLETE("Complete", "F"),
    TODO("To Do", "T"),
    HOLD("On Hold", "H"),
    UNDEFINED("Undefined", "");

    private String description;
    private String code;

    EStatus(final String description, final String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCode() {
        return this.code;
    }

    public static EStatus valueFromCode(final String code) {
        switch (code) {
            case "F":
                return COMPLETE;
            case "T":
                return TODO;
            case "H":
                return HOLD;
            default:
                return UNDEFINED;
        }
    }
}
