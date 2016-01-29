package uk.ac.glasgow.scclippy.uicomponents.search;

import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.search.ResultsSorter;
import uk.ac.glasgow.scclippy.plugin.search.Search;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * Scroll for search panel/tab
 */
public class SearchPanelScroll extends JScrollPane {

    private final static int SCROLL_STEP = 10;

    private Posts posts;

    public SearchPanelScroll(JComponent panel, Posts posts, Search localSearch, Search webServiceSearch, Search stackExchangeSearch) {
        super(panel);
        this.posts = posts;

        getVerticalScrollBar().setUnitIncrement(SCROLL_STEP);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener(localSearch, webServiceSearch, stackExchangeSearch));
    }

    class PostsScrollListener implements AdjustmentListener {

        private int lastValue = 0;
        final static int SCROLL_JUMP_BOUND = 100;

        Search localSearch;
        Search webServiceSearch;
        Search stackExchangeSearch;

        public PostsScrollListener(Search localSearch, Search webServiceSearch, Search stackExchangeSearch) {
            this.localSearch = localSearch;
            this.webServiceSearch = webServiceSearch;
            this.stackExchangeSearch = stackExchangeSearch;
        }

        public void adjustmentValueChanged(AdjustmentEvent evt) {
            int value = evt.getValue();
            int extent = getVerticalScrollBar().getModel().getExtent();
            int maximum = getVerticalScrollBar().getMaximum();

            boolean jump = false;
            // detect jumps from updated posts
            if (value > lastValue && Math.abs(lastValue - value) > SCROLL_JUMP_BOUND)
                jump = true;

            lastValue = value;
            if (jump) {
                // do not allow jumps (jump back)
                getVerticalScrollBar().setValue(getVerticalScrollBar().getMinimum());
                return;
            }

            if (value + extent == maximum) {
                String query = SearchTab.inputPane.inputArea.getText();

                try {
                    if (Search.currentSearchType.equals(Search.SearchType.LOCAL_INDEX)) {
                        localSearch.search(query, Posts.maxPostCount[0]);
                    } else if (Search.currentSearchType.equals(Search.SearchType.WEB_SERVICE)) {
                        webServiceSearch.search(query, Posts.maxPostCount[0]);
                    } else if (Search.currentSearchType.equals(Search.SearchType.STACKEXCHANGE_API)) {
                        stackExchangeSearch.search(query, Posts.maxPostCount[0]);
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
}
