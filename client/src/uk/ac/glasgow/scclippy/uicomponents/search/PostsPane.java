package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import com.intellij.ui.JBColor;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;


/**
 * Represents the Posts (JEditorPane(s) that hold the results from a query)
 */
public class PostsPane extends JPanel {

    public static final int defaultPostCount = 5;
    public static final int maxPostCount = 20;
    
    private String textColour = "";

    private HTMLEditorKit kit = new HTMLEditorKit();
    private Border border;
    
    /** A cache of editor panes that can be added to or removed from the parent JPanel as desired.*/
    private List<JEditorPane> entryEditorPanes;


    PostsPane() {
    	initialiseHTMLEditorKit();
        initialiseBorder();
    }

	private void initialiseBorder() {
		Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.ORANGE);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
	}

	private void initialiseHTMLEditorKit() {
		kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
	}
	
	private void initialiseEntryEditorPane() {
		JEditorPane postPane = new JEditorPane("text/html", "");
		postPane.setEditable(false);
		postPane.setVisible(false);
		postPane.setBorder(border);
		postPane.setEditorKit(kit);
		postPane.addMouseListener(new DoubleClickOnPostListener(postPane));
		postPane.addHyperlinkListener(new PostHyperlinkListener());
		entryEditorPanes.add(postPane);
		this.add(postPane);
	}

    /**
     * Enables code highlights through css rules
     */
    public void enableHighlights() {
        kit.getStyleSheet().addRule("code {color: #909090;}");
        kit.getStyleSheet().addRule("span.highlight {background-color: olive;}");
    }

    /**
     * Disables code highlights through css rules
     */
    public void disableHighlights() {
        kit.getStyleSheet().removeStyle("code");
        kit.getStyleSheet().removeStyle("span.highlight");
    }

    /**
     * Sets colour to text by default in posts
     *
     * @param colourName name of the colour
     */
    public void applyTextColour(String colourName) {
        kit.getStyleSheet().addRule("body {color: " + colourName + "}");
        updatePostsUI();
    }

    /**
     * Removes colour to text by default in posts
     */
    public void removeTextColour() {
        kit.getStyleSheet().removeStyle("body");
        updatePostsUI();
    }

    /**
     * Updates UI of every post (JEditorPane)
     */
    private void updatePostsUI() {
        for (JEditorPane aPostPane : entryEditorPanes) {
            aPostPane.updateUI();
        }
    }

    /**
     * Updates the editor panes with the entries provided.
     */
    public void update(List<StackoverflowEntry> stackoverflowEntries) {
    	
    	// Ensure that the cache of entry editor panes has sufficient capacity.
    	while (stackoverflowEntries.size() > entryEditorPanes.size())
    		this.initialiseEntryEditorPane();
    	
    	int postPanesUsed = 0;
    	
    	for (StackoverflowEntry stackoverflowEntry : stackoverflowEntries){
    		
    		String htmlText = stackoverflowEntry.toHTMLText();    			
    		
    		JEditorPane entryEditorPane =
    			entryEditorPanes.get(postPanesUsed);
    		entryEditorPane.setText(htmlText);
    		entryEditorPane.setVisible(true);
    		
    		postPanesUsed ++;
    	}
    	
    	for (int i = postPanesUsed; i < entryEditorPanes.size(); i++)
    		entryEditorPanes.get(i).setVisible(false);    	
    }

	/**
     * Updates the first pane with the message and the rest with empty strings
     * If message is null, normal update of posts is carried out
     *
     * @param message the message to display
     */
    public void update(String message) {
        if (message == null)
            return;
        
        if (entryEditorPanes.size() < 1)
        	initialiseEntryEditorPane();

        entryEditorPanes.get(0).setText(message);

        for (int i = 1; i < entryEditorPanes.size(); i++)
    		entryEditorPanes.get(i).setVisible(false);    	
    }

    /**
     * Listener for opening browser on hyperlink clicks.
     */
    private class PostHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                    	IntelijFacade.createErrorNotification(e1.getMessage());
                    }
                }
            }
        }
    }

	public String getTextColour() {
		return textColour;
	}
}
