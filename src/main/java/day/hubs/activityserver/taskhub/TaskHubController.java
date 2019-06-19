package day.hubs.activityserver.taskhub;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.controllers.HubController;
import day.hubs.activityserver.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/TaskHub")
public class TaskHubController extends HubController {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHubController.class);

    @GetMapping(value="/")
    @ResponseBody
    public List<Project> homeTaskHub(final HttpServletRequest request){
        LOG.info("homeTaskHub");
        return ActivityServerApplication.getHubService(extractHub(request)).getProjectSummary();
    }

}
