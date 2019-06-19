package day.hubs.activityserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class HubController {

    private static final Logger LOG = LoggerFactory.getLogger(HubController.class);


    protected String extractHub(final HttpServletRequest request) {
        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        final String[] pathParts = StringUtils.tokenizeToStringArray(path, "/", false, true);
        return pathParts[0];
    }

    protected String extractProject(final HttpServletRequest request) {
        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        final String[] pathParts = StringUtils.tokenizeToStringArray(path, "/", false, true);
        return pathParts[2];
    }

    protected int extractProjectId(final HttpServletRequest request) {
        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        final String[] pathParts = StringUtils.tokenizeToStringArray(path, "/", false, true);
        return Integer.valueOf(pathParts[2]);
    }

    protected boolean isRequestValid(final HttpHeaders headers) {
        LOG.info("isRequestValid: Origin = {}", headers.getOrigin());
        return (null != headers.getOrigin()) ?
                headers.getOrigin().startsWith("http://localhost") ||
                headers.getOrigin().startsWith("http://rh-d-1039") :
                true;
    }

    protected String expandUri(String rawUri, final HttpServletRequest request) {
        return rawUri
                .replace("%7B.+Hub%7D", extractHub(request))
                .replace("%7B.+%7D", String.valueOf(extractProjectId(request)));
    }

    protected String expandUri(String rawUri, final int projectId, final HttpServletRequest request) {
        return rawUri
                .replace("%7B.+Hub%7D", extractHub(request))
                .replace("%7B.+%7D", String.valueOf(projectId));
    }

}
