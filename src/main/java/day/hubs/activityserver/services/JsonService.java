package day.hubs.activityserver.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import day.hubs.activityserver.ActivityServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;

public class JsonService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonService.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonService() {
    }

    public static Optional<Object> readJsonDataFile(final File dataFile, final Class objectType) {
        try(InputStream stream = new FileInputStream(dataFile);) {

            final JsonReader jsonReader = new JsonReader(new InputStreamReader(stream));
            return Optional.of(GSON.fromJson(jsonReader, objectType));
        }
        catch(final Exception error) {
            LOG.error(error.getMessage());
        }

        return Optional.empty();
    }

    public static void writeJsonDataFile(final String dataFile, final Object object) {
        try (final Writer writer = new FileWriter(dataFile);) {
            final String jsonOutput = GSON.toJson(object);
            writer.write(jsonOutput);
            writer.flush();
        }
        catch(final Exception error) {
            LOG.error(error.getMessage());
        }
    }
}
