package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

class Git {
    private static final Logger LOGGER = LogManager.getLogger(Git.class);

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

    static void cloneModRepo() throws IOException {
        LOGGER.info("Downloading mod repository...");
        Process process = new ProcessBuilder(Utils.GIT_FILENAME, "lfs", "clone", Utils.MOD_REPO).start();
        while (process.isAlive()) {
        }
        LOGGER.info("Cloning process complete");
    }

    static void fetchNewestMods() throws IOException {
        LOGGER.info("Checking for new mods...");
        Process process = new ProcessBuilder(Utils.GIT_FILENAME, "reset", "--hard", "origin/master").start();
        while (process.isAlive()) {
        }
        LOGGER.info("Git pull complete");
    }
}
