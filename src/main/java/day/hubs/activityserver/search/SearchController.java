package day.hubs.activityserver.search;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
import day.hubs.activityserver.task.TaskController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@CrossOrigin
@Api(value="/{Hub}/Search", produces="application/json", consumes="application/json" )
@RestController
@RequestMapping("/{.+Hub}/Search")
public class SearchController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);


    @ApiOperation(value="Searches for tasks with the specified text", response=List.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully searched for the text")
    })
    @GetMapping("")
    @ResponseBody
    public ResponseEntity<Resources<SearchResultResource>> search(
                    @RequestParam(value = "text", required = true) @NotNull final String searchText,
                    final HttpServletRequest request) {
        LOG.info("search");

        final List<SearchResultResource> searchResultResources = ActivityServerApplication.getHubService(extractHub(request)).search(searchText).stream()
                .map(result -> convertSearchResultToSearchResultResource(result, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToSearchResultResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(searchResultResources)));
    }

    /////////////////////////
    // Hateoas helper methods
    /////////////////////////

    private Resources<SearchResultResource> addHateoasLinksToSearchResultResources(
                final String uriString, final Resources<SearchResultResource> resources) {
        if (!resources.hasLink("self")) {
            LOG.info("addHateoasLinksToActivityResources: uriString = {}", uriString);
            resources.add(new Link(uriString, "self"));
        }

        return resources;
    }

    private SearchResultResource convertSearchResultToSearchResultResource(
            final SearchResult searchResult, final HttpServletRequest request) {
        final SearchResultResource searchResultResource = new SearchResultResource(searchResult);
        searchResultResource.add( getSearchResultsLink(searchResult.getSearchText(), request) );
        searchResultResource.add( getTaskLink(searchResult.getTaskId(), request) );

        return searchResultResource;
    }

    private Link getTaskLink(final int taskId, final HttpServletRequest request) {
        final String rawTaskUri = linkTo(methodOn(TaskController.class).getTask(taskId, request)).toUri().toString();
        return new Link(expandUri(rawTaskUri, request)).withRel("tasks");
    }

    private Link getSearchResultsLink(final String searchText, final HttpServletRequest request) {
        final String rawTasksUri = linkTo(methodOn(SearchController.class).search(searchText, request)).toUri().toString();
        return new Link(expandUri(rawTasksUri, request)).withRel("activities");
    }

}
