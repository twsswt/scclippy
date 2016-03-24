package uk.ac.glasgow.scclippy.uicomponents.search;

import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Button for searching Stack Overflow domain with Google
 */
public class GoogleSearchButton extends JButton {

	private QueryInputPane queryInputPane;
	
    public GoogleSearchButton(QueryInputPane queryInputPane) {
        super("Google Search");
        this.queryInputPane = queryInputPane;
        setToolTipText("Open browser to search for Stackoverflow posts");

        addActionListener(new GoogleSearchActionListener());
    }

    private class GoogleSearchActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Desktop.isDesktopSupported()) {
                try {
                	String queryTerm = queryInputPane.getQueryText();
                    String query = URLEncoder.encode(queryTerm.trim() + " ", "UTF-8");
                    URI uri = new URI("https://www.google.com/search?q=" + query + "site:stackoverflow.com");
                    Desktop.getDesktop().browse(uri);
                } catch (IOException | URISyntaxException e1) {
                	IntelijFacade.createErrorNotification(e1.getMessage());
                }
            }
        }
    }
}
