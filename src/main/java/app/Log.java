package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

class Log {
    private static final Logger LOGGER = LogManager.getLogger(Log.class);
    private static final String FILE_OPERATION_LOG = "logs/file_operation.log";

    public enum FileOperation {
        COPY("copying", "copied"),
        DELETE("deleting", "deleted");

        private final String present;
        private final String past;

        FileOperation(final String present, final String past) {
            this.present = present;
            this.past = past;
        }

        public String getPresentParticiple() { return present; }
        public String getPastTense() { return past; }

        @Override
        public String toString() { return present; }
    }

    static RuntimeException logAndThrow(String message, Exception ex) {
        LOGGER.error(message, ex);
        return new RuntimeException(message, ex);
    }

    static RuntimeException logAndThrow(String message) {
        LOGGER.error(message);
        return new RuntimeException(message);
    }

    static void logFileOperation(FileOperation operation, String filename) {
        try (FileWriter fileWriter = new FileWriter(FILE_OPERATION_LOG, true)) {
            fileWriter.write(LocalDateTime.now() + " -- " + operation.getPresentParticiple() + " " + filename + "\n");
            LOGGER.debug(operation.getPresentParticiple() + " " + filename);
        } catch (IOException ex) {
            LOGGER.error("Error " + operation.getPresentParticiple() + " to " + FILE_OPERATION_LOG + " log", ex);
        }
    }
}
