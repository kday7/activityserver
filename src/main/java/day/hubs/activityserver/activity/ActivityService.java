package day.hubs.activityserver.activity;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.document.DocumentUtilities;
import day.hubs.activityserver.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityService {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityService.class);

    protected String hub;

    private ActivityList activityList;
    private String activitiesFile;
    private final List<Activity> activities = new ArrayList<>();

    @Autowired
    public ActivityService(final String activitiesFile, final String hub) {
        super();
        this.activitiesFile = activitiesFile;
        this.hub = hub;
        initialise();
    }

    private void initialise() {
        if (StringUtils.isEmpty(this.activitiesFile)) {
            return;
        }

        final Optional<Object> activitiesList = JsonService.readJsonDataFile(new File(this.activitiesFile), ActivityList.class);
        if (activitiesList.isPresent()) {
            this.activityList = (ActivityList) activitiesList.get();
            this.activities.addAll(this.activityList.getHubActivities());
        }
    }

    public int getNextActivityId() {
        return this.activityList.getNextActivityId();
    }

    public void addActivity(final Activity newActivity) {
        if (newActivity.getId() == -1) {
            newActivity.setId(getNextActivityId());
        }

        this.activities.add(newActivity);
        // this.activityList.addActivity(newActivity);
    }

    public Activity createActivity(final Activity newActivityDetails) {
        final Activity newActivity = new Activity();
        newActivity.setId(this.getNextActivityId());
        newActivity.setName(newActivityDetails.getName());
        newActivity.setLink(newActivityDetails.getLink());
        newActivity.setIcon(newActivityDetails.getIcon());

        addActivity(newActivity);

        saveActivities();

        return newActivity;
    }

    public Activity updateActivity(final String hub, final Activity updateActivity, final Activity newActivityDetails) {
        LOG.debug("ActivityService: Updating activity: {}", updateActivity.getName());

        updateActivity.setName(newActivityDetails.getName());
        updateActivity.setLink(newActivityDetails.getLink());
        updateActivity.setIcon(newActivityDetails.getIcon());

        saveActivities();

        return updateActivity;
    }

    public boolean deleteActivity(final int activityId) {
        // Remove activity from list of activities
        final Optional<Activity> activity = getActivity(activityId);
        if (activity.isEmpty()) {
            return false;
        }
        this.activities.remove(activity);

        saveActivities();

        return true;
    }

    public List<Activity> reloadActivities() {
        this.activityList.clearActivities();
        this.activities.clear();
        initialise();
        return this.activities;
    }

    public boolean openActivitiesFile() {
        final File file = new File(this.activitiesFile);
        if (!file.exists()) {
            return false;
        }
        DocumentUtilities.openCodeFile(file);
        return true;
    }

    public List<Activity> getActivities() {
        return this.activities;
    }

    public Activity getActivityByName(final String name) {
        return this.activities.stream().filter(activity -> name.equals(activity.getName()))
                .findFirst().get();
    }

    public Optional<Activity> getActivity(final int id) {
        return this.activities.stream().filter(activity -> id == activity.getId())
                .findFirst();
    }

    public void saveActivities() {
        this.activityList.clearActivities();
        this.activityList.setHubActivities(this.activities);
        JsonService.writeJsonDataFile(ActivityServerApplication.getApplicationContext().getActivitiesFile(this.hub), this.activityList);
    }
}
