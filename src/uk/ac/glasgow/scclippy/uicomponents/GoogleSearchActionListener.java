package uk.ac.glasgow.scclippy.uicomponents;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


public class GoogleSearchActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
            try {
                String query = "";
                try {
                    query = URLEncoder.encode(SearchTab.inputPane.inputArea.getText().trim() + " ", "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                URI uri = new URI("https://www.google.com/search?q=" + query + "site:stackoverflow.com");
                Desktop.getDesktop().browse(uri);
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }
}
