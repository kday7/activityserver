package day.hubs.activityserver.search;

public class SearchResult {

    private String task;
    private String link;
    private String project;
    private int projectId;
    private int taskId;
    private String searchText;

    public SearchResult(final String searchText, final String project, final int projectId, final String task, final int taskId) {
        this.project = project;
        this.projectId = projectId;
        this.task = task;
        this.taskId = taskId;
        this.searchText = searchText;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final int projectId) {
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(final int taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getProject() {
        return this.project;
    }

    public void setProject(final String project) {
        this.project = project;
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void setSearchText(final String searchText) {
        this.searchText = searchText;
    }
}
