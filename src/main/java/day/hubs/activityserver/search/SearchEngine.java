package day.hubs.activityserver.search;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchEngine {

    private static final Logger LOG = LoggerFactory.getLogger(SearchEngine.class);

    protected String hub;

    private final List<SearchableFile> allSearchableFiles = new ArrayList<>();

    private String templateStore;

    private String searchText;
    private SearchResults searchResults = new SearchResults();

    @Autowired
    public SearchEngine(final String hub) {
        super();
        this.hub = hub;
    }

    ////////////////////////////
    // Maintain Searchable Files
    ////////////////////////////

    public void addSearchableFile(final SearchableFile searchableFile) {
        this.allSearchableFiles.add(searchableFile);
    }

    public void removeSearchableFile(final String searchableDataFile) {
        final List<SearchableFile> files = this.allSearchableFiles.stream()
                .filter(searchable -> searchableDataFile.equals(searchable.getDataFile()))
                .collect(Collectors.toList());

        this.allSearchableFiles.remove(files);
    }

    public void updateSearchableFiles(final String oldSearchableDataFile, final Project searchableProject, final Task searchableTask) {
        final List<SearchableFile> files = this.allSearchableFiles.stream()
                .filter(searchable -> searchable.getDataFile().startsWith(oldSearchableDataFile + '/'))
                .collect(Collectors.toList());

        if (!files.isEmpty()) {
            this.allSearchableFiles.remove(files);

            for (final SearchableFile file : files) {
                //file.setDataFile(StringUtils.replace(file.getDataFile(), oldSearchableDataFile, searchableProject.getView()));
                file.setDataFile(searchableTask.getLocation() + ".html");
                file.setProjectName(searchableProject.getName());
                file.setProjectId(searchableProject.getId());
                file.setTask(searchableTask.getName());
                file.setTaskId(searchableTask.getId());
            }

            this.allSearchableFiles.addAll(files);
        }
    }

    //////////////////////
    // Search
    //////////////////////

    public SearchResults search(final String searchText) {
        this.searchText = searchText;
        this.searchResults = new SearchResults();
        this.searchResults.setSearchText(searchText);

        if (null != searchText) {
            findText();
        }

        return getResults();
    }

    public SearchResults getResults() {
        return this.searchResults;
    }

    private void findText() {
        for (final SearchableFile searchableFile : this.allSearchableFiles) {
            final String dataFile = searchableFile.getDataFile();

            final boolean found = isSearchTextInFile(dataFile);

            if (found) {
                this.searchResults.add(
                        new SearchResult(
                                this.searchText,
                                searchableFile.getProjectName(),
                                searchableFile.getProjectId(),
                                searchableFile.getTask(),
                                searchableFile.getTaskId()));
            }
        }
    }

    private boolean isSearchTextInFile(final String dataFile) {
        final File searchFile = new File(getTemplateStore() + dataFile);
        if (!searchFile.exists()) {
            LOG.warn("Task content file not found: {}", dataFile);
            return false;
        }

        try (final InputStream inputStream = new FileInputStream( getTemplateStore() + dataFile);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {

            final String contents = new BufferedReader(inputStreamReader).lines().collect(Collectors.joining("\n"));
            return contents.toLowerCase(Locale.getDefault()).contains(this.searchText.toLowerCase(Locale.getDefault()));
        }
        catch (final Exception error) {
            error.printStackTrace();
            ActivityServerApplication.getApplicationContext().setLastException(error);
            return false;
        }
    }

    private String getTemplateStore() {
        if (StringUtils.isEmpty(this.templateStore)) {
            this.templateStore = ActivityServerApplication.getApplicationContext().getTemplateStore(this.hub);
        }
        return this.templateStore;
    }
}
