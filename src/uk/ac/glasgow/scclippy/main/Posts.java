package uk.ac.glasgow.scclippy.main;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import uk.ac.glasgow.scclippy.lucene.File;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;


class Posts {

    public JEditorPane[] postPane;

    Posts(int queryNumber, ToolWindow toolWindow) {
        postPane = new JEditorPane[queryNumber];

        for (int i = 0; i < postPane.length; i++) {
            postPane[i] = new JEditorPane("text/html", "");
            postPane[i].setEditable(false);
            postPane[i].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.YELLOW));
            HTMLEditorKit kit = new HTMLEditorKit();
            postPane[i].setEditorKit(kit);
            kit.getStyleSheet().addRule("code {background-color: olive;}");

            postPane[i].addMouseListener(new SelectedSnippetListener(i, toolWindow));
            postPane[i].addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (URISyntaxException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    void addTo(JPanel panel) {
        for (int i = 0; i < postPane.length; i++) {
            panel.add(postPane[i]);
        }
    }

    void update(File[] files) {
        for (int i = 0; i < files.length; i++) {
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
}
