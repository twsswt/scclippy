package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.JBColor;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;


public class Posts {

    private JEditorPane[] postPane;
    static int SEARCH_POST_COUNT = 5;
    static HTMLEditorKit kit = new HTMLEditorKit();

    static {
        kit.getStyleSheet().addRule("code {background-color: olive;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
    }

    Posts() {
        postPane = new JEditorPane[SEARCH_POST_COUNT];

        for (int i = 0; i < postPane.length; i++) {
            postPane[i] = new JEditorPane("text/html", "");
            postPane[i].setEditable(false);
            postPane[i].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW));
            postPane[i].setEditorKit(kit);
            postPane[i].addMouseListener(new PostMouseListener(i));
            postPane[i].addHyperlinkListener(new PostHyperlinkListener());
        }
    }

    static void enableHighlights() {
        kit.getStyleSheet().addRule("code {background-color: olive;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
    }

    static void disableHighlights() {
        kit.getStyleSheet().removeStyle("code");
        kit.getStyleSheet().removeStyle("span.highlight");
    }


    /**
     * Adds each pane to a panel
     * @param panel the panel
     */
    void addTo(JComponent panel) {
        for (JEditorPane pane : postPane) {
            panel.add(pane);
        }
    }

    /**
     * Updates all the panes with the files provided
     * @param files File array
     */
    void update(File[] files) {
        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {
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
     * Updates the first pane with the message and the rest with empty strings
     * @param message the message to display
     */
    void update(String message) {
        postPane[0].setText(message);
        for (int i = 1; i < postPane.length; i++) {
            postPane[i].setText("");
        }
    }

    /**
     * Updates a pane
     * @param index index of the pane
     * @param snippetText the text to place in the pane
     * @param id id of the pane (starting from index 0)
     */
    void update(int index, String snippetText, int id) {
        if (index >= postPane.length) {
            return;
        }
        String url = "<a href=\"http://stackoverflow.com/questions/"
                + id
                + "\">Link to Stackoverflow</a>";


        postPane[index].setText(textToHTML(snippetText) + "<br/>" + url);
    }

    /**
     * Converts text to HTML by adding breaks and nbsp
     * @param snippetText the text
     * @return the html variant of the text
     */
    private String textToHTML(String snippetText) {
        snippetText = replaceAll(snippetText, '\n', "<br/>");
        snippetText = replaceAll(snippetText, ' ', "&nbsp ");

        return snippetText;
    }

    /**
     * Replaces all occurences of a character in a string with some string
     * @param s the initial string
     * @param c the character
     * @param replacement the replacement string
     * @return the modified string
     */
    private static String replaceAll(String s, char c, String replacement) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = s.length() - 2; i >= 0; i--) {
            if (sb.charAt(i) == c) {
                sb.replace(i, i+1, replacement);
            }
        }
        return sb.toString();
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
