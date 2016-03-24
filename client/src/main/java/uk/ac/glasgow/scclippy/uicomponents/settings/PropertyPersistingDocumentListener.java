package uk.ac.glasgow.scclippy.uicomponents.settings;

import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.util.Properties;

/**
 * Updates the string provided when the text field is
 * changed
 */
public class PropertyPersistingDocumentListener
	implements DocumentListener {

	private String key;
	private Properties properties;

	public PropertyPersistingDocumentListener(Properties properties, String key) {
       this.key = key;
       this.properties = properties;
	}

	public void changedUpdate(DocumentEvent e) {
		saveChange(e);
	}

	public void removeUpdate(DocumentEvent e) {
		saveChange(e);
	}

	public void insertUpdate(DocumentEvent e) {
		saveChange(e);
	}

	public void saveChange(DocumentEvent e) {
		try {

			Document document = e.getDocument();
			String newText = document.getText(0, document.getLength());
			properties.put(key, newText);

		} catch (BadLocationException e1) {
			IntelijFacade
				.createErrorNotification(e1.getMessage());
		}
	}
}