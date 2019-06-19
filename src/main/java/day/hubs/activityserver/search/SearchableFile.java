package day.hubs.activityserver.search;

public class SearchableFile {
    private String dataFile;
    private String task;
    private int taskId;
    private String projectName;
    private int projectId;

    public SearchableFile(final String dataFile, final String projectName, final int projectId, final String taskName, final int taskId) {
        this.dataFile = dataFile;
        this.projectName = projectName;
        this.projectId = projectId;
        this.task = taskName;
        this.taskId = taskId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void setTaskId(final int taskId) {
        this.taskId = taskId;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(final int projectId) {
        this.projectId = projectId;
    }

    public String getDataFile() {
        return this.dataFile;
    }

    public void setDataFile(final String dataFile) {
        this.dataFile = dataFile;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(final String name) {
        this.task = name;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }
}
