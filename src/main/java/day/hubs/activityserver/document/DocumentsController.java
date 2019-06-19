package day.hubs.activityserver.document;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@Api(value="/{Hub}/Documents", produces="application/json", consumes="application/json" )
@RestController
@RequestMapping("/{.+Hub}/Documents")
public class DocumentsController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentsController.class);


    @ApiOperation(value="Retrieve all documents", response=List.class, produces="application/json", consumes="application/json")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Successfully retrieved list of documents")
    })
    @GetMapping("/")
    @ResponseBody
    public List<Document> getDocuments(final HttpServletRequest request) {
        LOG.info("getDocuments");
        return ActivityServerApplication.getHubService(extractHub(request)).getDocuments();
    }

    /*
    @GetMapping(value="/openDocumentsFile")
    public ModelAndView openDocumentsFile(@RequestHeader final HttpHeaders headers,
                                          @RequestHeader(value = "referer", required = false) final String referer,
                                          final HttpServletRequest request) {
        LOG.info("openDocumentsFile");

        if (isRequestValid(headers)) {
            getHubService(extractHub(request)).openDocumentsFile();
        }

        return reloadPage(referer);
    }

    @PostMapping(value="/refresh")
    public ModelAndView refreshDocuments(@RequestHeader(value = "referer", required = false) final String referer,
                                         final HttpServletRequest request) {
        LOG.info("refreshDocuments");

        getHubService(extractHub(request)).reloadDocuments();

        return reloadPage(referer);
    }

    @PostMapping(value="/editDocument")
    public ModelAndView editDocument(@RequestParam(value = "document", required = false) final String document,
                                     @RequestParam(value = "type", required = false) final String type,
                                     @RequestHeader final HttpHeaders headers,
                                     @RequestHeader(value = "referer", required = false) final String referer,
                                     final HttpServletRequest request) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("editDocument: document = %s, type = %s", document, type));
        }

        if (isRequestValid(headers) && null != document) {
            getHubService(extractHub(request)).editDocument(document, type);
        }

        return reloadPage(referer);
    }

    @PostMapping(value="/openDocument")
    public ModelAndView openDocument(@RequestParam(value = "document", required = false) final String document,
                                     @RequestParam(value = "type", required = false) final String type,
                                     @RequestHeader final HttpHeaders headers,
                                     @RequestHeader(value = "referer", required = false) final String referer,
                                     final HttpServletRequest request) {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("openDocument: document = %s, type = %s", document, type));
        }

        if (isRequestValid(headers) && null != document) {
            getHubService(extractHub(request)).openDocument(document, type);
        }

        return reloadPage(referer);
    }
    */
}
