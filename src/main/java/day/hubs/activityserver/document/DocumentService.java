package day.hubs.activityserver.document;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.services.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DocumentService {

    private final List<Document> documents = new ArrayList<>();
    private DocumentList documentList;
    private String documentsFile;

    public DocumentService(final String documentsFile) {
        super();
        this.documentsFile = documentsFile;
        initialise();
    }

    private void initialise() {
        if (StringUtils.isEmpty(this.documentsFile)) {
            return;
        }

        final Optional<Object> docs = JsonService.readJsonDataFile(new File(this.documentsFile), DocumentList.class);
        if (docs.isPresent()) {
            this.documentList = (DocumentList)docs.get();
            this.documents.addAll( this.documentList.getHubDocuments() );
        }
    }

    public void reloadDocuments() {
        this.documentList.clearDocuments();
        this.documents.clear();
        initialise();
    }

    /*public void openDocumentsFile(final String hub) {
        final File file = new File(ActivityServerApplication.getApplicationContext().getDocumentsFile(hub));
        //DocumentUtilities.openTextFile(file);
    }*/

    public List<Document> getDocuments() {
        return this.documents;
    }

    public List<Document> getDocuments(final String project) {
        return this.documents.stream().filter(document -> project.equals(document.getProject()))
                .collect(Collectors.toList());
    }

    public Document getDocumentByName(final String name) {
        return this.documents.stream().filter(document -> name.equals(document.getName()))
                .findFirst().get();
    }

    public Document getEditableDocumentByName(final String name) {
        return this.documents.stream().filter(document -> "Maintenance".equals(document.getProject()) && name.equals(document.getName()))
                .findFirst().get();
    }

    public void saveDocumentList(final String hub) {
        JsonService.writeJsonDataFile(ActivityServerApplication.getApplicationContext().getDocumentsFile(hub), this.documentList);
    }}
