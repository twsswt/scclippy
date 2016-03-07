package uk.ac.glasgow.scclippy.uicomponents.infotab;

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
        info.setEditable(false);
        info.setText(getInfoText());

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
                "There are several ways of searching for excerpts:" +
                "\n" +
                "> Using a local index which uses Apache Lucene " +
                "to rank the indexed documents by the user. " +
                "\n(+) Can be changed to index files according to your preferences. " +
                "\n(-) Main downside is that it is usually restricted to only a few files." +
                "\n" +
                "> Using a web service which has already indexed posts from Stack Overflow" +
                "\n(requires ssh and tunneling (ssh -L 9999:rote.dcs.gla.ac.uk:9999 <USERNAME>@sibu.dcs.gla.ac.uk)). " +
                "\n(+) Good due to unlimited requests" +
                "\n(-) Requires user credentials in order to have access (will probably be changed in the future)." +
                "\n" +
                "> Using StackExchange API to search for snippets from Stack Overflow. " +
                "\n(+) Frequently updated" +
                "\n(-) Limited requests and does not handle variable names well enough." +
                "\n" +
                "> Google search button" +
                "\n(+) Up to date, not restrictive as other methods" +
                "\n(-) Not instantaneous - requires opening a tab, visiting the links, and navigating in the opened page" +
                "\n\n" +
                "All search methods use some sort of index and will usually give different results (!) based on the scoring performed." +
                "\n\n" +
                "Project GitHub link: github.com/thrios/SourceCodeClippy";
    }
}