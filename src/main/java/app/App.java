package app;

import app.exception.ConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class App extends JDialog {
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton update;
    private JTextField minecraftDir;
    private JButton chooseFolder;
    private JTextArea lastUpdated;
    private Config config;
    private static final String BANNER = "\n" +
            "___  ___ _                                  __  _   ___  ___            _ ___  ___                                        \n" +
            "|  \\/  |(_)                                / _|| |  |  \\/  |           | ||  \\/  |                                        \n" +
            "| .  . | _  _ __    ___   ___  _ __  __ _ | |_ | |_ | .  . |  ___    __| || .  . |  __ _  _ __    __ _   __ _   ___  _ __ \n" +
            "| |\\/| || || '_ \\  / _ \\ / __|| '__|/ _` ||  _|| __|| |\\/| | / _ \\  / _` || |\\/| | / _` || '_ \\  / _` | / _` | / _ \\| '__|\n" +
            "| |  | || || | | ||  __/| (__ | |  | (_| || |  | |_ | |  | || (_) || (_| || |  | || (_| || | | || (_| || (_| ||  __/| |   \n" +
            "\\_|  |_/|_||_| |_| \\___| \\___||_|   \\__,_||_|   \\__|\\_|  |_/ \\___/  \\__,_|\\_|  |_/ \\__,_||_| |_| \\__,_| \\__, | \\___||_|   \n" +
            "                                                                                                         __/ |            \n" +
            "                                                                                                        |___/             \n";

    public App() {
        Utils.touchCacheFiles();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        try {
            config = Config.getConfig();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        if (!config.getGameDir().isPresent()) {
            config.setGameDir(MinecraftScanner.getCommonMinecraftPaths().get(0).toString());
            // show modal letting them know defaults are being set //todo
        }
        config.getGameDir().ifPresent(dir -> minecraftDir.setText(dir.toString()));

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        chooseFolder.addActionListener(e -> {
            LOGGER.debug("User clicked on directory button");
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            try {
                chooser.setCurrentDirectory(new File(minecraftDir.getText()));
            } catch (Exception ex) {
                LOGGER.error("Could not open file dialog box to path in minecraftDir field", ex);
            }
            int returnVal = chooser.showOpenDialog(chooseFolder);
            if (returnVal == 0) {
                LOGGER.info("Minecraft folder selected: " + chooser.getSelectedFile().getAbsolutePath());
                minecraftDir.setText(chooser.getSelectedFile().getAbsolutePath());
            } else {
                LOGGER.debug("No directory chosen");
            }
        });

        update.addActionListener(e -> ModUpdater.updateMods());
        updateUpdatedWithLastGitCommitDate();

        minecraftDir.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                config.setGameDir(minecraftDir.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                config.setGameDir(minecraftDir.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                config.setGameDir(minecraftDir.getText());
            }
        });
    }

    public static void main(String[] args) {
        LOGGER.info("Minecraft Mod Manager starting at: " + LocalDateTime.now());
        LOGGER.info(BANNER);
        App dialog = new App();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onCancel() {
        LOGGER.info("Exiting, cancel called.");
        dispose();
    }

    private void updateUpdatedWithLastGitCommitDate() {
        new Thread(() -> {
            try {
                while (true) {
                    String timeSinceLastCommit = Git.getLastModCommitDate();
                    LOGGER.debug("Most last updated: " + timeSinceLastCommit);
                    lastUpdated.setText(timeSinceLastCommit);
                    Thread.sleep(60000);
                    repaint();
                }
            } catch (Exception ex) {
                LOGGER.error("Error fetching last mod commit date from git repository", ex);
            }
        }).start();
    }

    private void onOK() {
        try {
            Config.getConfig().save();
        } catch (IOException | ConfigException ex) {
            throw Log.logAndReturnException("Error saving config file", ex);
        }
        dispose();
    }
}
