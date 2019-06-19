package day.hubs.activityserver.activity;

import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.ResourceSupport;

@ApiModel(description="All details about an activity with hateoas links")
public class ActivityResource extends ResourceSupport {

    private int identifier;
    private String name;
    private String activityLink;
    private String icon;

    public ActivityResource(final Activity activity) {
        super();

        this.identifier = activity.getId();
        this.name = activity.getName();
        this.activityLink = activity.getLink();
        this.icon = activity.getIcon();
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public String getName() {
        return this.name;
    }

    public String getActivityLink() {
        return this.activityLink;
    }

    public String getIcon() {
        return this.icon;
    }
}
