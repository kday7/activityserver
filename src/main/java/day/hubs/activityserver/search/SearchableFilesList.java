package day.hubs.activityserver.search;

import java.util.List;

public class SearchableFilesList {
    private List<SearchableFile> hubSearchableDataFiles;

    public void addSearchable(final SearchableFile searchableDataFile) {
        this.hubSearchableDataFiles.add(searchableDataFile);
    }

    public List<SearchableFile> getSearchables() {
        return this.hubSearchableDataFiles;
    }
}
