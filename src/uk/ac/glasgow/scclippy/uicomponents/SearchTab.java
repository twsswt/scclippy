package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.plugin.Search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class SearchTab {

    private static int MAIN_SCROLL_STEP = 10;

    static JComponent searchPanel = new JPanel();
    static JBScrollPane searchPanelScroll = new JBScrollPane(searchPanel);

    public static InputPane inputPane = new InputPane();
    public static Posts posts = new Posts();

    static void initSearchPanel() {
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        searchPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);
        searchPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchPanelScroll.getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener());

        String[] searchOption = {"Local Index", "Web Service", "StackExchange API"};
        JComboBox searchOptions = new ComboBox(searchOption);
        searchOptions.setSelectedIndex(1);
        searchOptions.addActionListener(e -> {
            JComboBox cb = (JComboBox)e.getSource();
            String selectedSearchOption = (String)cb.getSelectedItem();

            for (int i = 0; i < searchOption.length; i++) {
                if (searchOption[i].equals(selectedSearchOption)) {
                    Search.currentSearchType = Search.SearchType.values()[i];
                    break;
                }
            }
        });

        String[] sortOption = {"Relevance", "Score"};
        JComboBox sortOptions = new ComboBox(sortOption);
        sortOptions.setSelectedIndex(0);
        sortOptions.addActionListener(e -> {
            JComboBox cb = (JComboBox) e.getSource();
            String selectedSortOption = (String) cb.getSelectedItem();

            for (int i = 0; i < sortOption.length; i++) {
                if (sortOption[i].equals(selectedSortOption)) {
                    Search.currentSortOption = Search.SortType.values()[i];
                    break;
                }
            }
        });

        // google button
        JButton searchWithGoogleButton = new GoogleSearchButton("Google Search");
        searchWithGoogleButton.setToolTipText("Open browser to search for Stackoverflow posts");

        // top panel
        JComponent topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(searchOptions);
        topPanel.add(sortOptions);
        topPanel.add(searchWithGoogleButton);

        // add components to search panel
        searchPanel.add(topPanel);
        searchPanel.add(inputPane.getComponent());
        posts.addTo(searchPanel);
    }

    static class PostsScrollListener implements AdjustmentListener {

        private static int lastValue = 0;
        final static int SCROLL_JUMP_BOUND = 100;

        public void adjustmentValueChanged(AdjustmentEvent evt) {

            int value = evt.getValue();
            int extent = searchPanelScroll.getVerticalScrollBar().getModel().getExtent();
            int maximum = searchPanelScroll.getVerticalScrollBar().getMaximum();

            boolean jump = false;
            // detect jumps from updated posts
            if (Math.abs(lastValue - value) > SCROLL_JUMP_BOUND)
                jump = true;

            lastValue = value;
            if (jump) {
                // do not allow jumps (jump back)
                SearchTab.searchPanelScroll.getVerticalScrollBar().setValue(SearchTab.searchPanelScroll.getVerticalScrollBar().getMinimum());
                return;
            }

            if (value + extent == maximum) {
                String query = inputPane.inputArea.getText();
                String msg = null;
                if (Search.currentSearchType.equals(Search.SearchType.LOCAL_INDEX)) {
                    msg = Search.localIndexSearch(query, Posts.maxPostCount[0]);
                } else if (Search.currentSearchType.equals(Search.SearchType.WEB_SERVICE)) {
                    msg = Search.webAppSearch(query, Posts.maxPostCount[0]);
                } else if (Search.currentSearchType.equals(Search.SearchType.STACKEXCHANGE_API)) {
                    msg = Search.stackExchangeSearch(query);
                }

                if (Search.currentSortOption == Search.SortType.BY_SCORE) {
                    Search.sortResultsByScore();
                }

                SearchTab.posts.update(msg);
            }
        }
    }
}
