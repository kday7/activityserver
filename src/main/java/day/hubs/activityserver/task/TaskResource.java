package day.hubs.activityserver.task;

import org.springframework.hateoas.ResourceSupport;

public class TaskResource extends ResourceSupport {

    private int identifier;
    private String name;
    private String category;
    private String location = "";
    private String view = "";
    private String status = "";
    private String htmlContent = "";

    public TaskResource(final Task task) {
        super();

        this.identifier = task.getId();
        this.name = task.getName();
        this.category = task.getCategory();
        this.location = task.getLocation();
        this.view = task.getView();
        this.status = task.getStatus();
        this.htmlContent = task.getHtmlContent();
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public String getLocation() {
        return this.location;
    }

    public String getView() {
        return this.view;
    }

    public String getStatus() {
        return this.status;
    }

    public String getHtmlContent() {
        return this.htmlContent;
    }

    public void setHtmlContent(final String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
