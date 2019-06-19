package day.hubs.activityserver.infohub;

import day.hubs.activityserver.services.BaseHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static day.hubs.activityserver.Constants.INFO_HUB;

@Service
public class InfoHubService extends BaseHubService {

    private static final Logger LOG = LoggerFactory.getLogger(InfoHubService.class);


    @Autowired
    public InfoHubService(@Value("${projects.file}") final String projectsFile,
                          @Value("${archived.projects.file}") final String archivedProjectsFile,
                          @Value("${activities.file}") final String activitiesFile,
                          @Value("${notices.file}") final String noticesFile,
                          @Value("${documents.file}") final String documentsFile) {
        super(projectsFile, archivedProjectsFile, activitiesFile, noticesFile, documentsFile, INFO_HUB);
    }


}
