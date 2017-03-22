package app;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Config {
    private static volatile Config INSTANCE;
    private static final String CONFIG_FILE = "mmm.yml";
    private Map settings;

    private Config() throws YamlException, FileNotFoundException {
        INSTANCE = this;
        File f = new File(CONFIG_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                throw Log.logAndThrow("Error creating new empty configuration file " + CONFIG_FILE, ex);
            }
        }
        FileReader fr;
        try {
            fr = new FileReader(CONFIG_FILE);
        } catch (FileNotFoundException ex) {
            throw Log.logAndThrow("Cannot open configuration file " + CONFIG_FILE);

        }
        YamlReader reader = new YamlReader(fr);
        Object object = reader.read();
        if (object == null) {
            settings = new HashMap<>();
        } else {
            settings = (Map) object;
        }

    }

    public static Config getConfig() throws YamlException, FileNotFoundException {
        if (INSTANCE == null) {
            synchronized (Config.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Config();
                }
            }
        }
        return INSTANCE;
    }

    @Deprecated
    public Optional<Map> getSetings() {
        return Optional.ofNullable(settings);
    }

    public Optional<Object> getSetting(String key) {
        return Optional.ofNullable(settings != null ? settings.get(key) : null);
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public void delSetting(String key) {
        settings.remove(key);
    }

    public void save() throws IOException {
        if (settings != null) {
            YamlWriter writer = new YamlWriter(new FileWriter(CONFIG_FILE));
            writer.write(settings);
            writer.close();
        }
    }
}
