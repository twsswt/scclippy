package uk.ac.glasgow.scclippy.uicomponents.settings;

import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;

public class PropertyPreservingTextFieldPanel extends JPanel {

	private JTextField jTextField;
	
	public PropertyPreservingTextFieldPanel (Properties properties, String key, String labelString){
        jTextField = new JTextField(properties.getProperty(key));
        
        jTextField.getDocument().addDocumentListener(
        	new PropertyPersistingDocumentListener(properties, key));
       
        JLabel jLabel = new JLabel(labelString);
        jLabel.setLabelFor(jTextField);
        
        add(jLabel);
        add(jTextField);
	}

	public void addDocumentListener(DocumentListener listener) {
		jTextField.getDocument().addDocumentListener(listener);
	}
	
}

class PropertyPersistingDocumentListener implements DocumentListener {

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
			IntellijFacade
				.createErrorNotification(e1.getMessage());
		}
	}
}