package app;

import java.io.IOException;

public class ModUpdater {
    public static void updateMods() throws RuntimeException {
        updateGitRepo();
        try {
            if (!Utils.isMinecraftRunning()) {
                Utils.deleteOldMods();
                Utils.addNewMods();
            } else {
                throw Log.logAndThrow("Cannot update mods while Minecraft is running. Please shut down Minecraft to continue");
            }
        } catch (IOException ex) {
            throw Log.logAndThrow("Issue updating mods", ex);
        }
    }

    private static void updateGitRepo() {
        if (!Git.modRepoExists()) {
            try {
                Git.cloneModRepo();
            } catch (IOException ex) {
                throw Log.logAndThrow("Cannot clone mod repo", ex);
            } catch (InterruptedException ex) {
                throw Log.logAndThrow("Repository clone interrupted", ex);
            }
        } else {
            try {
                Git.fetchNewestMods();
            } catch (IOException ex) {
                throw Log.logAndThrow("Cannot update mod repo", ex);
            } catch (InterruptedException ex) {
                throw Log.logAndThrow("Repository update interrupted", ex);
            }
        }
    }
}
