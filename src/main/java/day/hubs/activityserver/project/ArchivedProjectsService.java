package day.hubs.activityserver.project;

import day.hubs.activityserver.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchivedProjectsService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    protected String hub;

    // Holds the raw data loaded from the json file
    private ProjectList projectList;
    private String archivedProjectsFile;
    private final List<Project> archivedProjects = new ArrayList<>();


    public ArchivedProjectsService(final String archivedProjectsFile, final String hub) {
        super();
        this.archivedProjectsFile = archivedProjectsFile;
        this.hub = hub;
        initialise();
    }

    private void initialise() {
        final Optional<Object> archivedProjects = JsonService.readJsonDataFile(new File(this.archivedProjectsFile), ProjectList.class);
        if (archivedProjects.isPresent()) {
            this.projectList = (ProjectList)archivedProjects.get();
            this.archivedProjects.addAll(this.projectList.getActivityHubProjects());
        }
    }

    public List<Project> getArchivedProjects() {
        return this.archivedProjects;
    }

    public Optional<Project> getArchivedProject(final int id) {
        return this.archivedProjects.stream().filter(project -> id == project.getId())
                .findFirst();
    }

    public void saveArchivedProjects() {
        JsonService.writeJsonDataFile(this.archivedProjectsFile, this.projectList);
    }

    public boolean addProject(final Project archivedProject) {
        if (!this.projectList.contains(archivedProject)) {
            this.projectList.addProject(archivedProject);
            this.archivedProjects.add(archivedProject);
            return true;
        }
        return false;
    }

    public boolean removeProject(final int id) {
        final Optional<Project> archivedProject = getArchivedProject(id);
        if (archivedProject.isEmpty()) {
            return false;
        }

        this.projectList.removeProject(archivedProject.get());
        this.archivedProjects.remove(archivedProject.get());

        return true;
    }
}
