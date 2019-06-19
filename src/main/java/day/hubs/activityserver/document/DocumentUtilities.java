package day.hubs.activityserver.document;

import day.hubs.activityserver.ActivityServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DocumentUtilities {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentUtilities.class);

    public static void openDocument(final String documentName, final String type) {
        if (null == type) {
            ActivityServerApplication.getApplicationContext().setLastException(new Exception("Document type not set for document " + documentName));
            return;
        }

        if (type.equals("folder")) {
            DocumentUtilities.openFolder(documentName);
        }
        else if (type.equals("network")) {
            DocumentUtilities.openRemoteFolder(documentName);
        }
        else if (type.equals("batch")) {
            DocumentUtilities.runBatchFile(documentName);
        }
        else if (type.equalsIgnoreCase("text")) {
            final File file = new File(documentName);
            DocumentUtilities.openTextFile(file);
        }
        else if (type.equals("file")) {
            final File file = new File(documentName);
            final String extension = documentName.substring(documentName.lastIndexOf('.'));
            if (".doc".equals(extension) || ".docx".equals(extension)) {
                DocumentUtilities.openWordDocument(file);
            } else if (".xls".equals(extension) || ".xlsx".equals(extension)) {
                DocumentUtilities.openExcelDocument(file);
            } else if (".txt".equals(extension)) {
                DocumentUtilities.openTextFile(file);
            } else if (".html".equals(extension)) {
                DocumentUtilities.openCodeFile(file);
            } else {
                DocumentUtilities.openTextFile(file);
            }
        }
        else if (type.equals("app")) {
            final File file = new File(documentName);
            DocumentUtilities.runApplication(file.getAbsolutePath());
        }
        else if (documentName.contains(".")) {
            ActivityServerApplication.getApplicationContext().setLastException(new Exception("Missing type when opening document " + documentName));
        }
    }

    public static void openWordDocument(final File document) {
        try {
            final Runtime load = Runtime.getRuntime();
            load.exec( ActivityServerApplication.getApplicationContext().getWordExe() + " \"" + document.toString() + '"');
        }
        catch(final IOException exception) {
            LOG.error("ControllerUtil: Failed to open Word document");
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void openTextFile(final File textPage) {
        try {
            final Runtime load = Runtime.getRuntime();
            load.exec(ActivityServerApplication.getApplicationContext().getNotepadExe() + " \"" + textPage.toString() + '"');
        }
        catch(final IOException exception) {
            LOG.error("ControllerUtil: Failed to open text file");
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    // Opens file in VS Code
    public static void openCodeFile(final File codeFile) {
        try {
            final Runtime load = Runtime.getRuntime();
            final String rawVsCodeCommand = ActivityServerApplication.getApplicationContext().getVsCodeExe();
            final String runCommand = rawVsCodeCommand.replace("{file}", codeFile.getAbsolutePath());
            load.exec(runCommand);
        }
        catch(final IOException exception) {
            LOG.error("ControllerUtil: Failed to open text file");
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void runBatchFile(final String batchFile) {
        try {
            final File dir = new File(batchFile.substring(0, batchFile.lastIndexOf('/')));
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "Start", batchFile);
            pb.directory(dir);
            Process p = pb.start();
        }
        catch(final IOException exception) {
            LOG.error("ControllerUtil: Failed to run batch file");
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void openExcelDocument(final File document) {
        try {
            final Runtime load = Runtime.getRuntime();
            load.exec( ActivityServerApplication.getApplicationContext().getExcelExe() + " \"" + document.toString() + '"');
        }
        catch(final IOException exception) {
            LOG.error("ControllerUtil: Failed to open Excel document");
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void runApplication(final String exePath) {
        try {
            final Runtime load = Runtime.getRuntime();
            load.exec(exePath);
        }
        catch(final IOException exception) {
            LOG.error(String.format("ControllerUtil: Failed to run application %s", exePath));
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void openRemoteFolder(final String folder) {
        try {
            final Runtime load = Runtime.getRuntime();
            load.exec("explorer.exe /select," + folder);
        }
        catch(final IOException exception) {
            LOG.error(String.format("ControllerUtil: Failed to open remote folder %s", folder));
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void openFolder(final String folder) {
        try {
            final String completeCmd = "explorer.exe /select," + folder;
            new ProcessBuilder(("explorer.exe " + completeCmd).split(" ")).start();
        }
        catch(final IOException exception) {
            LOG.error(String.format("ControllerUtil: Failed to open folder %s", folder));
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }
}
