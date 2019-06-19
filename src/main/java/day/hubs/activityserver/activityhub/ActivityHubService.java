package day.hubs.activityserver.activityhub;

import day.hubs.activityserver.services.BaseHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static day.hubs.activityserver.Constants.ACTIVITY_HUB;

@Service
public class ActivityHubService extends BaseHubService {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityHubService.class);


    @Autowired
    public ActivityHubService(@Value("${projects.file}") final String projectsFile,
                              @Value("${archived.projects.file}") final String archivedProjectsFile,
                              @Value("${activities.file}") final String activitiesFile,
                              @Value("${notices.file}") final String noticesFile,
                              @Value("${documents.file}") final String documentsFile) {
        super(projectsFile, archivedProjectsFile, activitiesFile, noticesFile, documentsFile, ACTIVITY_HUB);
    }


}
