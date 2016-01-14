package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.components.JBScrollPane;
import uk.ac.glasgow.scclippy.lucene.SearchFiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class Search {

    private static int MAIN_SCROLL_STEP = 10;

    static JComponent searchPanel = new JPanel();
    static JBScrollPane searchPanelScroll = new JBScrollPane(searchPanel);
    public static Posts posts = new Posts();
    public static InputPane inputPane = new InputPane();

    static SearchType currentSearchType = SearchType.INDEX;

    enum SearchType { INDEX, API }

    static void initSearchPanel() {
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        searchPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);
        searchPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        searchPanelScroll.getVerticalScrollBar().addAdjustmentListener(new PostsScrollListener());

        // buttons
        JButton searchStackoverflowButton = new JButton("Search for excerpts in Stackoverflow");
        searchStackoverflowButton.addActionListener(new StackoverflowSearchActionListener(posts, inputPane));
        JButton searchWithGoogleButton = new JButton("Open browser to search for Stackoverflow posts");
        searchWithGoogleButton.addActionListener(new GoogleSearchActionListener(inputPane));

        // button panel
        JComponent buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(searchStackoverflowButton);
        buttonPanel.add(searchWithGoogleButton);

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
                Search.searchPanelScroll.getVerticalScrollBar().setValue(Search.searchPanelScroll.getVerticalScrollBar().getMinimum());
                return;
            }

            if (value + extent == maximum && currentSearchType.equals(SearchType.INDEX)) {
                try {
                    String text = inputPane.inputArea.getText().trim();
                    MainWindow.files = SearchFiles.search(
                            Settings.indexPath,
                            "contents",
                            text,
                            Posts.MAX_POST_COUNT
                    );
                    Search.posts.update(MainWindow.files);
                } catch (Exception e2) {
                    System.err.println(e2.getMessage());
                }
            }
        }
    }
}
