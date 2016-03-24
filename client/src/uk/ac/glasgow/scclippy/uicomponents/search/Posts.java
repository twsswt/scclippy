package uk.ac.glasgow.scclippy.uicomponents.search;

import com.intellij.ui.JBColor;
import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.lucene.File;
import uk.ac.glasgow.scclippy.plugin.search.ResultsSorter;
import uk.ac.glasgow.scclippy.plugin.search.Search;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Represents the Posts (JEditorPane(s) that hold the results from a query)
 */
public class Posts {

    private JEditorPane[] postPane;

    public static int[] defaultPostCount = new int[]{5};
    public static int[] maxPostCount = new int[]{20};
    public static String textColour = "";

    static HTMLEditorKit kit = new HTMLEditorKit();
    private static Border border;

    static {
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");

        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.ORANGE);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    Posts() {
        postPane = new JEditorPane[maxPostCount[0]];

        for (int i = 0; i < maxPostCount[0]; i++) {
            postPane[i] = new JEditorPane("text/html", "");
            postPane[i].setEditable(false);
            postPane[i].setBorder(border);
            postPane[i].setEditorKit(kit);
            postPane[i].addMouseListener(new DoubleClickOnPostListener(postPane[i], i));
            postPane[i].addHyperlinkListener(new PostHyperlinkListener());
        }
    }

    /**
     * Enables code highlights through css rules
     */
    public static void enableHighlights() {
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
    }

    /**
     * Disables code highlights through css rules
     */
    public static void disableHighlights() {
        kit.getStyleSheet().removeStyle("code");
        kit.getStyleSheet().removeStyle("span.highlight");
    }

    /**
     * Sets colour to text by default in posts
     *
     * @param colourName name of the colour
     */
    public void applyTextColour(String colourName) {
        kit.getStyleSheet().addRule("body {color: " + colourName + "}");
        updatePostsUI();
    }

    /**
     * Removes colour to text by default in posts
     */
    public void removeTextColour() {
        kit.getStyleSheet().removeStyle("body");
        updatePostsUI();
    }

    /**
     * Updates UI of every post (JEditorPane)
     */
    private void updatePostsUI() {
        for (JEditorPane aPostPane : postPane) {
            aPostPane.updateUI();
        }
    }

    /**
     * Adds each pane to a panel
     *
     * @param panel the panel
     */
    void addTo(JComponent panel) {
        for (JEditorPane aPostPane : postPane) {
            panel.add(aPostPane);
        }
    }

    /**
     * Updates all the panes with the files provided
     */
    public void update() {
        File[] files = Search.getFiles();

        update("");
        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length && i < postPane.length; i++) {
            if (files[i] == null) {
                return;
            }
            if (files[i].getScore() < ResultsSorter.minimumScore[0]) {
                continue;
            }

            String text = files[i].getContent();
            String fileType = files[i].getFileName().contains("#") ? "Answer" : "Question";
            int score = files[i].getScore();

            String url;
            if (Search.currentSearchType == Search.SearchType.LOCAL_INDEX) {
                url = "<br/>" + "> " + files[i].getPath();
            } else {
                url = "<br/>" + "> " + fileType + " (score: " + score + "; "
                        + "<a href=\"http://stackoverflow.com/questions/"
                        + files[i].getFileName()
                        + "\">link</a>" + ")";
            }
            postPane[i].setText(text + url);
            postPane[i].setEnabled(true);
            postPane[i].updateUI();
        }

        for (int i = files.length; i < postPane.length; i++) {
            postPane[i].setText("");
        }
    }

    /**
     * Updates the first pane with the message and the rest with empty strings
     * If message is null, normal update of posts is carried out
     *
     * @param message the message to display
     */
    public void update(String message) {
        if (message == null)
            return;

        postPane[0].setText(message);
        for (int i = 1; i < postPane.length; i++) {
            postPane[i].setText("");
        }
    }

    /**
     * Listener for opening browser on hyperlink clicks
     */
    private class PostHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                        Notification.createErrorNotification(e1.getMessage());
                    }
                }
            }
        }
    }
}
