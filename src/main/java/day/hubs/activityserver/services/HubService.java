package day.hubs.activityserver.services;

import day.hubs.activityserver.activity.Activity;
import day.hubs.activityserver.document.Document;
import day.hubs.activityserver.notice.Notice;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.project.ProjectService;
import day.hubs.activityserver.search.SearchResult;
import day.hubs.activityserver.search.SearchableFile;
import day.hubs.activityserver.task.Task;

import java.util.List;
import java.util.Optional;

public interface HubService {

    ///////////
    // Projects
    ///////////
    ProjectService getProjectService();

    List<Project> getProjects();

    boolean projectExists(final int projectId);

    Optional<Project> getProject(final int projectId);

    Optional<Project> getProject(final String view);

    Project createProject(final Project newProjectDetails);

    Project updateProject(final String hub, final Project updateProject, final Project newProjectDetails);

    boolean deleteProject(final int projectId);

    void saveProjects();

    int getNextProjectId();

    int getNextTaskId();

    // Optional<Task> getTask(final Project project, final String taskView);

    Optional<Task> getTaskById(final Project project, final int id);

    ///////////
    // Archive
    ///////////
    List<Project> getArchivedProjects();

    Optional<Project> getArchivedProject(final int projectId);

    boolean archiveProject(final int projectId);

    boolean restoreProject(final int projectId);

    //////////////////
    // Project Summary
    //////////////////
    List<Project> getProjectSummary();

    /////////////
    // Activities
    /////////////
    List<Activity> getActivities();

    Optional<Activity> getActivity(final int activityId);

    Activity createActivity(final Activity newActivityDetails);

    Activity updateActivity(final String hub, final Activity updateActivity, final Activity newActivityDetails);

    boolean deleteActivity(final int activityId);

    void saveActivities();

    List<Activity> reloadActivities();

    boolean openActivitiesFile();

    ////////////
    // Documents
    ////////////
    List<Document> getDocuments();

    //////////
    // Notices
    //////////
    List<Notice> getNotices();

    Optional<Notice> getNotice(final int id);

    Notice createNotice(final Notice newNoticeDetails);

    Notice updateNotice(final String hub, final Notice updateNotice, final Notice newNoticeDetails);

    boolean deleteNotice(final int id);

    void saveNotices();

    List<Notice> reloadNotices();

    boolean openNoticesFile();

    ////////////////
    // Search Engine
    ////////////////
    List<SearchResult> search(final String searchText);

    void addSearchableFile(final SearchableFile searchableFile);
}
