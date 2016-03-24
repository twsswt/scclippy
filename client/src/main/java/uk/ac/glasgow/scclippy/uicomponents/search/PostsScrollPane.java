package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;
import uk.ac.glasgow.scclippy.plugin.search.SearchException;

public class PostsScrollPane extends JScrollPane {
	
    private final static int SCROLL_STEP = 10;

	public PostsScrollPane (PostsPane postsPane, SearchController searchController){
		super(postsPane);
		
    	getVerticalScrollBar().setUnitIncrement(SCROLL_STEP);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener(searchController));

	}
	
	class PostsScrollListener implements AdjustmentListener {
    	
        final static int SCROLL_JUMP_BOUND = 100;

        private int lastValue = 0;
        private SearchController searchController;

        public PostsScrollListener(SearchController searchController) {
            this.searchController = searchController;
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
            	searchController.incrementMaximumPostsToRetrieveOnScroll();
            	try {
					searchController.updateSearchAndSort();
				} catch (SearchException e) {
					IntellijFacade.createErrorNotification(e.getMessage());
				}            	
            }
        }
    }
}
