package day.hubs.activityserver.domain.enums;

public enum EProfile {
    TASKHUB("taskhub", "Task Hub", "TaskHub"),
    INFOHUB("infohub", "Info Hub", "InfoHub"),
    BASEHUB("basehub", "Base Hub", "BaseHub");

    private String identifier;
    private String label;
    private String rootContext;
    private boolean archivingEnabled = false;
    private boolean statusTrackingEnabled = false;

    EProfile(final String identifier, final String label, final String rootContext) {
        this.identifier = identifier;
        this.label = label;
        this.rootContext = rootContext;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getLabel() {
        return this.label;
    }

    public String getRootContext() {
        return this.rootContext;
    }

    public boolean isArchivingEnabled() {
        return this.archivingEnabled;
    }

    public void setArchivingEnabled(final boolean archivingEnabled) {
        this.archivingEnabled = archivingEnabled;
    }

    public boolean isStatusTrackingEnabled() {
        return this.statusTrackingEnabled;
    }

    public void setStatusTrackingEnabled(final boolean statusTrackingEnabled) {
        this.statusTrackingEnabled = statusTrackingEnabled;
    }

    public static EProfile valueFromIdentifier(final String identifier) {
        switch (identifier) {
            case "taskhub":
                return TASKHUB;
            case "infohub":
                return INFOHUB;
            default:
                return BASEHUB;
        }
    }
}
