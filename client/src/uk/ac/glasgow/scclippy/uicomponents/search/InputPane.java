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

/**
 * Represents an InputPane (JTextArea)
 */
public class InputPane {

    public final static int INPUT_TEXT_AREA_ROWS = 5;
    private final SearchHistoryTab searchHistoryTab;

    private Posts posts;

    public JTextArea inputArea = new JTextArea();
    JScrollPane inputScrollPane = new JBScrollPane(inputArea);

    SearchTab searchTab;

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
        inputArea.getDocument().addDocumentListener(new InputPaneListener(inputArea));
    }

    public JComponent getComponent() {
        return inputScrollPane;
    }

    private class InputPaneListener implements DocumentListener {

        JTextArea inputPane;
        String lastText = "";

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
            if (Settings.resizableInputArea)
                inputPane.setRows(inputPane.getLineCount());

            String query = inputPane.getText();
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
                    searchTab.stackExchangeSearchRequestsLabel.setText(requests + " requests left");
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

}
