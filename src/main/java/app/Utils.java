package app;

import app.Exception.ConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

class Utils {
    private static final Logger LOGGER = LogManager.getLogger(Utils.class);
    static final String MOD_REPO = "https://github.com/MarkNBroadhead/MinecraftMods.git";
    static final String LOCAL_MOD_DIR = "MinecraftMods" + File.separator + "mods";
    private static final String CACHE_FILE = "cache";
    private static final String WRITE_CACHE_FILE = "write_cache";

    private Utils() {
    }

    static Boolean isMinecraftRunning() throws IOException {
        String line;
        StringBuilder pidInfo = new StringBuilder();
        Boolean result = false;
        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            pidInfo.append(line);
        }
        input.close();
        if (pidInfo.toString().contains("minecraft.exe")) {
            result = true;
        }
        LOGGER.debug("Minecraft is" + (result ? " " : " not ") + "running.");
        return result;
    }

    static List<String> getListOfFilesInDir(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    static HashSet<String> getSetOfDirectoriesInDir(String dir) {
        File folder = new File(dir);
        File[] directories = folder.listFiles(File::isDirectory);
        HashSet<String> fileNames = new HashSet<>();
        if (directories != null) {
            for (File file : directories) {
                fileNames.add(file.getName() + "/");
            }
        }
        return fileNames;
    }

    static List<String> truncateVersionFromModFileName(List<String> fileNames) {
        String pattern = "([a-zA-Z0-9\\s-]+)([-_\\s\\[])(.+)(.jar)";
        List<String> truncatedNames = new ArrayList<>();
        for (String fileName : fileNames) {
            String newName = fileName.replaceAll(pattern, "$1");
            truncatedNames.add(newName.trim());
        }
        return truncatedNames;
    }

    static void deleteOldMods() {
        LOGGER.info("Deleting old managed mods");
        try {
            HashSet<String> copiedMods = new HashSet<>(fileLinesToList(WRITE_CACHE_FILE));
            for (String filename : copiedMods) {
                Path path = Paths.get(App.MINECRAFT_MOD_DIR + File.separator + filename);
                deleteFile(filename, path);
            }
        } catch (IOException | ConfigException ex) {
            throw Log.logAndReturnException("Could not delete old mods", ex);
        }
    }

    private static void deleteFile(String filename, Path path) {
        try {
            Files.delete(path);
            Log.logFileOperation(Log.FileOperation.DELETE, path.toString());
        } catch (NoSuchFileException ex) {
            LOGGER.debug("Unable to delete " + path + " no such file or directory", ex);
        } catch (DirectoryNotEmptyException ex) {
            throw Log.logAndReturnException(path.toString() + " directory is not empty.", ex);
        } catch (IOException ex) {
            throw Log.logAndReturnException("Permissions issue while deleting " + filename, ex);
        }
    }

    static void addNewMods() {
        copyModsIntoMinecraftModFolder();
    }

    static void copyModsIntoMinecraftModFolder() {
        List<String> mods = getListOfFilesInDir(LOCAL_MOD_DIR);
        for (String mod : mods) {
            try {
                Path modPath = Paths.get(LOCAL_MOD_DIR + File.separator + mod).toAbsolutePath();
                if (Config.getConfig().getModDir().isPresent()) {
                    Files.copy(modPath, Paths.get(Config.getConfig().getModDir() + File.separator + mod), StandardCopyOption.REPLACE_EXISTING);
                }
                Log.logFileOperation(Log.FileOperation.COPY, mod);
            } catch (IOException | ConfigException ex) {
                throw Log.logAndReturnException("Cannot copy " + mod + " to minecraft mod folder", ex);
            }
            try {
                recordModNames(WRITE_CACHE_FILE);
            } catch (IOException ex) {
                throw Log.logAndReturnException("Cannot access cache file " + CACHE_FILE, ex);
            }
        }
    }

    static void recordModNames(String cacheName) throws IOException {
        List<String> controlledModFiles = getListOfFilesInDir(LOCAL_MOD_DIR);
        writeListToFile(cacheName, controlledModFiles, true);
    }

    private static void writeListToFile(String cacheName, List<String> controlledModFiles, Boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(cacheName, append);
        for (String fileName : controlledModFiles) {
            fileWriter.write(fileName + "\n");
        }
        fileWriter.close();
    }

    static List<String> fileLinesToList(String fileName) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(fileNames::add);
        }
        return fileNames;
    }

    public static void touchCacheFiles() {
        try {
            touch(new File(CACHE_FILE));
            touch(new File(WRITE_CACHE_FILE));
        } catch (IOException ex) {
            LOGGER.error("Unable to create new cache files", ex);
        }
    }

    public static void touch(File file) throws IOException {
        long timestamp = System.currentTimeMillis();
        touch(file, timestamp);
    }

    private static void touch(File file, long timestamp) throws IOException {
        if (!file.exists()) {
            new FileOutputStream(file).close();
        }
        if (!file.setLastModified(timestamp)) {
            throw new IOException("Could not set last modified for " + file.getName());
        }
    }

    public static String getCurrentDirectory() throws IOException {
        return new java.io.File(".").getCanonicalPath();
    }
}
