package uk.ac.glasgow.scclippy.uicomponents.search;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.search.ResultsSorter;
import uk.ac.glasgow.scclippy.plugin.search.Search;
import uk.ac.glasgow.scclippy.plugin.search.StackExchangeSearch;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;
import uk.ac.glasgow.scclippy.uicomponents.history.SearchHistoryTab;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;

/**
 * Represents an InputPane (JTextArea)
 */
public class InputPane {

    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";
    public static final int INPUT_TEXT_AREA_ROWS = 5;

    public JTextArea inputArea = new JTextArea();
    private String lastText = "";

    private JScrollPane inputScrollPane = new JBScrollPane(inputArea);
    private Posts posts;
    private SearchTab searchTab;
    private SearchHistoryTab searchHistoryTab;

    private static Border border;

    static {
        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    InputPane(Posts posts, SearchHistoryTab searchHistoryTab, SearchTab searchTab) {
        this.posts = posts;
        this.searchHistoryTab = searchHistoryTab;
        this.searchTab = searchTab;

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(border);
        inputArea.setRows(INPUT_TEXT_AREA_ROWS);
        inputArea.getDocument().addDocumentListener(new InputPaneListener());

        InputMap input = inputArea.getInputMap();

        // 'shift + enter' for typing a new line
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);

        // 'enter' to perform search
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        input.put(enter, TEXT_SUBMIT);

        ActionMap actions = inputArea.getActionMap();
        actions.put(TEXT_SUBMIT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAction();
            }
        });
    }

    public JComponent getComponent() {
        return inputScrollPane;
    }

    private class InputPaneListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkRows();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkRows();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // no effect
        }

        private void checkRows() {
            if (Settings.resizableInputArea)
                inputArea.setRows(inputArea.getLineCount());
        }
    }

    private void searchAction() {

        String query = inputArea.getText();
        if (query.equals("") || query.equals(lastText))
            return;
        lastText = query;

        if (Settings.indexPath == null) {
            Notification.createErrorNotification("Set index path from 'SettingsTab' first");
            return;
        }

        searchHistoryTab.update(query);

        try {
            if (Search.currentSearchType.equals(Search.SearchType.LOCAL_INDEX)) {
                searchTab.getLocalSearch().search(query, Posts.defaultPostCount[0]);
            } else if (Search.currentSearchType.equals(Search.SearchType.WEB_SERVICE)) {
                searchTab.getWebServiceSearch().search(query, Posts.defaultPostCount[0]);
            } else if (Search.currentSearchType.equals(Search.SearchType.STACKEXCHANGE_API)) {
                searchTab.getStackExchangeSearch().search(query, Posts.defaultPostCount[0]);
                int requests = StackExchangeSearch.getRemainingCalls();
                searchTab.stackExchangeSearchRequestsLabel.setText(requests + " requests left.");
            }

            if (ResultsSorter.currentSortOption == ResultsSorter.SortType.BY_SCORE) {
                ResultsSorter.sortFilesByScore();
            }

            posts.update();
        } catch (Exception e) {
            Notification.createErrorNotification(e.getMessage());
        }
    }

}
