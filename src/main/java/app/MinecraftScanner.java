package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MinecraftScanner {
    private static final String USER_HOME_DIR = System.getProperty("user.home");
    private static final String CURSE_DIR = USER_HOME_DIR + "\\Documents\\Curse\\Minecraft\\Instances\\";

    private MinecraftScanner() {
    }
    
    public static Set<Path> getCommonMinecraftPaths() {
        HashSet<Path> commonPaths = new HashSet<>();
        commonPaths.addAll(getCurseMinecraftFolders());
        return commonPaths;
    }

    static Set<Path> getCurseMinecraftFolders() {
        HashSet<String> setOfDirectoriesInDir = Utils.getSetOfDirectoriesInDir(CURSE_DIR);
        HashSet<Path> paths = new HashSet<>();
        for (String dir : setOfDirectoriesInDir) {
            paths.add(Paths.get(dir));
        }
        return paths;
    }
}
