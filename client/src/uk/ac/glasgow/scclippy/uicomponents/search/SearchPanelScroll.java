package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Scroll for search panel/tab
 */
public class SearchPanelScroll extends JScrollPane {

    private final static int SCROLL_STEP = 10;

    public SearchPanelScroll(JComponent panel, SearchTab searchTab) {
        super(panel);

        getVerticalScrollBar().setUnitIncrement(SCROLL_STEP);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener(searchTab));
    }

    class PostsScrollListener implements AdjustmentListener {
    	
        final static int SCROLL_JUMP_BOUND = 100;

        private int lastValue = 0;
        private SearchTab searchTab;

        public PostsScrollListener(SearchTab searchTab) {
            this.searchTab = searchTab;
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
            	searchTab.updateSearch();            	
            }
        }
    }
}
