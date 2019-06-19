package day.hubs.activityserver.notice;

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
@Api(value="/{Hub}/Notices", produces="application/json", consumes="application/json" )
@RestController
@RequestMapping("/{.+Hub}/Notices")
public class NoticesController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(NoticesController.class);


    @ApiOperation(value="Retrieve all notices", response=List.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of notices")
    })
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Resources<NoticeResource>> getNotices(final HttpServletRequest request) {
        LOG.info("getNotices");

        final List<NoticeResource> noticeResources = ActivityServerApplication.getHubService(extractHub(request)).getNotices().stream()
                .map(notice -> convertNoticeToNoticeResource(notice, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToNoticeResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(noticeResources)));
    }

    @ApiOperation(value="Creates a new notice", response=NoticeResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=201, message="Successfully created a new notice"),
            @ApiResponse(code=403, message="You are not allowed to create notices")
    })
    @PostMapping(value="/")
    @ResponseBody
    public ResponseEntity<NoticeResource> createNotice(@RequestBody @NotNull final Notice newNoticeDetails,
                                               @RequestHeader final HttpHeaders headers,
                                               final HttpServletRequest request) {
        LOG.info("createNotice: {}", newNoticeDetails.getId());
        if (isRequestValid(headers)) {
            final Notice createdNotice = ActivityServerApplication.getHubService(extractHub(request)).createNotice(newNoticeDetails);
            final NoticeResource noticeResource = convertNoticeToNoticeResource(createdNotice, request);

            return new ResponseEntity<>(noticeResource, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Updates a notice", response=NoticeResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully updated the notice"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to update notices"),
            @ApiResponse(code=404, message="The notice could not be found")
    })
    @ResponseBody
    @PutMapping(value="/{noticeId}")
    public ResponseEntity<NoticeResource> updateNotice(@RequestBody @NotNull final Notice newNoticeDetails,
                                               @PathVariable("noticeId") @NotNull final int noticeId,
                                               @RequestHeader final HttpHeaders headers,
                                               final HttpServletRequest request) {
        LOG.info("updateNotice: notice = {}", noticeId);

        if (isRequestValid(headers)) {
            if (noticeId != newNoticeDetails.getId()) {
                LOG.error("Path variable ({}) does not match notice ID ({})", noticeId, newNoticeDetails.getId());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            final Optional<Notice> notice = ActivityServerApplication.getHubService(extractHub(request)).getNotice(noticeId);
            if (notice.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            final Notice updatedNotice = ActivityServerApplication.getHubService(extractHub(request)).updateNotice(
                    extractHub(request), notice.get(), newNoticeDetails);

            return ResponseEntity.ok(convertNoticeToNoticeResource(updatedNotice, request));
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value="Find a notice", response=NoticeResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully found the notice"),
            @ApiResponse(code=404, message="The notice could not be found")
    })
    @GetMapping(value="/{noticeId}")
    @ResponseBody
    public ResponseEntity<NoticeResource> getNotice(@PathVariable("noticeId") @NotNull final int noticeId,
                                            final HttpServletRequest request) {
        LOG.info("getNotice: notice = {}", noticeId);
        final Optional<Notice> notice = ActivityServerApplication.getHubService(extractHub(request)).getNotice(noticeId);
        if (notice.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(convertNoticeToNoticeResource(notice.get(), request));
    }

    @ApiOperation(value="Deletes a notice", response=NoticeResource.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully deleted the notice"),
            @ApiResponse(code=403, message="You are not allowed to delete notices"),
            @ApiResponse(code=404, message="The notice could not be found")
    })
    @DeleteMapping(value="/{noticeId}")
    public ResponseEntity deleteNotice(@PathVariable("noticeId") @NotNull final int noticeId,
                                         @RequestHeader final HttpHeaders headers,
                                         @RequestHeader(value = "referer", required = false) final String referer,
                                         final HttpServletRequest request) {
        LOG.info("deleteNotice: notice = {}", noticeId);

        if (isRequestValid(headers)) {
            final Optional<Notice> notice = ActivityServerApplication.getHubService(extractHub(request)).getNotice(noticeId);
            if (notice.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            if (ActivityServerApplication.getHubService(extractHub(request)).deleteNotice(noticeId)) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value="Opens the notices file", response=void.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully opened the notices file"),
            @ApiResponse(code=400, message="Request details were invalid"),
            @ApiResponse(code=403, message="You are not allowed to open the notices file"),
    })
    @PostMapping(value="/open")
    public ResponseEntity openNoticesFile(@RequestHeader final HttpHeaders headers,
                                          @RequestHeader(value = "referer", required = false) final String referer,
                                          final HttpServletRequest request) {
        LOG.info("openNoticesFile");

        if (isRequestValid(headers)) {
            if (ActivityServerApplication.getHubService(extractHub(request)).openNoticesFile()) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value="Refreshes the list of notices", response=List.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully refreshed the list of notices"),
    })
    @PostMapping(value="/refresh")
    @ResponseBody
    public ResponseEntity<Resources<NoticeResource>> refreshNotices(final HttpServletRequest request) {
        LOG.info("refreshNotices");

        final List<NoticeResource> noticeResources = ActivityServerApplication.getHubService(extractHub(request)).reloadNotices().stream()
                .map(notice -> convertNoticeToNoticeResource(notice, request))
                .collect(Collectors.toList());

        return ResponseEntity.ok(addHateoasLinksToNoticeResources(
                ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString(),
                new Resources<>(noticeResources)));
    }

    /////////////////////////
    // Hateoas helper methods
    /////////////////////////

    private Resources<NoticeResource> addHateoasLinksToNoticeResources(final String uriString, final Resources<NoticeResource> resources) {
        if (!resources.hasLink("self")) {
            LOG.info("addHateoasLinksToNoticeResources: uriString = {}", uriString);
            resources.add(new Link(uriString, "self"));
        }

        return resources;
    }

    private NoticeResource convertNoticeToNoticeResource(final Notice notice, final HttpServletRequest request) {
        final NoticeResource noticeResource = new NoticeResource(notice);
        noticeResource.add( getNoticesLink(request) );
        noticeResource.add( getNoticeLink(notice.getId(), request) );

        return noticeResource;
    }

    private Link getNoticeLink(final int identifier, final HttpServletRequest request) {
        final String rawTaskUri = linkTo(methodOn(NoticesController.class).getNotice(identifier, request)).toUri().toString();
        return new Link(expandUri(rawTaskUri, request)).withSelfRel();
    }

    private Link getNoticesLink(final HttpServletRequest request) {
        final String rawTasksUri = linkTo(methodOn(NoticesController.class).getNotices(request)).toUri().toString();
        return new Link(expandUri(rawTasksUri, request)).withRel("notices");
    }

}
