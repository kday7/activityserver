package day.hubs.activityserver.task;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.document.DocumentUtilities;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TaskManager {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    public static final String FILE_EXTENSION = ".html";

    // Opens the task file for editing
    public static boolean openTaskForEditing(final String hub, final Project project, final Task task) {
        final Path filePath = Paths.get(
                ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + project.getView() + '/' + task.getView() + FILE_EXTENSION);
        DocumentUtilities.openCodeFile(filePath.toFile());
        return true;
    }

    // Completes the task
    public static boolean completeTask(final String hub, final Task task) {
        task.complete();
        ActivityServerApplication.getHubService(hub).saveProjects();
        return true;
    }

    public static Task updateTask(final String hub, final Project sourceProject, final Task updateTask, final Task newTaskDetails, final Project targetProject) {
        LOG.debug("TaskManager: Editing task details: {}", updateTask.getName());
        final String originalTaskLink = updateTask.getLocation();

        updateTask.setName(newTaskDetails.getName());
        updateTask.setCategory(newTaskDetails.getCategory());
        updateTask.setStatus(newTaskDetails.getStatus());
        updateTask.setView(newTaskDetails.getView());
        updateTask.setLocation(newTaskDetails.getLocation());

        // Rename the html file if the task link has changed
        if (newTaskDetails.getLocation() != null && !newTaskDetails.getLocation().equals(originalTaskLink)) {
            LOG.debug("Renaming the HTML file");
            FileService.moveTaskPage(hub, originalTaskLink, newTaskDetails.getLocation());
        }

        if (targetProject != null && !targetProject.equals(sourceProject)) {
            sourceProject.removeTask(updateTask.getId());
            targetProject.addTask(updateTask);
        }

        // Save the changes
        ActivityServerApplication.getHubService(hub).saveProjects();

        // TODO: Remove the old task from the searchables
        //removeFromSearchables(project, originalTaskName);

        // TODO: Add the new task to the searchables
        //addTaskToSearchables(updateTask);

        return updateTask;
    }


    public static Task createTask(final String hub, final Project project, final Task newTaskDetails) throws IOException {
        LOG.debug("TaskManager: Creating task: {}", newTaskDetails.getName());

        // Create the html file
        FileService.createPageForTask(hub, project, newTaskDetails);

        // Add task to project in the project service
        project.addTask(newTaskDetails);

        // Save the project service
        ActivityServerApplication.getHubService(hub).saveProjects();

        // TODO: Add the new link to the searchables
        //addTaskToSearchables(newTaskDetails);

        return newTaskDetails;
    }

    public static void deleteTask(final String hub, final Project project, final Task taskDetails) throws IOException {
        LOG.debug("TaskManager: Deleting task: {}", taskDetails.getName());

        // TODO: Delete the html file
        FileService.deletePageForTask(hub, project, taskDetails);

        // Remove task from project in the project service
        project.removeTask(taskDetails.getId());

        // Save the project service
        ActivityServerApplication.getHubService(hub).saveProjects();

        // TODO: Remove the link from the searchables
        //removeTaskFromSearchables(newTaskDetails);
    }

}
