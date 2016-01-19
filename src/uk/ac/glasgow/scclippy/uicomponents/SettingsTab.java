package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.lucene.IndexFiles;
import uk.ac.glasgow.scclippy.plugin.Settings;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Represents a class that contains components in the settings panel/tab
 */
public class SettingsTab {

    static JComponent settingsPanel = new JPanel();
    static JBScrollPane settingsPanelScroll = new JBScrollPane(settingsPanel);
    public static JTextField webAppURL = new JTextField("http://localhost:8080/scc/rest/search/"); //TODO

    static void initSettingsPanel() {
        Settings.loadSettings();

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));

        // General settings
        JPanel generalSettings = new JPanel();
        generalSettings.setBorder(BorderFactory.createTitledBorder(lineBorder, "General settings"));
        settingsPanel.add(generalSettings);
        // resizable input
        JCheckBox resizableInputPaneCheckBox = new ResizableInputPaneCheckBox("Auto-resizable query input text area");
        generalSettings.add(resizableInputPaneCheckBox);
        // highlighted results
        JCheckBox indexSearchHighlightCheckBox = new IndexSearchHighlightCheckBox("Highlighted results");
        generalSettings.add(indexSearchHighlightCheckBox);
        // Web App RESTful URL
        generalSettings.add(webAppURL);

        // Index settings
        JPanel indexOptions = new JPanel();
        indexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Index options"));
        settingsPanel.add(indexOptions);
        // index used option
        String text = Settings.indexPath == null ? "Index directory not selected" : Settings.indexPath;
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


    private static class ResizableInputPaneCheckBox extends JCheckBox {
        public ResizableInputPaneCheckBox(String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Settings.resizable = true;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    Settings.resizable = false;
                    SearchTab.inputPane.inputArea.setRows(InputPane.INPUT_TEXT_AREA_ROWS);
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
                    Settings.indexPath = chooser.getSelectedFile().getAbsolutePath();
                    Settings.saveSettings();
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
