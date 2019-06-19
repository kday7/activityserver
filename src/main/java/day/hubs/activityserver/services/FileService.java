package day.hubs.activityserver.services;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.document.DocumentUtilities;
import day.hubs.activityserver.project.Project;
import day.hubs.activityserver.task.Task;
import day.hubs.activityserver.task.TaskManager;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    private static final String FILE_EXTENSION = ".html";

    public static void createPageForTask(final String hub, final Project project, final Task task) throws IOException {
        try {
            if (null == project && null == project.getView()) {
                throw new IllegalStateException("Project must have a view");
            }

            // Create the project folder if required
            final Path projectPath = Paths.get(
                    ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + project.getView());
            projectPath.toFile().mkdirs();

            // Create the file contents
            final Path sourcePath = Paths.get(ActivityServerApplication.getApplicationContext().getTemplateFile(hub));
            final Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(sourcePath), charset);
            content = content.replaceAll(ActivityServerApplication.getApplicationContext().getTaskNamePlaceholder(), task.getName());

            // Create the file
            final Path destinationPath = Paths.get(
                    ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + project.getView() + '/' + task.getView() + FILE_EXTENSION);
            Files.write(destinationPath, content.getBytes(charset));

            // Open it for editing
            DocumentUtilities.openCodeFile(destinationPath.toFile());
        }
        catch (final IOException exception) {
            LOG.error("Error creating page for task", exception);
            ActivityServerApplication.getApplicationContext().setLastException(exception);
            throw new IOException(exception);
        }
    }

    public static void moveTaskPage(final String hub, final String source, final String destination) {
        try {
            if (source.equalsIgnoreCase(destination)) {
                return;
            }

            final Path sourcePath = Paths.get( ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + source + ".html");
            final Path destinationPath = Paths.get( ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + destination + ".html");
            Files.move(sourcePath, destinationPath, REPLACE_EXISTING);

        } catch (final IOException exception) {
            LOG.error("Failed to move the file", exception);
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void renameProjectFolder(final String hub, final String source, final String destination) {
        try {
            if (source.equalsIgnoreCase(destination)) {
                return;
            }

            final Path sourcePath = Paths.get( ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + source);
            final Path destinationPath = Paths.get( ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + destination);

            if (destinationPath.toFile().exists()) {
                return;     // TODO: Throw an exception here
            }
            Files.move(sourcePath, destinationPath, COPY_ATTRIBUTES);

        } catch (final IOException exception) {
            LOG.error("Failed to move the file", exception);
            ActivityServerApplication.getApplicationContext().setLastException(exception);
        }
    }

    public static void deletePageForTask(final String hub, final Project project, final Task task) throws IOException {
        try {
            if (null == project && null == project.getView()) {
                throw new IllegalStateException("Project must have a view");
            }

            // Delete the file
            final Path destinationPath = Paths.get(
                    ActivityServerApplication.getApplicationContext().getTemplateStore(hub) + project.getView() + '/' + task.getView() + FILE_EXTENSION);
            Files.delete(destinationPath);
        }
        catch (final IOException exception) {
            LOG.error("Error deleting page for task", exception);
            ActivityServerApplication.getApplicationContext().setLastException(exception);
            throw new IOException(exception);
        }
    }
}
