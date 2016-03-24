package uk.ac.glasgow.scclippy.uicomponents.search;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Ask a question button
 */
public class AskAQuestionButton extends JButton {

    final String STACKOVERFLOW_WRITE_QUESTION_URL = "http://stackoverflow.com/questions/ask/";

    public AskAQuestionButton(){
        super("Ask a question");
        addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    URI uri = new URI(STACKOVERFLOW_WRITE_QUESTION_URL);
                    Desktop.getDesktop().browse(uri);
                } catch (IOException | URISyntaxException e1) {
                	IntellijFacade.createErrorNotification(e1.getMessage());
                }
            }
        });
    }
}
