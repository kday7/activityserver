package day.hubs.activityserver.project;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectList {

    private int nextProjectId;
    private int nextTaskId;
    private List<Project> hubProjects = new ArrayList<>();

    public List<Project> getActivityHubProjects() {
        this.hubProjects.sort(Comparator.comparing(Project::getGroup));
        return new ArrayList<>(this.hubProjects);
    }

    public boolean isEmpty() {
        return this.hubProjects.isEmpty();
    }

    public void setActivityHubProjects(final List<Project> taskHubProjects) {
        this.hubProjects = new ArrayList<>(taskHubProjects);
    }

    public int getNextProjectId() {
        return this.nextProjectId++;
    }

    public int getNextTaskId() {
        return this.nextTaskId++;
    }

    public void addProject(final Project newProject) {
        this.hubProjects.add(newProject);
    }

    public void removeProject(final Project project) {
        this.hubProjects.remove(project);
    }

    public boolean contains(final Project project) {
        return this.hubProjects.contains(project);
    }

    public void clearProjects() {
        this.hubProjects.clear();
    }
}
