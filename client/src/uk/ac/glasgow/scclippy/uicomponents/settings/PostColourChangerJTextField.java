package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;
import uk.ac.glasgow.scclippy.uicomponents.search.Posts;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.FileNotFoundException;

/**
 * Changes colour of the text in all posts
 */
class PostColourChangerJTextField extends JTextField {

    public PostColourChangerJTextField(Posts posts) {
        super(Posts.textColour);
        posts.applyTextColour(Posts.textColour);

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
                    Posts.textColour = newColour;
                    Settings.saveSettings();

                    if (newColour.equals("")) {
                        posts.removeTextColour();
                    } else {
                        posts.applyTextColour(newColour);
                    }
                } catch (BadLocationException | FileNotFoundException e1) {
                    Notification.createErrorNotification(e1.getMessage());
                }
            }
        });
    }
}