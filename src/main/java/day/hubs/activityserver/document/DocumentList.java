package day.hubs.activityserver.document;

import java.util.ArrayList;
import java.util.List;

public class DocumentList {

    private List<Document> hubDocuments = new ArrayList<>();

    public List<Document> getHubDocuments() {
        return this.hubDocuments;
    }

    public void setHubProjects(final List<Document> documents) {
        this.hubDocuments = documents;
    }

    public void clearDocuments() {
        this.hubDocuments.clear();
    }}
