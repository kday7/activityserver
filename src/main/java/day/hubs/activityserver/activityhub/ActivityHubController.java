package day.hubs.activityserver.activityhub;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.activity.Activity;
import day.hubs.activityserver.controllers.HubController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/ActivityHub")
public class ActivityHubController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityHubController.class);

    public static final String ACTIVITYHUB_URL_PREFIX = "/ActivityHub/";


    @GetMapping(value="/")
    @ResponseBody
    public List<Activity> homeActivityHub(final HttpServletRequest request){
        LOG.info("homeActivityHub");
        return ActivityServerApplication.getHubService(extractHub(request)).getActivities();
    }

}
