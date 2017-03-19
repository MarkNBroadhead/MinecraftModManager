package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class App {
//    static final String MINECRAFT_DIR = "C:\\Users\\orcsl\\Documents\\Curse\\Minecraft\\Instances\\FTB Beyond (3)";
    static final String MINECRAFT_DIR = "C:\\Users\\orcsl\\Desktop\\testMinecraftFolder";
    static final String MINECRAFT_MOD_DIR = MINECRAFT_DIR + File.separator + "mods";
    static final String MINECRAFT_RESOURCE_PACK_DIR = MINECRAFT_DIR + File.separator + "resourcepacks";
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static final String BANNER = "\n" +
            "___  ___ _                                  __  _   ___  ___            _ ___  ___                                        \n" +
            "|  \\/  |(_)                                / _|| |  |  \\/  |           | ||  \\/  |                                        \n" +
            "| .  . | _  _ __    ___   ___  _ __  __ _ | |_ | |_ | .  . |  ___    __| || .  . |  __ _  _ __    __ _   __ _   ___  _ __ \n" +
            "| |\\/| || || '_ \\  / _ \\ / __|| '__|/ _` ||  _|| __|| |\\/| | / _ \\  / _` || |\\/| | / _` || '_ \\  / _` | / _` | / _ \\| '__|\n" +
            "| |  | || || | | ||  __/| (__ | |  | (_| || |  | |_ | |  | || (_) || (_| || |  | || (_| || | | || (_| || (_| ||  __/| |   \n" +
            "\\_|  |_/|_||_| |_| \\___| \\___||_|   \\__,_||_|   \\__|\\_|  |_/ \\___/  \\__,_|\\_|  |_/ \\__,_||_| |_| \\__,_| \\__, | \\___||_|   \n" +
            "                                                                                                         __/ |            \n" +
            "                                                                                                        |___/             \n";

    public static void main(String[] args) {
        LOGGER.info("Minecraft Mod Manager starting at: " + LocalDateTime.now());
        LOGGER.info(BANNER);
        Utils.touchCacheFiles();
        updateMods();
    }

    private static void updateMods() throws RuntimeException {
        updateGitRepo();
        try {
            if (!Utils.isMinecraftRunning()) {
                Utils.deleteOldMods();
                Utils.addNewMods();
            } else {
                Log.logAndThrow("Cannot update mods while Minecraft is running. Please shut down Minecraft to continue");
            }
        } catch (IOException e) {
            Log.logAndThrow("Issue updating mods", e);
        }
    }

    private static void updateGitRepo() {
        if (!Git.modRepoExists()) {
            try {
                Git.cloneModRepo();
            } catch (IOException e) {
                Log.logAndThrow("Cannot clone mod repo", e);
            }
        } else {
            try {
                Git.fetchNewestMods();
            } catch (IOException e) {
                Log.logAndThrow("Cannot update mod repo", e);
            }
        }
    }
}
