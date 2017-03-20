package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class MinecraftScanner {
    private static String WIN_USER_HOME_DIR = System.getProperty("user.home");
    private static String WIN_CURSE_DIR = WIN_USER_HOME_DIR + "\\Documents\\Curse\\Minecraft\\Instances\\";

    public static HashSet<Path> getCommonMinecraftPaths() {
        HashSet<Path> commonPaths = new HashSet<>();
        commonPaths.addAll(getCurseMinecraftFolders());
        return commonPaths;
    }

    public static HashSet<Path> getCurseMinecraftFolders() {

        HashSet<String> setOfDirectoriesInDir = Utils.getSetOfDirectoriesInDir(WIN_CURSE_DIR);
        HashSet<Path> paths = new HashSet<>();
        for (String dir : setOfDirectoriesInDir) {
            paths.add(Paths.get(dir));
        }
        return paths;
    }
}
