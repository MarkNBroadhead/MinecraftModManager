package app;

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

public class Welcome extends JDialog {
    private static final Logger LOGGER = LogManager.getLogger(Welcome.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton update;
    private JTextField minecraftDir;
    private JButton chooseFolder;
    private JTextArea lastUpdated;
    private Config config;

    public Welcome() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        try {
            config = Config.getConfig();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        config.getSetting("game.dir").ifPresent(dir -> minecraftDir.setText(dir.toString()));

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

        update.addActionListener(e -> App.updateMods());
        updateUpdatedWithLastGitCommitDate();

        minecraftDir.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                persistMinecraftDir(minecraftDir.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                persistMinecraftDir(minecraftDir.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                persistMinecraftDir(minecraftDir.getText());
            }
        });
    }

    private void persistMinecraftDir(String dir) {
        config.setSetting("game.dir", dir);
    }

    private void onOK() {
        try {
            Config.getConfig().save();
        } catch (IOException ex) {
            throw Log.logAndThrow("Error saving config file", ex);
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void updateUpdatedWithLastGitCommitDate() {
        new Thread(() -> {
            try {
                while (true) {
                    LOGGER.debug("Checking git repository for last git commit date");
                    String lastCommitDate = Git.getLastCommitDate();
                    LOGGER.debug("Last commit date: " + lastCommitDate);
                    lastUpdated.setText(lastCommitDate);
                    Thread.sleep(60000);
                    repaint();
                }
            } catch (Exception ex) {
                LOGGER.error("Error fetching last commit date from git repository", ex);
            }
        }).start();
    }

    public static void main(String[] args) {
        Welcome dialog = new Welcome();
        dialog.pack();
        dialog.setVisible(true);
        Utils.touchCacheFiles();
        System.exit(0);
    }
}
