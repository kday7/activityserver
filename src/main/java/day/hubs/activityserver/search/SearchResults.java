package day.hubs.activityserver.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {

    private String searchText;

    private List<SearchResult> results = new ArrayList<>();


    public void add(final SearchResult result) {
        this.results.add(result);
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void setSearchText(final String searchText) {
        this.searchText = searchText;
    }

    public List<SearchResult> getResults() {
        return this.results;
    }

    public void setResults(final List<SearchResult> results) {
        this.results = results;
    }
}
