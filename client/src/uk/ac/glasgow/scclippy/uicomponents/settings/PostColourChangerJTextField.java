package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.util.Properties;

/**
 * Changes colour of the text in all posts
 */
class PostColourChangerJTextField extends JTextField {

    public PostColourChangerJTextField(Properties properties, PostsPane posts) {
        super(posts.getTextColour());

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyColour(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyColour(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyColour(e);
            }

            private void applyColour(DocumentEvent e) {
                try {
                    String newColour = e.getDocument().getText(0, e.getDocument().getLength());
                    properties.setProperty("textColour", newColour);
                    

                    if (newColour.equals("")) {
                        posts.removeTextColour();
                    } else {
                        posts.applyTextColour(newColour);
                    }
                } catch (BadLocationException e1) {
                	IntelijFacade.createErrorNotification(e1.getMessage());
                }
            }
        });
    }
}