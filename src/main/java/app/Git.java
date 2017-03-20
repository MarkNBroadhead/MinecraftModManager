package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

class Git {
    private static final Logger LOGGER = LogManager.getLogger(Git.class);

    private Git() {
    }

    static boolean modRepoExists() {
        LOGGER.debug("Checking for local git mod repo folder");
        File dir = new File(Utils.LOCAL_MOD_DIR);
        Boolean dirExists = dir.exists();
        LOGGER.debug("Directory " + dir + (dirExists ? " exists." : " does not exist."));
        if (dirExists) {
            File gitRepo = new File(Utils.LOCAL_MOD_DIR + "/.git");
            Boolean repoExists = gitRepo.exists();
            LOGGER.debug("Directory " + gitRepo + (repoExists ? " exists." : " does not exist."));
            return repoExists;
        } else {
            return false;
        }
    }

    static void cloneModRepo() throws IOException, InterruptedException {
        // TODO make these cancelable and put in a different thread than GUI
        LOGGER.info("Downloading mod repository...");
        Process process = new ProcessBuilder(Utils.GIT_FILENAME, "lfs", "clone", Utils.MOD_REPO).start();
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
            throw ex;
        }
        LOGGER.info("Cloning process complete");
    }

    static void fetchNewestMods() throws IOException, InterruptedException {
        // TODO make these cancelable and put in a different thread than GUI
        LOGGER.info("Checking for new mods...");
        Process process = new ProcessBuilder(Utils.GIT_FILENAME, "reset", "--hard", "origin/master").start();
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
            throw ex;
        }
        LOGGER.info("Git pull complete");
    }

    static String getLastCommitDate() throws IOException {
        LOGGER.info("Checking for new mods...");
        Process process = new ProcessBuilder(Utils.GIT_FILENAME, "log", "-1", "--format=%cd").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }
}
