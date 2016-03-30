package uk.ac.glasgow.scclippy.uicomponents.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.intellij.ui.JBColor;

import uk.ac.glasgow.scclippy.plugin.search.SearchController;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;
import uk.ac.glasgow.scclippy.uicomponents.search.QueryInputPane;

public class GeneralSettingsPanel extends JPanel {

	public GeneralSettingsPanel (Properties properties, SearchController searchController, QueryInputPane inputPane, PostsPane postsPane){
       
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 10;
        gbc.ipady = 10;

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        this.setBorder(BorderFactory.createTitledBorder(lineBorder, "General settings"));

        // resizable input
        JCheckBox resizableInputPaneCheckBox = 
        	new ResizableInputPaneCheckBox(
        		properties, inputPane, "Auto-resizable query input text area");
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(resizableInputPaneCheckBox, gbc);

        // highlighted results
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox indexSearchHighlightCheckBox = 
        	new IndexSearchHighlightCheckBox(
        		postsPane, "Highlighted results");
        this.add(indexSearchHighlightCheckBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        JPanel defaultPostsCountPanel = 
        	new PropertyPreservingTextFieldPanel(properties, "defaultMaximumPostsToRetrieve", 
        		"Initial number of posts after search.");
        this.add(defaultPostsCountPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        PropertyPreservingTextFieldPanel extraPostsToRetrieveOnScrollingDownPanel = 
        	new PropertyPreservingTextFieldPanel(
        		properties, "extraPostsToRetrieveOnScroll",  "Extra number of posts after scrolling down.");
        this.add(extraPostsToRetrieveOnScrollingDownPanel, gbc);

        PropertyPreservingTextFieldPanel postsTextColourPanel =
        	new PropertyPreservingTextFieldPanel(
        		properties, "textColour", 
        		"Colour of text (hex colours e.g. #FF0000; colours by name e.g. red)");
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.add(postsTextColourPanel, gbc);
        postsTextColourPanel.addDocumentListener(new PostColourChangerDocumentListener(postsPane));
        
        JPanel minimumUpVotesPanel = 
        	new PropertyPreservingTextFieldPanel (
        		properties, "minimumUpVotes", "Minimum up votes.");
        this.add(minimumUpVotesPanel);

	}
	
	private class ResizableInputPaneCheckBox extends JCheckBox {
        public ResizableInputPaneCheckBox(Properties properties, QueryInputPane inputPane, String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    properties.setProperty("resizableInputArea", "true");
                    inputPane.setInputAreaIsResizable(true);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    properties.setProperty("resizableInputArea", "false");
                    inputPane.setInputAreaIsResizable(false);
                }
            });
        }
    }

    private class IndexSearchHighlightCheckBox extends JCheckBox {
        
    	public IndexSearchHighlightCheckBox(PostsPane postsPane, String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    postsPane.enableHighlights();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    postsPane.disableHighlights();
                }
            });
        }
    	
    }

}
