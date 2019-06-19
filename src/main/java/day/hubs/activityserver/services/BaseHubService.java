package day.hubs.activityserver.services;

import day.hubs.activityserver.activity.Activity;
import day.hubs.activityserver.activity.ActivityService;
import day.hubs.activityserver.document.Document;
import day.hubs.activityserver.document.DocumentService;
import day.hubs.activityserver.notice.Notice;
import day.hubs.activityserver.notice.NoticeService;
import day.hubs.activityserver.project.ArchivedProjectsService;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.project.ProjectService;
import day.hubs.activityserver.project.ProjectSummaryService;
import day.hubs.activityserver.search.SearchEngine;
import day.hubs.activityserver.search.SearchResult;
import day.hubs.activityserver.search.SearchableFile;
import day.hubs.activityserver.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static day.hubs.activityserver.Constants.HUB_PLACEHOLDER;

public class BaseHubService implements HubService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseHubService.class);

    private ProjectService projectService;
    private ArchivedProjectsService archivedProjectService;
    private ActivityService activityService;
    private NoticeService noticeService;
    private DocumentService documentService;
    private ProjectSummaryService projectSummaryService;
    private SearchEngine searchEngine;


    @Autowired
    public BaseHubService(final String projectsFile,
                          final String archivedProjectsFile,
                          final String activitiesFile,
                          final String noticesFile,
                          final String documentsFile,
                          final String hub) {
        super();

        LOG.info("Creating server for {}", hub);
        this.projectService = new ProjectService(projectsFile.replace(HUB_PLACEHOLDER, hub), hub);
        this.archivedProjectService = new ArchivedProjectsService(archivedProjectsFile.replace(HUB_PLACEHOLDER, hub), hub);
        this.activityService = new ActivityService(activitiesFile.replace(HUB_PLACEHOLDER, hub), hub);
        this.noticeService = new NoticeService(noticesFile.replace(HUB_PLACEHOLDER, hub), hub);
        this.documentService = new DocumentService(documentsFile.replace(HUB_PLACEHOLDER, hub));
        this.searchEngine = new SearchEngine(hub);

        // Create a list of SearchableFiles and add to the search engine
        for (final Project project: this.projectService.getProjects()) {
            for (final Task task: project.getTasks()) {
                this.searchEngine.addSearchableFile(
                        new SearchableFile(task.getLocation() + ".html", project.getName(), project.getId(), task.getName(), task.getId())
                );
            }
        }

    }

    ///////////
    // Projects
    ///////////
    @Override
    public ProjectService getProjectService() {
        return this.projectService;
    }

    @Override
    public List<Project> getProjects() {
        return this.projectService.getProjects();
    }

    @Override
    public boolean projectExists(final int projectId) {
        return this.projectService.projectExists(projectId);
    }

    @Override
    public Optional<Project> getProject(final int projectId) {
        return this.projectService.getProject(projectId);
    }

    @Override
    public Optional<Project> getProject(final String view) {
        return this.projectService.getProject(view);
    }

    @Override
    public Project createProject(final Project newProjectDetails) {
        return this.projectService.createProject(newProjectDetails);
    }

    @Override
    public Project updateProject(final String hub, final Project updateProject, final Project newProjectDetails) {
        return this.projectService.updateProject(hub, updateProject, newProjectDetails);
    }

    @Override
    public boolean deleteProject(final int projectId) {
        return this.projectService.deleteProject(projectId);
    }

    @Override
    public void saveProjects() {
        this.projectService.saveProjects();
    }

    @Override
    public int getNextProjectId() {
        return this.projectService.getNextProjectId();
    }

    @Override
    public int getNextTaskId() {
        return this.projectService.getNextTaskId();
    }

    /*@Override
    public Optional<Task> getTask(final Project project, final String taskView) {
        return this.projectService.getTask(project, taskView);
    }*/

    @Override
    public Optional<Task> getTaskById(final Project project, final int taskId) {
        return this.projectService.getTaskById(project, taskId);
    }

    ///////////
    // Archive
    ///////////
    @Override
    public List<Project> getArchivedProjects() {
        return this.archivedProjectService.getArchivedProjects();
    }

    @Override
    public Optional<Project> getArchivedProject(final int projectId) {
        return this.archivedProjectService.getArchivedProject(projectId);
    }

    @Override
    public boolean archiveProject(final int projectId) {
        final Optional<Project> project = this.projectService.getProject(projectId);
        if (!project.isPresent()) {
            return false;
        }
        final boolean wasArchived = this.archivedProjectService.addProject(project.get());
        final boolean wasRemoved = this.projectService.deleteProject(projectId);

        if (wasArchived) {
            this.archivedProjectService.saveArchivedProjects();
        }
        if (wasRemoved) {
            this.projectService.saveProjects();
        }

        return true;
    }

    @Override
    public boolean restoreProject(final int projectId) {
        final Optional<Project> archivedProject = this.archivedProjectService.getArchivedProject(projectId);
        if (archivedProject.isEmpty()) {
            return false;
        }
        this.projectService.addProject(archivedProject.get());
        this.archivedProjectService.removeProject(projectId);

        this.projectService.saveProjects();
        this.archivedProjectService.saveArchivedProjects();

        return true;
    }

    //////////////////
    // Project Summary
    //////////////////
    @Override
    public List<Project> getProjectSummary() {
        return this.projectSummaryService.getIncompleteProjects(this.getProjects());
    }

    /////////////
    // Activities
    /////////////
    @Override
    public List<Activity> getActivities() {
        return this.activityService.getActivities();
    }

    @Override
    public Optional<Activity> getActivity(final int activityId) {
        return this.activityService.getActivity(activityId);
    }

    @Override
    public Activity createActivity(final Activity newActivityDetails) {
        return this.activityService.createActivity(newActivityDetails);
    }

    @Override
    public Activity updateActivity(final String hub, final Activity updateActivity, final Activity newActivityDetails) {
        return this.activityService.updateActivity(hub, updateActivity, newActivityDetails);
    }

    @Override
    public boolean deleteActivity(final int activityId) {
        return this.activityService.deleteActivity(activityId);
    }

    @Override
    public void saveActivities() {
        this.activityService.saveActivities();
    }

    @Override
    public List<Activity> reloadActivities() {
        return this.activityService.reloadActivities();
    }

    @Override
    public boolean openActivitiesFile() {
        return this.activityService.openActivitiesFile();
    }

    ////////////
    // Documents
    ////////////
    @Override
    public List<Document> getDocuments() {
        return this.documentService.getDocuments();
    }

    //////////
    // Notices
    //////////
    @Override
    public List<Notice> getNotices() {
        return this.noticeService.getNotices();
    }

    @Override
    public Optional<Notice> getNotice(final int id) {
        return this.noticeService.getNotice(id);
    }

    @Override
    public Notice createNotice(final Notice newNoticeDetails) {
        return this.noticeService.createNotice(newNoticeDetails);
    }

    @Override
    public Notice updateNotice(final String hub, final Notice updateNotice, final Notice newNoticeDetails) {
        return this.noticeService.updateNotice(hub, updateNotice, newNoticeDetails);
    }

    @Override
    public boolean deleteNotice(final int id) {
        return this.noticeService.deleteNotice(id);
    }

    @Override
    public void saveNotices() {
        this.noticeService.saveNotices();
    }

    @Override
    public List<Notice> reloadNotices() {
        return this.noticeService.reloadNotices();
    }

    @Override
    public boolean openNoticesFile() {
        return this.noticeService.openNoticesFile();
    }


    ////////////////
    // Search Engine
    ////////////////
    @Override
    public List<SearchResult> search(final String searchText) {
        return this.searchEngine.search(searchText).getResults();
    }

    @Override
    public void addSearchableFile(final SearchableFile searchableFile) {
        this.searchEngine.addSearchableFile(searchableFile);
    }
}
