package day.hubs.activityserver.notice;

import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.ResourceSupport;

@ApiModel(description="All details about a notice with hateoas links")
public class NoticeResource extends ResourceSupport {

    private int identifier;
    private String datePosted;
    private String detail;

    public NoticeResource(final Notice notice) {
        super();

        this.identifier = notice.getId();
        this.datePosted = notice.getDatePosted();
        this.detail = notice.getDetail();
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public String getDatePosted() {
        return this.datePosted;
    }

    public String getDetail() {
        return this.detail;
    }
}
