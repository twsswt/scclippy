package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.editor.Notification;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.FileNotFoundException;

/**
 * Updates the integer provided when the text field is changed
 */
public class IntegerSavingJTextField extends JTextField {

    int[] savedNumber;

    public IntegerSavingJTextField(int[] number, int defaultValue) {
        super(5);
        setText(String.valueOf(number[0]));

        savedNumber = number;

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
                    savedNumber[0] = Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength()));
                    Settings.saveSettings();
                } catch (BadLocationException | FileNotFoundException | NumberFormatException e1) {
                    savedNumber[0] = defaultValue;
                    Notification.createErrorNotification(e1.getMessage());
                }
            }
        });
    }
}