package day.hubs.activityserver.domain.enums;

public enum EGroup {
    PERSONAL("Personal Projects", 1),
    ACTIVE("Active Projects", 2),
    COMPLETED("Completed Projects", 3),
    ACTIONS("Actions", 4),
    MISCELLANEOUS("Miscellaneous", 5);

    private String name;
    private int index;

    EGroup(final String name, final int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public static EGroup valueFromIndex(final int index) {
        switch (index) {
            case 1:
                return PERSONAL;
            case 2:
                return ACTIVE;
            case 3:
                return COMPLETED;
            case 4:
                return ACTIONS;
            case 5:
                return MISCELLANEOUS;
            default:
                return ACTIVE;
        }
    }
}
