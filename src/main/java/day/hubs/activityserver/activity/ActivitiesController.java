package day.hubs.activityserver.activity;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
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
@Api(value="/{Hub}/Activities", produces="application/json", consumes="application/json" )
@RestController
@RequestMapping("/{.+Hub}/Activities")
public class ActivitiesController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiesController.class);


    @ApiOperation(value="Retrieve all activities", response=List.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of activities")
    })
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Resources<ActivityResource>> getActivities(final HttpServletRequest request) {
        LOG.info("getActivities");

        final List<ActivityResource> activityResources = ActivityServerApplication.getHubService(extractHub(request)).getActivities().stream()
                .map(activity -> convertActivityToActivityResource(activity, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToActivityResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(activityResources)));
    }

    @ApiOperation(value="Creates a new project", response=ActivityResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=201, message="Successfully created a new activity"),
            @ApiResponse(code=403, message="You are not allowed to create activities")
    })
    @PostMapping(value="/")
    @ResponseBody
    public ResponseEntity<ActivityResource> createActivity(@RequestBody @NotNull final Activity newActivityDetails,
                                                 @RequestHeader final HttpHeaders headers,
                                                 final HttpServletRequest request) {
        LOG.info("createActivity: {}", newActivityDetails.getName());
        if (isRequestValid(headers)) {
            final Activity createdActivity = ActivityServerApplication.getHubService(extractHub(request)).createActivity(newActivityDetails);
            final ActivityResource activityResource = convertActivityToActivityResource(createdActivity, request);

            return new ResponseEntity<>(activityResource, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Updates an activity", response=ActivityResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully updated the activity"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to update activities"),
            @ApiResponse(code=404, message="The activity could not be found")
    })
    @ResponseBody
    @PutMapping(value="/{activityId}")
    public ResponseEntity<ActivityResource> updateActivity(@RequestBody @NotNull final Activity newActivityDetails,
                                @PathVariable("activityId") @NotNull final int activityId,
                                @RequestHeader final HttpHeaders headers,
                                final HttpServletRequest request) {
        LOG.info("updateActivity: activity = {}", activityId);

        if (isRequestValid(headers)) {
            if (activityId != newActivityDetails.getId()) {
                LOG.error("Path variable ({}) does not match activity ID ({})", activityId, newActivityDetails.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            final Optional<Activity> activity = ActivityServerApplication.getHubService(extractHub(request)).getActivity(activityId);
            if (activity.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Activity updatedActivity = ActivityServerApplication.getHubService(extractHub(request)).updateActivity(
                    extractHub(request), activity.get(), newActivityDetails);

            return ResponseEntity.ok(convertActivityToActivityResource(updatedActivity, request));
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Find an activity", response=ActivityResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully found the activity"),
            @ApiResponse(code=404, message="The activity could not be found")
    })
    @GetMapping(value="/{activityId}")
    @ResponseBody
    public ResponseEntity<ActivityResource> getActivity(@PathVariable("activityId") @NotNull final int activityId,
                                                final HttpServletRequest request) {
        LOG.info("getActivity: activity = {}", activityId);
        Optional<Activity> activity = ActivityServerApplication.getHubService(extractHub(request)).getActivity(activityId);
        if (activity.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(convertActivityToActivityResource(activity.get(), request));
    }

    @ApiOperation(value="Deletes an activity", response=ActivityResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully deleted the activity"),
            @ApiResponse(code=403, message="You are not allowed to delete activities"),
            @ApiResponse(code=404, message="The activity could not be found")
    })
    @DeleteMapping(value="/{activityId}")
    public ResponseEntity deleteActivity(@PathVariable("activityId") @NotNull final int activityId,
                                     @RequestHeader final HttpHeaders headers,
                                     final HttpServletRequest request) {
        LOG.info("deleteActivity: activity = {}", activityId);

        if (isRequestValid(headers)) {
            final Optional<Activity> activity = ActivityServerApplication.getHubService(extractHub(request)).getActivity(activityId);
            if (activity.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (ActivityServerApplication.getHubService(extractHub(request)).deleteActivity(activityId)) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }


    @ApiOperation(value="Opens the activities file", response=void.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully opened the activities file"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to open the activities file"),
    })
    @PostMapping(value="/open")
    public ResponseEntity openActivitiesFile(@RequestHeader final HttpHeaders headers,
                                             @RequestHeader(value = "referer", required = false) final String referer,
                                             final HttpServletRequest request) {
        LOG.info("openActivitiesFile");

        if (isRequestValid(headers)) {
            if (ActivityServerApplication.getHubService(extractHub(request)).openActivitiesFile()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Refreshes the list of activities", response=List.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully refreshed the list of activities"),
    })
    @PostMapping(value="/refresh")
    @ResponseBody
    public ResponseEntity<Resources<ActivityResource>> refreshActivities(final HttpServletRequest request) {
        LOG.info("refreshActivities");

        final List<ActivityResource> activityResources = ActivityServerApplication.getHubService(extractHub(request)).reloadActivities().stream()
                .map(activity -> convertActivityToActivityResource(activity, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToActivityResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(activityResources)));
    }

    /////////////////////////
    // Hateoas helper methods
    /////////////////////////

    private Resources<ActivityResource> addHateoasLinksToActivityResources(final String uriString, final Resources<ActivityResource> resources) {
        if (!resources.hasLink("self")) {
            LOG.info("addHateoasLinksToActivityResources: uriString = {}", uriString);
            resources.add(new Link(uriString, "self"));
        }

        return resources;
    }

    private ActivityResource convertActivityToActivityResource(final Activity activity, final HttpServletRequest request) {
        final ActivityResource activityResource = new ActivityResource(activity);
        activityResource.add( getActivitiesLink(request) );
        activityResource.add( getActivityLink(activity.getId(), request) );

        return activityResource;
    }

    private Link getActivityLink(final int identifier, final HttpServletRequest request) {
        final String rawTaskUri = linkTo(methodOn(ActivitiesController.class).getActivity(identifier, request)).toUri().toString();
        return new Link(expandUri(rawTaskUri, request)).withSelfRel();
    }

    private Link getActivitiesLink(final HttpServletRequest request) {
        final String rawTasksUri = linkTo(methodOn(ActivitiesController.class).getActivities(request)).toUri().toString();
        return new Link(expandUri(rawTasksUri, request)).withRel("activities");
    }

}
