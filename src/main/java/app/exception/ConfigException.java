package app.exception;

public class ConfigException extends Exception {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Exception ex) {
        super(message, ex);
    }

    public ConfigException(Exception ex) {
        super(ex);
    }
}
