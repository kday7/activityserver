package day.hubs.activityserver.project;

import day.hubs.activityserver.task.Task;
import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(description="All details about a project")
public class Project {

    private int id;
    private String name;
    private String view;
    private int group = 2;
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(final Project source) {
        this(-1, source.name, source.view, source.tasks, source.group);
    }

    public Project(final String name) {
        this.name = name;
    }

    public Project(final int id, final String name, final String view, final List<Task> tasks, final int group) {
        this.id = id;
        this.name = name;
        this.view = view;
        this.tasks = new ArrayList<>(tasks);
        this.group = group;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getView() {
        return this.view;
    }

    public void setView(final String view) {
        this.view = view;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(final List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(final Task newTask) {
        this.tasks.add(newTask);
    }

    public void removeTask(final int taskId) {
        for(final Task task : this.tasks) {
            if (task.getId() == taskId) {
                this.tasks.remove(task);
                return;
            }
        }
    }

    public boolean hasTasks() {
        return !this.tasks.isEmpty();
    }

    public int getGroup() {
        return this.group;
    }

    public void setGroup(final int group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + this.id + '\'' +
                ", name='" + this.name + '\'' +
                ", view='" + this.view + '\'' +
                ", group=" + this.group +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
