package day.hubs.activityserver.controllers;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.domain.ApplicationProperty;
import day.hubs.activityserver.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    @GetMapping(value="/")
    public List<Project> home(){
        LOG.info("home");

        final List<Project> hubs = new ArrayList<>();
        hubs.add(new Project("Task Hub"));
        hubs.add(new Project("Info Hub"));
        hubs.add(new Project("Activity Hub"));

        return hubs;
    }

    @PostMapping(value="/ShutDown")
    public void shutDown() {
        LOG.info("shutDown");
        ActivityServerApplication.shutDown();
    }

    @GetMapping(value="/{hub}/settings")
    public List<ApplicationProperty> getSettings(@PathVariable("hub") final String hub) {
        LOG.info("getSettings: {}", hub);
        return ActivityServerApplication.getApplicationContext().getApplicationProperties(hub);
    }
}
