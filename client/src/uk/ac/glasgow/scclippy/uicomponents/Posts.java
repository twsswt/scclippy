package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.JBColor;
import uk.ac.glasgow.scclippy.lucene.File;

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
    public static int DEFAULT_POST_COUNT = 5;
    public static int MAX_POST_COUNT = 20;

    static HTMLEditorKit kit = new HTMLEditorKit();
    private static Border border;

    static {
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");

        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    Posts() {
        postPane = new JEditorPane[MAX_POST_COUNT];

        for (int i = 0; i < MAX_POST_COUNT; i++) {
            postPane[i] = new JEditorPane("text/html", "");
            postPane[i].setEditable(false);
            postPane[i].setBorder(border);
            postPane[i].setEditorKit(kit);
            postPane[i].addMouseListener(new DoubleClickOnPostListener(i));
            postPane[i].addHyperlinkListener(new PostHyperlinkListener());
        }
    }

    /**
     * Enables code highlights through css rules
     */
    static void enableHighlights() {
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
    }

    /**
     * Disables code highlights through css rules
     */
    static void disableHighlights() {
        kit.getStyleSheet().removeStyle("code");
        kit.getStyleSheet().removeStyle("span.highlight");
    }

    /**
     * Adds each pane to a panel
     * @param panel the panel
     */
    void addTo(JComponent panel) {
        for (JEditorPane aPostPane : postPane) {
            panel.add(aPostPane);
        }
    }

    /**
     * Updates all the panes with the files provided
     * @param files File array
     */
    public void update(File[] files) {
        if (files == null)
            return;

        for (int i = 0; i < files.length && i < postPane.length; i++) {
            if (files[i] == null) {
                return;
            }
            String text = files[i].getContent();
            String url = "<a href=\"http://stackoverflow.com/questions/"
                    + files[i].getFileName()
                    + "\">Link to Stackoverflow</a>";
            postPane[i].setText(text + url);
            postPane[i].setEnabled(true);
            postPane[i].updateUI();
        }
        for (int i = files.length; i < postPane.length; i++) {
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
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
