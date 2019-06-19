package day.hubs.activityserver.project;

import day.hubs.activityserver.task.Task;

import java.util.ArrayList;
import java.util.List;

public class ProjectSummaryService {

    public static List<Project> getIncompleteProjects(final List<Project> allProjects) {
        final List<Project> incompleteProjects = new ArrayList<>();

        for (final Project project : allProjects) {
            final Project incompleteProject = new Project(project.getName());
            incompleteProject.setId(project.getId());
            incompleteProject.setView(project.getView());

            // Add any outstanding tasks
            for (final Task task : project.getTasks()) {
                if (!task.isComplete() && task.isTask()) {
                    incompleteProject.addTask(task.copy());
                }
            }
            if (incompleteProject.hasTasks()) {
                incompleteProjects.add( incompleteProject );
            }
        }

        return incompleteProjects;
    }
}
