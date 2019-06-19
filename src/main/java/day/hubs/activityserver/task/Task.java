package day.hubs.activityserver.task;

import java.util.Objects;

public class Task {

    public static final String STATUS_COMPLETE = "COMPLETE";
    public static final String STATUS_TODO = "TODO";

    public static final String CATEGORY_UNDEFINED = "UNDEFINED";
    public static final String CATEGORY_TASK = "TASK";

    private int id;
    private String name;
    private String category = CATEGORY_UNDEFINED;
    private String location = "";
    private String view = "";
    private String status = "TODO";
    private String htmlContent = "";

    public Task() {
        this(-1, "", CATEGORY_UNDEFINED, "", "", STATUS_TODO);
    }

    public Task(final int id, final String name, final String category, final String location, final String view, final String status) {
        this.id = id;
        this.name = name;           // Descriptive name that appears in the sidebar
        this.category = category;
        this.location = location;           // URL
        this.view = view;           // part of the URL and the file name
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setCategory( final String category )
    {
        this.category = category;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(final String location)
    {
        this.location = location;
    }

    public String getView() {
        return this.view;
    }

    public void setView( final String view )
    {
        this.view = view;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getHtmlContent() {
        return this.htmlContent;
    }

    public void setHtmlContent(final String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public boolean isComplete() {
        return this.status.equals(STATUS_COMPLETE);
    }

    public void complete() {
        this.status = STATUS_COMPLETE;
    }

    public boolean isTask() {
        return this.category.equals(CATEGORY_TASK);
    }

    public Task copy() {
        final Task newTask = new Task(this.id, this.name, this.category, this.location, this.view, this.status);
        newTask.setHtmlContent(this.htmlContent);
        return newTask;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", category=" + this.category +
                ", location='" + this.location + '\'' +
                ", view='" + this.view + '\'' +
                ", status=" + this.status +
                '}';
    }
}
