package uk.ac.glasgow.scclippy.uicomponents;


import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.lucene.IndexFiles;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Settings {

    static JComponent settingsPanel = new JPanel();
    static JBScrollPane settingsPanelScroll = new JBScrollPane(settingsPanel);
    final static String settingsPath = "D:/scc_settings.txt";
    static String indexPath;

    static void initSettingsPanel() {
        loadSettings();

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));

        // Input and Output settings
        JPanel inputAndOutputOptions = new JPanel();
        inputAndOutputOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Input and Output options"));
        settingsPanel.add(inputAndOutputOptions);
        // input checkbox
        JCheckBox resizableInputPaneCheckBox = new ResizableInputPaneCheckBox("Auto-resizable query input text area");
        inputAndOutputOptions.add(resizableInputPaneCheckBox);
        // output checkbox
        JCheckBox indexSearchHighlightCheckBox = new IndexSearchHighlightCheckBox("Highlighted results");
        inputAndOutputOptions.add(indexSearchHighlightCheckBox);

        // Index settings
        JPanel indexOptions = new JPanel();
        indexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Index options"));
        settingsPanel.add(indexOptions);
        // index used option
        String text = indexPath == null ? "Index directory not selected" : indexPath;
        JLabel indexChosenLabel = new JLabel(text);
        indexOptions.add(indexChosenLabel);
        JButton indexChooserButton = new IndexChooserButton("Choose index", indexChosenLabel);
        indexOptions.add(indexChooserButton);

        // Creating/Updating index
        JPanel createIndexOptions = new JPanel();
        createIndexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Create or Update index"));
        settingsPanel.add(createIndexOptions);
        // index chooser
        JLabel indexFolderLabel = new JLabel("Index directory not selected");
        createIndexOptions.add(indexFolderLabel);
        String[] index = new String[1];
        JButton indexFolderChooser = new FolderChooserButton("Choose index folder", indexFolderLabel, index);
        createIndexOptions.add(indexFolderChooser);
        // data chooser
        JLabel dataFolderLabel = new JLabel("Data directory not selected");
        createIndexOptions.add(dataFolderLabel);
        String[] data = new String[1];
        JButton dataFolderChooser = new FolderChooserButton("Choose data folder", dataFolderLabel, data);
        createIndexOptions.add(dataFolderChooser);
        // update index option
        boolean[] update = new boolean[1];
        JCheckBox updateIndex = new UpdateIndex("Update index", update);
        createIndexOptions.add(updateIndex);
        // start indexing button
        JLabel statusLabel = new JLabel("");
        JButton indexButton = new IndexButton("Create/Update Index", index, data, update, statusLabel);
        createIndexOptions.add(indexButton);
        createIndexOptions.add(statusLabel);
    }

    static class IndexTask extends SwingWorker<Void, Void> {

        String indexPath;
        String dataPath;
        boolean updateIndex;

        public IndexTask(String[] index, String[] data, boolean[] update) {
            indexPath = index[0];
            dataPath = data[0];
            updateIndex = update[0];
        }

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            IndexFiles.index(indexPath, dataPath, updateIndex);
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private static void saveSettings() {
        java.io.File f = new java.io.File(settingsPath);

        try {
            PrintWriter pw = new PrintWriter(f);
            pw.write(indexPath);
            pw.write("\n");
            pw.write(String.valueOf(InputPane.resizable));
            pw.write("\n");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadSettings() {
        java.io.File f = new java.io.File(settingsPath);

        if (!f.exists()) {
            try {
                if (!f.createNewFile())
                    return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Scanner sc = new Scanner(f);
            if (sc.hasNextLine()) {
                indexPath = sc.nextLine();
            }
            if (sc.hasNextLine()) {
                InputPane.resizable = Boolean.parseBoolean(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class ResizableInputPaneCheckBox extends JCheckBox {
        public ResizableInputPaneCheckBox(String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    InputPane.resizable = true;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    InputPane.resizable = false;
                    Search.inputPane.inputArea.setRows(InputPane.INPUT_TEXT_AREA_ROWS);
                }
            });
        }
    }

    private static class IndexSearchHighlightCheckBox extends JCheckBox {
        public IndexSearchHighlightCheckBox(String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Posts.enableHighlights();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    Posts.disableHighlights();
                }
            });
        }
    }

    private static class IndexChooserButton extends JButton {
        public IndexChooserButton(String s, JLabel indexChosenLabel) {
            super(s);
            addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose index");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    indexPath = chooser.getSelectedFile().getAbsolutePath();
                    saveSettings();
                    indexChosenLabel.setText(chooser.getSelectedFile().getName());
                    indexChosenLabel.setToolTipText(chooser.getSelectedFile().getPath());
                }
            });
        }
    }

    private static class FolderChooserButton extends JButton {

        public FolderChooserButton(String s, JLabel indexFolderLabel, String[] path) {
            super(s);
            addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle(s);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path[0] = chooser.getSelectedFile().getAbsolutePath();
                    indexFolderLabel.setText(chooser.getSelectedFile().getName());
                    indexFolderLabel.setToolTipText(chooser.getSelectedFile().getPath());
                }
            });
        }
    }

    private static class UpdateIndex extends JCheckBox {

        public UpdateIndex(String s, boolean[] update) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    update[0] = true;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    update[0] = false;
                }
            });
        }
    }

    private static class IndexButton extends JButton {
        public IndexButton(String s, String[] index, String[] data, boolean[] update, JLabel statusLabel) {
            super(s);
            addActionListener(e -> {
                        statusLabel.setText("Indexing files...");
                        IndexTask task = new IndexTask(index, data, update);
                        task.execute();
                        statusLabel.setText("Finished indexing " + IndexFiles.getFilesIndexed() + " files!");
                    }
            );
        }
    }
}
