package uk.ac.glasgow.scclippy.uicomponents.infotab;

import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class InfoTab {

    private JComponent infoPanel;
    private JScrollPane scroll;

    public InfoTab() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        scroll = new JBScrollPane(infoPanel);

        JEditorPane info = new JEditorPane();
        info.setText(getInfoText());
        info.addHyperlinkListener(new BrowserHyperlinkListener());
        // add components to info panel
        infoPanel.add(info);
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public String getInfoText() {
        return
                "Source Code Clippy is an IntelliJ plugin that allows searching " +
                "for excerpts of code for the Java programming language." +
                "\n\n" +
                "There are three ways of searching for excerpts:" +
                "\n" +
                "- Using a local index which uses Apache Lucene " +
                "to rank the indexed documents by the user" +
                "\n" +
                "- Using a web service which has already indexed posts from Stack Overflow" +
                "\n" +
                "- Using StackExchange API to search for snippets in Stack Overflow" +
                "\n\n" +
                "Project GitHub link: github.com/thrios/SourceCodeClippy";
    }
}