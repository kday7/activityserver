package day.hubs.activityserver.infohub;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.activity.Activity;
import day.hubs.activityserver.controllers.HubController;
import day.hubs.activityserver.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/InfoHub")
public class InfoHubController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(InfoHubController.class);

    @GetMapping(value="/")
    @ResponseBody
    public List<Activity> homeTaskHub(final HttpServletRequest request){
        LOG.info("homeTaskHub");
        return ActivityServerApplication.getHubService(extractHub(request)).getActivities();
    }

}
