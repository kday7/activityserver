package day.hubs.activityserver.task;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.project.ProjectResource;
import day.hubs.activityserver.project.ProjectService;
import day.hubs.activityserver.project.ProjectsController;
import day.hubs.activityserver.services.HubService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@Api(value="/{Hub}/Projects/{id}/Task", produces="application/json")
@RestController
@RequestMapping("/{.+Hub}/Projects/{.+}/Tasks")
public class TaskController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);


    @ApiOperation(value="Retrieve all tasks for a specified project", response=List.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of tasks"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Resources<TaskResource>> getTasks(final HttpServletRequest request) {
        LOG.info("getTasks");
        final int projectId = extractProjectId(request);
        final Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProjectService().getProject(projectId);
        if (project.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final List<Task> projectTasks = project.get().getTasks();

        final List<TaskResource> taskResources = projectTasks.stream()
                .map(task -> convertTaskToTaskResource(task, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToTaskResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(taskResources)));
    }

    @ApiOperation(value="Find a task", response=TaskResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully found the task"),
            @ApiResponse(code=404, message="The task could not be found")
    })
    @GetMapping(value="/{taskId}")
    @ResponseBody
    public ResponseEntity<TaskResource> getTask(@PathVariable("taskId") @NotNull final int taskId,
                        final HttpServletRequest request) {
        LOG.info("getTask: taskId = {}", taskId);

        final HubService hubService = ActivityServerApplication.getHubService(extractHub(request));

        Optional<Project> project = hubService.getProject(extractProjectId(request));
        if (project.isEmpty()) {
            project = hubService.getArchivedProject(extractProjectId(request));
        }

        if (project.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final Optional<Task> task = hubService.getProjectService().getTaskById(project.get(), taskId);

        if (task.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Add the hateoas links before returning the task
        LOG.info("getTask: returning {}", task.get().getName());
        return ResponseEntity.ok(convertTaskToTaskResource(task.get(), request));
    }

    @ApiOperation(value="Updates a task", response=TaskResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully updated the task"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to update tasks"),
            @ApiResponse(code=404, message="The task could not be found")
    })
    @ResponseBody
    @PutMapping(value="/{taskId}")
    public ResponseEntity<TaskResource> editTaskDetails(@RequestBody @NotNull final Task newTaskDetails,
                                @PathVariable("taskId") @NotNull final int taskId,
                                @RequestParam(value = "destination", required = false) final int destinationProjectId,
                                @RequestHeader final HttpHeaders headers,
                                @RequestHeader(value = "referer", required = false) final String referer,
                                final HttpServletRequest request) {
        LOG.info("editTaskDetails: task = {}", taskId);

        if (isRequestValid(headers)) {
            if (taskId != newTaskDetails.getId()) {
                LOG.error("Path variable ({0}) does not match task ID ({1})", taskId, newTaskDetails.getId());
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            final Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProject(extractProjectId(request));
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Optional<Task> task = ActivityServerApplication.getHubService(extractHub(request)).getTaskById(project.get(), newTaskDetails.getId());
            if (task.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Optional<Project> targetProject = (destinationProjectId > 0) ?
                    ActivityServerApplication.getHubService(extractHub(request)).getProject(destinationProjectId) :
                    Optional.empty();

            final Task updatedTask = TaskManager.updateTask(
                    extractHub(request), project.get(), task.get(), newTaskDetails,
                    targetProject.isPresent() ? targetProject.get() : null
            );

            // Add the hateoas links before returning the task
            return ResponseEntity.ok(convertTaskToTaskResource(updatedTask, request));
        }

        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Creates a new task", response= TaskResource.class)
    @ApiResponses(value={
            @ApiResponse(code=201, message="Successfully created a task"),
            @ApiResponse(code=403, message="You are not allowed to create tasks"),
            @ApiResponse(code=404, message="Could not find the specified project")
    })
    @ResponseBody
    @PostMapping(value="/{taskId}")
    public ResponseEntity<TaskResource> createTask(@RequestBody @NotNull final Task newTaskDetails,
                                @PathVariable("taskId") @NotNull final int taskId,
                                @RequestHeader final HttpHeaders headers,
                                @RequestHeader(value = "referer", required = false) final String referer,
                                final HttpServletRequest request) {
        LOG.info("createTask: task = {}", taskId);

        if (isRequestValid(headers)) {
            final ProjectService projectService = ActivityServerApplication.getHubService(extractHub(request)).getProjectService();
            final Optional<Project> project = projectService.getProject(extractProjectId(request));
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final int newTaskId = projectService.getNextTaskId();
            newTaskDetails.setId(newTaskId);

            try {
                final Task createdTask = TaskManager.createTask(extractHub(request), project.get(), newTaskDetails);

                // Add the hateoas links before returning the task
                return new ResponseEntity<>(convertTaskToTaskResource(createdTask, request), HttpStatus.CREATED);

            } catch (IOException error) {
                LOG.error("Failed to create task", error);
            }
        }

        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Deletes a task", response=TaskResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully deleted the task"),
            @ApiResponse(code=403, message="You are not allowed to delete tasks"),
            @ApiResponse(code=404, message="Either the task or the project could not be found")
    })
    @DeleteMapping(value="/{taskId}")
    public ResponseEntity deleteTask(@PathVariable("taskId") @NotNull final int taskId,
                           @RequestHeader final HttpHeaders headers,
                           final HttpServletRequest request) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("deleteTask: task = {}", taskId));
        }

        if (isRequestValid(headers)) {
            final Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProject(extractProjectId(request));
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Optional<Task> task = ActivityServerApplication.getHubService(extractHub(request)).getTaskById(project.get(), taskId);
            if (task.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            try {
                TaskManager.deleteTask(extractHub(request), project.get(), task.get());
                return new ResponseEntity(HttpStatus.OK);
            } catch (IOException error) {
                LOG.error("Failed to delete task", error);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        LOG.info("Task deletion failed!");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Opens the content of a task for editing", response=void.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully opened the task for editing"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to open tasks for editing"),
            @ApiResponse(code=404, message="Either the task or the project could not be found")
    })
    @PutMapping(value="/{taskId}/open")
    public ResponseEntity openTaskForEditing(@PathVariable("taskId") @NotNull final int taskId,
                                             @RequestParam(value = "archived", required = false) final String archived,
                                             @RequestHeader final HttpHeaders headers,
                                             final HttpServletRequest request) {
        LOG.info("openTaskForEditing");

        if (isRequestValid(headers)) {
            final Optional<Project> project = (archived != null && archived.equalsIgnoreCase("Y")) ?
                    ActivityServerApplication.getHubService(extractHub(request)).getArchivedProject(extractProjectId(request))
                    :
                    ActivityServerApplication.getHubService(extractHub(request)).getProject(extractProjectId(request));
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Optional<Task> task = ActivityServerApplication.getHubService(extractHub(request)).getTaskById(project.get(), taskId);
            if (task.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (TaskManager.openTaskForEditing(extractHub(request), project.get(), task.get())) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        LOG.info("Page editing failed!");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Sets the status of a task to completed", response=void.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully completed the task"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to complete tasks"),
            @ApiResponse(code=404, message="Either the task or the project could not be found")
    })
    @PutMapping(value="/{taskId}/complete")
    public ResponseEntity completeTask(@PathVariable("taskId") @NotNull final int taskId,
                                       @RequestHeader final HttpHeaders headers,
                                       final HttpServletRequest request) {
        LOG.info("completeTask: {}", taskId);

        if (isRequestValid(headers)) {
            final Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProject(extractProjectId(request));
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Optional<Task> task = ActivityServerApplication.getHubService(extractHub(request)).getTaskById(project.get(), taskId);
            if (task.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (TaskManager.completeTask(extractHub(request), task.get())) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /////////////////////////
    // Hateoas helper methods
    /////////////////////////

    private Resources<TaskResource> addHateoasLinksToTaskResources(final String uriString, final Resources<TaskResource> resources) {
        if (!resources.hasLink("self")) {
            LOG.info("addHateoasLinksToTaskResources: uriString = {}", uriString);
            resources.add(new Link(uriString, "self"));
        }

        return resources;
    }

    private TaskResource convertTaskToTaskResource(final Task task, final HttpServletRequest request) {
        final TaskResource taskResource = new TaskResource(task);
        taskResource.add( getProjectLink(request) );
        taskResource.add( getTasksLink(request) );
        taskResource.add( getTaskLink(task.getId(), request) );

        // Add html content
        final String taskContents = ActivityServerApplication.getHubService(extractHub(request)).getProjectService().getTaskContents(task);
        taskResource.setHtmlContent(taskContents);

        return taskResource;
    }

    private Link getTaskLink(final int identifier, final HttpServletRequest request) {
        final String rawTaskUri = linkTo(methodOn(TaskController.class).getTask(identifier, request)).toUri().toString();
        return new Link(expandUri(rawTaskUri, request)).withSelfRel();
    }

    private Link getTasksLink(final HttpServletRequest request) {
        final String rawTasksUri = linkTo(methodOn(TaskController.class).getTasks(request)).toUri().toString();
        return new Link(expandUri(rawTasksUri, request)).withRel("tasks");
    }

    private Link getProjectLink(final HttpServletRequest request) {
        final String rawProjectUri = linkTo(methodOn(ProjectsController.class).getProject(extractProjectId(request), request)).toUri().toString();
        return new Link(expandUri(rawProjectUri, request)).withRel("projects");
    }

}
