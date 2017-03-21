package app;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Config {
    private static Config INSTANCE;
    private static final String CONFIG_FILE = "mmm.yml";
    private Map settingsMap;

    private Config() throws YamlException, FileNotFoundException {
        INSTANCE = this;
        File f = new File(CONFIG_FILE);
        if(!f.exists()) {
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
        settingsMap = (Map)object;
    }

    public static Config getConfig() throws YamlException, FileNotFoundException  {
        return INSTANCE;
    }



}
