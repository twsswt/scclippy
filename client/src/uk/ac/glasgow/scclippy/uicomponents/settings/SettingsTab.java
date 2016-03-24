package uk.ac.glasgow.scclippy.uicomponents.settings;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.lucene.IndexFiles;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;
import uk.ac.glasgow.scclippy.uicomponents.search.InputPane;
import uk.ac.glasgow.scclippy.uicomponents.search.Posts;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Represents a class that contains components in the settings panel/tab
 */
public class SettingsTab {

    private JComponent settingsPanel = new JPanel();
    private JScrollPane scroll = new JBScrollPane(settingsPanel);

    static JTextField webAppURL;

    Posts posts;

    public SettingsTab(Posts posts) {
        this.posts = posts;
        initSettingsPanel();
    }

    void initSettingsPanel() {
        try {
            Settings.loadSettings();
        } catch (FileNotFoundException | NumberFormatException e) {
            Notification.createErrorNotification(e.getMessage());
        }

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));

        //// General settings
        JPanel generalSettings = new JPanel();
        generalSettings.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
//        generalSettings.setLayout(new BoxLayout(generalSettings, BoxLayout.PAGE_AXIS));
        generalSettings.setBorder(BorderFactory.createTitledBorder(lineBorder, "General settings"));
        settingsPanel.add(generalSettings);

        // resizable input
        JCheckBox resizableInputPaneCheckBox = new ResizableInputPaneCheckBox("Auto-resizable query input text area");
        gbc.gridx = 0;
        gbc.gridy = 0;
        generalSettings.add(resizableInputPaneCheckBox, gbc);

        // highlighted results
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox indexSearchHighlightCheckBox = new IndexSearchHighlightCheckBox("Highlighted results");
        generalSettings.add(indexSearchHighlightCheckBox, gbc);

        JPanel mergePanel;
        // Default post count
        int defaultPostsCountValue = 5;
        JTextField defaultPostCountTextField = new IntegerSavingJTextField(Posts.defaultPostCount, defaultPostsCountValue);
        JLabel defaultPostCountLabel = new JLabel("Total number of posts after search (influences efficiency)");
        defaultPostCountLabel.setLabelFor(defaultPostCountTextField);
        mergePanel = new JPanel();
        mergePanel.add(defaultPostCountLabel);
        mergePanel.add(defaultPostCountTextField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        generalSettings.add(mergePanel, gbc);

        // Max post count
        int maxPostsCountValue = 20;
        JTextField maxPostCountTextField = new IntegerSavingJTextField(Posts.maxPostCount, maxPostsCountValue);
        JLabel maxPostCountLabel = new JLabel("Total number of posts after scrolling down search (influences efficiency)");
        maxPostCountLabel.setLabelFor(maxPostCountTextField);
        mergePanel = new JPanel();
        mergePanel.add(maxPostCountLabel);
        mergePanel.add(maxPostCountTextField);
        gbc.gridx = 0;
        gbc.gridy = 3;
        generalSettings.add(mergePanel, gbc);

        // Colour of text in posts
        JTextField postsTextColourTextField = new PostColourChangerJTextField(posts);
        JLabel postsTextColourLabel = new JLabel("Colour of text (hex colours e.g. #FF0000; colours by name e.g. red)");
        postsTextColourLabel.setLabelFor(postsTextColourTextField);
        mergePanel = new JPanel();
        mergePanel.add(postsTextColourLabel);
        mergePanel.add(postsTextColourTextField);
        gbc.gridx = 0;
        gbc.gridy = 4;
        generalSettings.add(mergePanel, gbc);

        //// Web service settings
        JPanel webServiceOptions = new JPanel(new GridBagLayout());
        webServiceOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Web service options"));
        settingsPanel.add(webServiceOptions);

        // Web App RESTful URL
        webAppURL = new StringSavingJTextField(Settings.webServiceURI);
        JLabel webAppURLLabel = new JLabel("Web service URI:");
        webAppURLLabel.setLabelFor(webAppURL);
        mergePanel = new JPanel();
        mergePanel.add(webAppURLLabel);
        mergePanel.add(webAppURL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        webServiceOptions.add(mergePanel, gbc);

        //// Local Index settings
        JPanel localIndexOptions = new JPanel();
        localIndexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Local index options"));
        localIndexOptions.setLayout(new GridBagLayout());
        settingsPanel.add(localIndexOptions);

        // Local index used option
        String text = Settings.indexPath == null ? "Index directory not selected" : Settings.indexPath;
        JLabel indexChosenLabel = new JLabel(text);
        JButton indexChooserButton = new IndexChooserButton("Choose index", indexChosenLabel);
        mergePanel = new JPanel();
        mergePanel.add(indexChosenLabel);
        mergePanel.add(indexChooserButton);
        gbc.gridx = 0;
        gbc.gridy = 0;
        localIndexOptions.add(mergePanel, gbc);

        // Creating/Updating index
        JPanel createIndexOptions = new JPanel();
        createIndexOptions.setLayout(new GridBagLayout());
        createIndexOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Create/Update Local Index"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        localIndexOptions.add(createIndexOptions, gbc);

        // index chooser
        JLabel indexFolderLabel = new JLabel("Index directory not selected");
        String[] index = new String[1];
        JButton indexFolderChooser = new FolderChooserButton("Choose index folder", indexFolderLabel, index);
        mergePanel = new JPanel(new GridBagLayout());
        mergePanel.add(indexFolderLabel);
        mergePanel.add(indexFolderChooser);
        gbc.gridx = 0;
        gbc.gridy = 0;
        createIndexOptions.add(mergePanel, gbc);

        // data chooser
        JLabel dataFolderLabel = new JLabel("Data directory not selected");
        String[] data = new String[1];
        JButton dataFolderChooser = new FolderChooserButton("Choose data folder", dataFolderLabel, data);
        mergePanel = new JPanel(new GridBagLayout());
        mergePanel.add(dataFolderLabel);
        mergePanel.add(dataFolderChooser);
        gbc.gridx = 0;
        gbc.gridy = 1;
        createIndexOptions.add(mergePanel, gbc);

        // update index option
        boolean[] update = new boolean[1];
        JCheckBox updateIndex = new UpdateIndex("Update index", update);
        gbc.gridx = 0;
        gbc.gridy = 2;
        createIndexOptions.add(updateIndex);

        // start indexing button
        JLabel statusLabel = new JLabel("");
        JButton indexButton = new IndexButton("Create/Update Index", index, data, update, statusLabel);
        mergePanel = new JPanel(new GridBagLayout());
        mergePanel.add(indexButton);
        mergePanel.add(statusLabel);
        gbc.gridx = 0;
        gbc.gridy = 3;
        createIndexOptions.add(mergePanel, gbc);
    }

    class IndexTask extends SwingWorker<Void, Void> {

        String indexPath;
        String dataPath;
        boolean updateIndex;

        public IndexTask(String[] index, String[] data, boolean[] update) {
            indexPath = index[0];
            dataPath = data[0];
            updateIndex = update[0];
        }

        /**
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            try {
                IndexFiles.index(indexPath, dataPath, updateIndex);
            } catch (IOException e) {
                Notification.createErrorNotification(e.getMessage());
            }
            return null;
        }

        /**
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Notification.createInfoNotification("Finished Indexing");
        }
    }


    private class ResizableInputPaneCheckBox extends JCheckBox {
        public ResizableInputPaneCheckBox(String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Settings.resizableInputArea = true;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    Settings.resizableInputArea = false;
                    SearchTab.inputPane.inputArea.setRows(InputPane.INPUT_TEXT_AREA_ROWS);
                }
            });
        }
    }

    private class IndexSearchHighlightCheckBox extends JCheckBox {
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

    private class IndexChooserButton extends JButton {
        public IndexChooserButton(String s, JLabel indexChosenLabel) {
            super(s);
            addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose index");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    Settings.indexPath = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Settings.saveSettings();
                    } catch (FileNotFoundException e1) {
                        Notification.createErrorNotification(e1.getMessage());
                    }
                    indexChosenLabel.setText(chooser.getSelectedFile().getName());
                    indexChosenLabel.setToolTipText(chooser.getSelectedFile().getPath());
                }
            });
        }
    }

    private class FolderChooserButton extends JButton {

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

    private class UpdateIndex extends JCheckBox {

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

    private class IndexButton extends JButton {
        public IndexButton(String s, String[] index, String[] data, boolean[] update, JLabel statusLabel) {
            super(s);
            setBackground(JBColor.orange);
            addActionListener(e -> {
                        statusLabel.setText("Indexing files...");
                        IndexTask task = new IndexTask(index, data, update);
                        task.execute();
                        statusLabel.setText("Finished indexing " + IndexFiles.getFilesIndexed() + " files!");
                    }
            );
        }
    }

    public JScrollPane getScroll() {
        return scroll;
    }
}
