package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

public class Search {

    private static int MAIN_SCROLL_STEP = 10;

    static JComponent searchPanel = new JPanel();
    static JBScrollPane searchPanelScroll = new JBScrollPane(searchPanel);
    public static Posts posts = new Posts();
    public static InputPane inputPane = new InputPane();

    static void initSearchPanel() {
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));
        searchPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);

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

}
