package app;

import app.Exception.ConfigException;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;

public final class Config implements Serializable {
    private static final String CONFIG_FILE = "mmm.yml";
    private static final String CREATE_ERROR_MSG = "Error creating new empty configuration file " + CONFIG_FILE;
    private static final String ACCESS_ERROR_MSG = "Error accessing configuration file " + CONFIG_FILE;
    private static final String READ_ERROR_MSG = "Error reading or parsing configuration file " + CONFIG_FILE;
    private static volatile Config instance;
    private HashMap settings;

    private Config() throws ConfigException {
        instance = this;
        File f = new File(CONFIG_FILE);
        Boolean configFileExists = f.exists();
        if (!configFileExists) {
            Boolean newFileCreated;
            try {
                newFileCreated = f.createNewFile();
            } catch (IOException ex) {
                throw Log.logAndReturnException(new ConfigException(CREATE_ERROR_MSG, ex));
            }
            if (!newFileCreated) {
                throw Log.logAndReturnException(new ConfigException(CREATE_ERROR_MSG));
            }
        }
        FileReader fr;
        try {
            fr = new FileReader(CONFIG_FILE);
        } catch (FileNotFoundException ex) {
            throw Log.logAndReturnException(new ConfigException(ACCESS_ERROR_MSG, ex));
        }
        YamlReader reader = new YamlReader(fr);
        Object object;
        try {
            object = reader.read();
        } catch (YamlException ex) {
            throw Log.logAndReturnException(new ConfigException(READ_ERROR_MSG, ex));
        }
        settings = object == null ? new HashMap<>() : (HashMap) object;
    }

    public static Config getConfig() throws ConfigException {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
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

    public Optional<Object> getModDir() {
        return getSetting("game.dir");
    }
}
