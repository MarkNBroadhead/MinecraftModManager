package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Welcome extends JDialog {
    private static final Logger LOGGER = LogManager.getLogger(Welcome.class);
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox startOnBoot;
    private JCheckBox hideOnBoot;
    private JButton update;
    private JTextField minecraftDir;
    private JButton chooseFolder;
    private JTextArea lastUpdated;

    public Welcome() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
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
                LOGGER.error("Could not open file dialog box to path in minecraftDir field");
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
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
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
            } catch (Exception e) {
                LOGGER.error("Error fetching last commit date from git repository", e);
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
