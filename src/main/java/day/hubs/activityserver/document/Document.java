package day.hubs.activityserver.document;

public class Document {
    private String name;
    private String path;
    private String type;
    private String icon;
    private String project;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public String getProject() {
        return this.project;
    }

    public void setProject(final String project) {
        this.project = project;
    }
}
