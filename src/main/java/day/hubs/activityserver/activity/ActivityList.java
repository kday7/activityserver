package day.hubs.activityserver.activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityList {

    private List<Activity> hubActivities = new ArrayList<>();

    private int nextActivityId;

    public int getNextActivityId() {
        return this.nextActivityId++;
    }

    public List<Activity> getHubActivities() {
        return this.hubActivities;
    }

    public void setHubActivities(final List<Activity> activities) {
        this.hubActivities = new ArrayList<>(activities);
    }

    public void addActivity(final Activity newActivity) {
        this.hubActivities.add(newActivity);
    }

    public void removeActivity(final Activity activity) {
        this.hubActivities.remove(activity);
    }

    public void clearActivities() {
        this.hubActivities.clear();
    }
}
