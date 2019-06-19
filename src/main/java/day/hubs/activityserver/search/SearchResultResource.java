package day.hubs.activityserver.search;

import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.ResourceSupport;

@ApiModel(description="All details about a search result with hateoas links")
public class SearchResultResource extends ResourceSupport {

    private String task;
    private String searchResultLink;
    private String project;
    private int projectId;
    private int taskId;

    public SearchResultResource(final SearchResult searchResult) {
        super();

        this.task = searchResult.getTask();
        this.searchResultLink = searchResult.getLink();
        this.project = searchResult.getProject();
        this.projectId = searchResult.getProjectId();
        this.taskId = searchResult.getTaskId();
    }

    public String getTask() {
        return this.task;
    }

    public String getSearchResultLink() {
        return this.searchResultLink;
    }

    public String getProject() {
        return this.project;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public int getTaskId() {
        return this.taskId;
    }
}
