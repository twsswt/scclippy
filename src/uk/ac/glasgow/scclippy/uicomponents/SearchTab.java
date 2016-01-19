package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;
import uk.ac.glasgow.scclippy.plugin.Search;
import uk.ac.glasgow.scclippy.plugin.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class SearchTab {

    private static int MAIN_SCROLL_STEP = 10;

    static JComponent searchPanel = new JPanel();
    static JBScrollPane searchPanelScroll = new JBScrollPane(searchPanel);

    // web server or desktop indexed search checkbox
    public static JCheckBox useAppServerCheckBox = new JCheckBox("App server / Local index");

    public static InputPane inputPane = new InputPane();
    public static Posts posts = new Posts();

    static void initSearchPanel() {
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        searchPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);
        searchPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchPanelScroll.getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener());

        // buttons
        JButton searchStackoverflowButton = new JButton("SearchTab for excerpts in Stackoverflow");
        searchStackoverflowButton.addActionListener(new StackoverflowSearchActionListener());
        JButton searchWithGoogleButton = new JButton("Open browser to search for Stackoverflow posts");
        searchWithGoogleButton.addActionListener(new GoogleSearchActionListener());

        // button panel
        JComponent buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(searchStackoverflowButton);
        buttonPanel.add(searchWithGoogleButton);
        buttonPanel.add(useAppServerCheckBox);

        // add components to search panel
        searchPanel.add(buttonPanel);
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

            if (value + extent == maximum && Search.currentSearchType.equals(Search.SearchType.INDEX)) {
                Search.localIndexSearch(inputPane.inputArea.getText(), Posts.MAX_POST_COUNT);
            }
        }
    }
}
