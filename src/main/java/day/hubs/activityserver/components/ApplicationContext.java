package day.hubs.activityserver.components;

import day.hubs.activityserver.domain.ApplicationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static day.hubs.activityserver.Constants.HUB_PLACEHOLDER;

@Component
public class ApplicationContext {

    // private Exception lastException = null;


    @Value("${default.task.name}")
    private String defaultTaskName;

    @Value("${default.task.view}")
    private String defaultTaskView;

    @Value("${task.name.placeholder}")
    private String taskNamePlaceholder;

    @Value("${enable.status.tracking}")
    private String enableStatusTracking;

    @Value("${notepad.exe}")
    private String notepadExe;

    @Value("${word.exe}")
    private String wordExe;

    @Value("${excel.exe}")
    private String excelExe;

    @Value("${vscode.exe}")
    private String vsCodeExe;


    @Value("${project.store}")
    private String projectStore;

    @Value("${template.store}")
    private String templateStore;

    @Value("${template.file}")
    private String templateFile;

    @Value("${documents.file}")
    private String documentsFile;

    @Value("${editable.documents.file}")
    private String editableDocumentsFile;

    @Value("${activities.file}")
    private String activitiesFile;

    @Value("${archived.projects.file}")
    private String archivedProjectsFile;

    @Value("${notices.file}")
    private String noticesFile;


    @Autowired
    public ApplicationContext() {
        super();
    }

    /*public String getLastExceptionStackTrace() {
        if (null == this.lastException) {
            return "No errors reported";
        }
        return Arrays.toString(this.lastException.getStackTrace());
    }*/

    public void setLastException(final Exception lastException) {
        // this.lastException = lastException;
    }

    public List<ApplicationProperty> getApplicationProperties(final String hub) {
        final List<ApplicationProperty> propertyList = new ArrayList<>();

        final String processName = ManagementFactory.getRuntimeMXBean().getName();

        propertyList.add(new ApplicationProperty("Server Process", processName));
        propertyList.add(new ApplicationProperty("Project store", getProjectStore(hub)));
        propertyList.add(new ApplicationProperty("Template store", getTemplateStore(hub)));
        propertyList.add(new ApplicationProperty("Template file", getTemplateFile(hub)));
        propertyList.add(new ApplicationProperty("Activities file", getActivitiesFile(hub)));
        propertyList.add(new ApplicationProperty("Task name placeholder", this.taskNamePlaceholder));
        propertyList.add(new ApplicationProperty("Archived projects file", getArchivedProjectsFile(hub)));
        propertyList.add(new ApplicationProperty("Notepad exe", this.notepadExe));
        propertyList.add(new ApplicationProperty("Word exe", this.wordExe));
        propertyList.add(new ApplicationProperty("Excel exe", this.excelExe));
        propertyList.add(new ApplicationProperty("VS Code exe", this.vsCodeExe));

        return propertyList;
    }

    public String getTaskNamePlaceholder() {
        return this.taskNamePlaceholder;
    }

    public String getNotepadExe() {
        return this.notepadExe;
    }

    public String getWordExe() {
        return this.wordExe;
    }

    public String getExcelExe() {
        return this.excelExe;
    }

    public String getVsCodeExe() {
        return this.vsCodeExe;
    }

    public String getProjectStore(final String hub) {
        return this.projectStore.replace(HUB_PLACEHOLDER, hub);
    }

    public String getTemplateStore(final String hub) {
        return this.templateStore.replace(HUB_PLACEHOLDER, hub);
    }

    public String getTemplateFile(final String hub) {
        return this.templateFile.replace(HUB_PLACEHOLDER, hub);
    }

    public String getDocumentsFile(final String hub) {
        return this.documentsFile.replace(HUB_PLACEHOLDER, hub);
    }

    public String getEditableDocumentsFile(final String hub) {
        return this.editableDocumentsFile.replace(HUB_PLACEHOLDER, hub);
    }

    public String getArchivedProjectsFile(final String hub) {
        return this.archivedProjectsFile.replace(HUB_PLACEHOLDER, hub);
    }

    public String getNoticesFile(final String hub) {
        return this.noticesFile.replace(HUB_PLACEHOLDER, hub);
    }

    public String getActivitiesFile(final String hub) {
        return this.activitiesFile.replace(HUB_PLACEHOLDER, hub);
    }
}
