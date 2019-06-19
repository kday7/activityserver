package day.hubs.activityserver.project;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
import day.hubs.activityserver.task.Task;
import day.hubs.activityserver.task.TaskController;
import day.hubs.activityserver.task.TaskResource;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@CrossOrigin
@Api(value="/InfoHub/Projects", produces="application/json", consumes="application/json", tags="projects")
@RestController
@RequestMapping("/{.+Hub}/Projects")
public class ProjectsController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectsController.class);

    @ApiOperation(value="Retrieve all projects", response=List.class, produces="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of projects")
    })
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Resources<ProjectResource>> getProjects(final HttpServletRequest request) {
        LOG.info("getProjects");
        final List<Project> allProjects = ActivityServerApplication.getHubService(extractHub(request)).getProjectService().getProjects();

        final List<ProjectResource> projectResources = allProjects.stream()
                .map(project -> convertProjectToProjectResource(project, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToProjectResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(projectResources)));
    }

    @ApiOperation(value="Creates a new project", response=ProjectResource.class)
    @ApiResponses(value={
            @ApiResponse(code=201, message="Successfully created a project"),
            @ApiResponse(code=403, message="You are not allowed to create projects")
    })
    @PostMapping(value="/")
    @ResponseBody
    public ResponseEntity<ProjectResource> createProject(@RequestBody @NotNull final Project newProjectDetails,
                                 @RequestHeader final HttpHeaders headers,
                                 final HttpServletRequest request) {
        LOG.info("createProject: project");
        if (isRequestValid(headers)) {
            final Project createdProject = ActivityServerApplication.getHubService(extractHub(request)).getProjectService().createProject(newProjectDetails);
            final ProjectResource projectResource = convertProjectToProjectResource(createdProject, request);

            return new ResponseEntity<>(projectResource, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Find a project", response=ProjectResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully found the project"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @GetMapping(value="/{projectId}")
    @ResponseBody
    public ResponseEntity<ProjectResource> getProject(@PathVariable("projectId") @NotNull final int projectId,
                              final HttpServletRequest request) {
        LOG.info("getProject: project = {}", projectId);
        Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProject(projectId);
        if (project.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(convertProjectToProjectResource(project.get(), request));
    }

    @ApiOperation(value="Updates a project", response=ProjectResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully updated the project"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to update projects"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @ResponseBody
    @PutMapping(value="/{projectId}")
    public ResponseEntity<ProjectResource> updateProject(@RequestBody @NotNull final Project newProjectDetails,
                                                   @PathVariable("projectId") @NotNull final int projectId,
                                                   @RequestHeader final HttpHeaders headers,
                                                   @RequestHeader(value = "referer", required = false) final String referer,
                                                   final HttpServletRequest request) {
        LOG.info("editProjectDetails: project = {}", projectId);

        if (isRequestValid(headers)) {
            if (projectId != newProjectDetails.getId()) {
                LOG.error("Path variable ({}) does not match project ID ({})", projectId, newProjectDetails.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            final Optional<Project> project = ActivityServerApplication.getHubService(extractHub(request)).getProject(projectId);
            if (project.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Project updatedProject = ActivityServerApplication.getHubService(extractHub(request)).updateProject(
                    extractHub(request), project.get(), newProjectDetails);

            return ResponseEntity.ok(convertProjectToProjectResource(updatedProject, request));
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Deletes a project", response=ProjectResource.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully deleted the project"),
            @ApiResponse(code=403, message="You are not allowed to delete projects"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @DeleteMapping(value="/{projectId}")
    public ResponseEntity deleteProject(@PathVariable("projectId") @NotNull final int projectId,
                                     @RequestHeader final HttpHeaders headers,
                                     @RequestHeader(value = "referer", required = false) final String referer,
                                     final HttpServletRequest request) {
        LOG.info(String.format("deleteProject: project = {}", projectId));

        if (isRequestValid(headers)) {
            if (!ActivityServerApplication.getHubService(extractHub(request)).getProjectService().projectExists(projectId)) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (ActivityServerApplication.getHubService(extractHub(request)).getProjectService().deleteProject(projectId)) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        LOG.info("Project deletion failed!");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    //////////////////
    // Project Summary
    //////////////////

    @ApiOperation(value="Retrieve all project summaries", response=List.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of project summaries")
    })
    @GetMapping(value="/summary")
    @ResponseBody
    public ResponseEntity<Resources<ProjectResource>> getProjectSummary(final HttpServletRequest request){
        LOG.info("getProjectSummary");
        final List<Project> projectSummary = ActivityServerApplication.getHubService(extractHub(request)).getProjectSummary();

        final List<ProjectResource> projectResources = projectSummary.stream()
                .map(project -> convertProjectToProjectResource(project, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToProjectResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(projectResources)));
    }

    //////////
    // Archive
    //////////

    @ApiOperation(value="Retrieve archived projects", response=List.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of archived projects")
    })
    @GetMapping(value="/archive")
    @ResponseBody
    public ResponseEntity<Resources<ProjectResource>> getArchivedProjects(final HttpServletRequest request) {
        LOG.info("getArchivedProjects");
        final List<Project> archivedProjects = ActivityServerApplication.getHubService(extractHub(request)).getArchivedProjects();

        final List<ProjectResource> projectResources = archivedProjects.stream()
                .map(project -> convertProjectToProjectResource(project, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToProjectResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(projectResources)));
    }

    @ApiOperation(value="Archive a project", response=List.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of archived projects"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to archive projects"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @PostMapping(value="/{projectId}/archive")
    public ResponseEntity archiveProject(@PathVariable("projectId") @NotNull final String projectId,
                                         @RequestHeader final HttpHeaders headers,
                                         final HttpServletRequest request) {
        LOG.info("archiveProject: project = {}", projectId);

        if (isRequestValid(headers)) {
            if (!ActivityServerApplication.getHubService(extractHub(request)).getProjectService().projectExists(Integer.valueOf(projectId))) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (ActivityServerApplication.getHubService(extractHub(request)).archiveProject(Integer.valueOf(projectId))) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Restore a project from the archive", response=void.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully restored the project"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to restore projects from the archive"),
            @ApiResponse(code=404, message="The project could not be found")
    })
    @PostMapping(value="/{projectId}/restore")
    public ResponseEntity restoreProject(@PathVariable("projectId") @NotNull final String projectId,
                                         @RequestHeader final HttpHeaders headers,
                                         final HttpServletRequest request) {
        LOG.info("restoreProject: project = {}", projectId);

        if (isRequestValid(headers)) {
            if (ActivityServerApplication.getHubService(extractHub(request)).getArchivedProject(Integer.valueOf(projectId)).isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (ActivityServerApplication.getHubService(extractHub(request)).restoreProject(Integer.valueOf(projectId))) {
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

    private Resources<ProjectResource> addHateoasLinksToProjectResources(final String uriString, final Resources<ProjectResource> resources) {
        if (!resources.hasLink("self")) {
            LOG.info("addHateoasLinksToProjectResources: uriString = {}", uriString);
            resources.add(new Link(uriString, "self"));
        }

        return resources;
    }

    private ProjectResource convertProjectToProjectResource(final Project project, final HttpServletRequest request) {
        final ProjectResource projectResource = new ProjectResource(project);
        projectResource.add( getProjectsLink(project.getId(), request) );
        projectResource.add( getProjectLink(project.getId(), "self", request) );

        List<TaskResource> tasks = project.getTasks().stream()
                .map(task -> convertTaskToTaskResource(task, project.getId(), request))
                .collect(Collectors.toList());
        projectResource.setTasks(tasks);

        return projectResource;
    }

    private TaskResource convertTaskToTaskResource(final Task task, final int projectId, final HttpServletRequest request) {
        final TaskResource taskResource = new TaskResource(task);
        taskResource.add( getProjectLink(projectId, "projects", request) );
        taskResource.add( getTasksLink(projectId, request) );
        taskResource.add( getTaskLink(task.getId(), projectId, request) );
        return taskResource;
    }

    private Link getTaskLink(final int identifier, final int projectId, final HttpServletRequest request) {
        final String rawTaskUri = linkTo(methodOn(TaskController.class).getTask(identifier, request)).toUri().toString();
        return new Link(expandUri(rawTaskUri, projectId, request)).withSelfRel();
    }

    private Link getTasksLink(final int projectId, final HttpServletRequest request) {
        final String rawTasksUri = linkTo(methodOn(TaskController.class).getTasks(request)).toUri().toString();
        return new Link(expandUri(rawTasksUri, projectId, request)).withRel("tasks");
    }

    private Link getProjectLink(final int identifier, final String rel, final HttpServletRequest request) {
        final String rawProjectUri = linkTo(methodOn(ProjectsController.class).getProject(identifier, request)).toUri().toString();
        return new Link(expandUri(rawProjectUri, identifier, request)).withRel(rel);
    }

    private Link getProjectsLink(final int projectId, final HttpServletRequest request) {
        final String rawProjectsUri = linkTo(methodOn(ProjectsController.class).getProjects(request)).toUri().toString();
        return new Link(expandUri(rawProjectsUri, projectId, request)).withRel("projects");
    }

}
