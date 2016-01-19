package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.plugin.Search;
import uk.ac.glasgow.scclippy.plugin.Settings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputPane {

    final static int INPUT_TEXT_AREA_ROWS = 5;

    public JTextArea inputArea = new JTextArea();
    JScrollPane inputScrollPane = new JBScrollPane(inputArea);

    private static Border border;

    static {
        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    InputPane() {
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(border);
        inputArea.setRows(INPUT_TEXT_AREA_ROWS);
        inputArea.getDocument().addDocumentListener(new InputPaneListener(inputArea));
    }

    public JComponent getComponent() {
        return inputScrollPane;
    }

    private static class InputPaneListener implements DocumentListener {

        JTextArea inputPane;
        static String lastText = "";

        InputPaneListener(JTextArea inputPane) {
            this.inputPane = inputPane;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            searchAction();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            searchAction();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // not fired
        }

        private void searchAction() {
            if (Settings.resizable)
                inputPane.setRows(inputPane.getLineCount());

            String text = inputPane.getText();
            if (text.equals("") || text.equals(lastText))
                return;
            lastText = text;

            if (Settings.indexPath == null) {
                SearchTab.posts.update("Set index path from 'SettingsTab' first");
                return;
            }

            SearchHistoryTab.update(text);

            if (SearchTab.useAppServerCheckBox.isSelected()) {
                Search.webAppSearch(text, Posts.DEFAULT_POST_COUNT);
            } else {
                Search.localIndexSearch(text, Posts.DEFAULT_POST_COUNT);
            }
        }
    }

}
