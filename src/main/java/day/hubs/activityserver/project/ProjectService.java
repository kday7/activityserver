package day.hubs.activityserver.project;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.services.FileService;
import day.hubs.activityserver.services.JsonService;
import day.hubs.activityserver.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    protected String hub;

    // Holds the raw data loaded from the json file
    private ProjectList projectList;
    private String projectsFile;
    private final List<Project> projects = new ArrayList<>();


    public ProjectService(final String projectsFile, final String hub) {
        super();
        this.projectsFile = projectsFile;
        this.hub = hub;
        initialise();
    }

    private void initialise() {
        final Optional<Object> projects = JsonService.readJsonDataFile(new File(this.projectsFile), ProjectList.class);
        if (projects.isPresent()) {
            this.projectList = (ProjectList)projects.get();
            this.projects.addAll( this.projectList.getActivityHubProjects() );
        }
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public boolean addProject(final Project newProject) {
        if (newProject.getId() == -1) {
            newProject.setId(this.projectList.getNextProjectId());
        }

        this.projects.add(newProject);

        return true;
    }

    public void saveProjects() {
        this.projectList.clearProjects();
        this.projectList.setActivityHubProjects(this.projects);
        JsonService.writeJsonDataFile(this.projectsFile, this.projectList);
    }

    public int getNextProjectId() {
        return this.projectList.getNextProjectId();
    }

    public int getNextTaskId() {
        return this.projectList.getNextTaskId();
    }

    public boolean projectExists(final int projectId) {
        return this.projects.stream()
                .anyMatch(project -> projectId == project.getId());
    }

    public Optional<Project> getProject(final int id) {
        return this.projects.stream()
                .filter(project -> id == project.getId())
                .findFirst();
    }

    public Optional<Project> getProject(final String view) {
        return this.projects.stream()
                .filter(project -> project.getView().equalsIgnoreCase(view))
                .findFirst();
    }

    public Project createProject(final Project newProjectDetails) {
        final Project newProject = new Project();
        newProject.setId(this.getNextProjectId());
        newProject.setName(newProjectDetails.getName());
        newProject.setView(newProjectDetails.getView());
        newProject.setGroup(newProjectDetails.getGroup());

        addProject(newProject);

        // Save projects
        saveProjects();

        return newProject;
    }

    public Project updateProject(final String hub, final Project updateProject, final Project newProjectDetails) {
        LOG.debug("ProjectService: Updating project: {}", updateProject.getName());

        updateProject.setName(newProjectDetails.getName());
        updateProject.setGroup(newProjectDetails.getGroup());

        if (!updateProject.getView().equals(newProjectDetails.getView())) {
            LOG.warn("ProjectService: View updates are not yet implemented");
            // TODO: If the view has changed then the file structure needs to change
            // 1. Rename the project folder
            FileService.renameProjectFolder(hub, updateProject.getView(), newProjectDetails.getView());

            // 2. For each task update the view

            updateProject.setView(newProjectDetails.getView());
        }

        // Save the changes
        saveProjects();

        return updateProject;
    }

    public boolean deleteProject(final int projectId) {
        // Remove project from list of active projects
        final Optional<Project> project = getProject(projectId);
        if (!project.isPresent()) {
            return false;
        }

        this.projects.remove(project.get());

        // Save projects
        saveProjects();

        return true;
    }

   /* public Optional<Task> getTask(final Project project, final String taskView) {
        if (null != project) {
            final List<Task> allTasks = project.getTasks();
            for(final Task task : allTasks) {
                if (task.getView().equalsIgnoreCase(taskView)) {
                    final String contentFile = ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + task.getLocation() + ".html";
                    try {
                        String contents = new String(Files.readAllBytes(Paths.get(contentFile)));
                        task.setHtmlContent(contents);
                    } catch (IOException e) {
                        LOG.error("Failed to read the file contents", e);
                    }

                    return Optional.of(task);
                }
            }
        }

        return Optional.empty();
    }*/

    public Optional<Task> getTaskById(final Project project, final int taskId) {
        if (null != project) {
            return project.getTasks().stream()
                    .filter(task -> taskId == task.getId())
                    .findFirst();
        }

        return Optional.empty();
    }

    public String getTaskContents(final Task task) {
        final String contentFile = ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + task.getLocation() + ".html";
        try {
            return new String(Files.readAllBytes(Paths.get(contentFile)));
        } catch (IOException e) {
            LOG.error("Failed to read the file contents", e);
        }

        return "<h1>Oops! File contents not found</h1>";
    }
}
