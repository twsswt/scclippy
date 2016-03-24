package uk.ac.glasgow.scclippy.uicomponents.settings;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;

/**
 * Changes colour of the text in all posts
 */
public class PostColourChangerDocumentListener implements DocumentListener {

	private PostsPane posts;
	
	public PostColourChangerDocumentListener(PostsPane posts) {
		this.posts = posts;
	}

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
			String newColour = e.getDocument().getText(0,
				e.getDocument().getLength());

			if (newColour.equals("")) {
				posts.removeTextColour();
			} else {
				posts.applyTextColour(newColour);
			}
		} catch (BadLocationException e1) {
			IntellijFacade
				.createErrorNotification(e1.getMessage());
		}
	}
}