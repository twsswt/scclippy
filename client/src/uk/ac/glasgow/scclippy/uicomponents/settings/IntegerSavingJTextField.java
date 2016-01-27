package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.settings.Settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * Updates the integer provided when the text field is changed
 */
public class IntegerSavingJTextField extends JTextField {

    int[] savedNumber;

    public IntegerSavingJTextField(int[] number) {
        super(number[0]);
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
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}