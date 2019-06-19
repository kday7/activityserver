package day.hubs.activityserver.project;

import day.hubs.activityserver.task.TaskResource;
import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

@ApiModel(description="All details about a project with hateoas links")
public class ProjectResource extends ResourceSupport {

    private int identifier;
    private String name;
    private String view;
    private int group = 2;
    private List<TaskResource> tasks;

    public ProjectResource(final Project project) {
        super();

        this.identifier = project.getId();
        this.name = project.getName();
        this.view = project.getView();
        this.group = project.getGroup();

        this.tasks = project.getTasks().stream().map(task -> new TaskResource(task)).collect(Collectors.toList());
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getView() {
        return this.view;
    }

    public int getGroup() {
        return this.group;
    }

    public List<TaskResource> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<TaskResource> tasks) {
        this.tasks = tasks;
    }
}
