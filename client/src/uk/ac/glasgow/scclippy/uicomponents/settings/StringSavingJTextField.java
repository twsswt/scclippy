package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.FileNotFoundException;

/**
 * Updates the string provided when the text field is changed
 */
class StringSavingJTextField extends JTextField {

    String[] savedText;

    public StringSavingJTextField(String[] text) {
        super(text[0]);
        savedText = text;

        this.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                saveChange(e);
            }

            public void removeUpdate(DocumentEvent e) {
                saveChange(e);
            }

            public void insertUpdate(DocumentEvent e) {
                saveChange(e);
            }

            private void saveChange(DocumentEvent e) {
                try {
                    savedText[0] = e.getDocument().getText(0, e.getDocument().getLength());
                    Settings.saveSettings();
                } catch (BadLocationException | FileNotFoundException e1) {
                    Notification.createErrorNotification(e1.getMessage());
                }
            }
        });
    }
}