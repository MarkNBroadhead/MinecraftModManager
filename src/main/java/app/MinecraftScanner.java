package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinecraftScanner {
    private static final String CURSE_DIR = System.getProperty("user.home") + "\\Documents\\Curse\\Minecraft\\Instances\\";

    private MinecraftScanner() {
    }

    static List<Path> getCommonMinecraftPaths() {
        HashSet<Path> commonPaths = new HashSet<>();
        commonPaths.addAll(getCurseMinecraftFolders());
        return new ArrayList<>(commonPaths);
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
