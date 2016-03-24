package uk.ac.glasgow.scclippy.uicomponents.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;

/**
 * Represents a class that contains components in the settings panel/tab
 */
public class SettingsTab extends JBScrollPane {

    private JPanel settingsPanel;

    private JTextField webAppURLJTextField;
	
    private SearchTab searchTab;
    
    private Properties properties;

    public SettingsTab(Properties properties, SearchTab searchTab) {    	
    	this.searchTab = searchTab;
    	this.properties = properties;
        initSettingsPanel();
    }

    private void initSettingsPanel() {
    	
    	settingsPanel = new JPanel();

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));

        //// General settings
        JPanel generalSettings = new JPanel();
        generalSettings.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
//        generalSettings.setLayout(new BoxLayout(generalSettings, BoxLayout.PAGE_AXIS));
        generalSettings.setBorder(BorderFactory.createTitledBorder(lineBorder, "General settings"));
        settingsPanel.add(generalSettings);

        // resizable input
        JCheckBox resizableInputPaneCheckBox = new ResizableInputPaneCheckBox("Auto-resizable query input text area");
        gbc.gridx = 0;
        gbc.gridy = 0;
        generalSettings.add(resizableInputPaneCheckBox, gbc);

        // highlighted results
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox indexSearchHighlightCheckBox = new IndexSearchHighlightCheckBox("Highlighted results");
        generalSettings.add(indexSearchHighlightCheckBox, gbc);

        // Default post count
        JTextField defaultPostCountTextField = new JTextField(properties.getProperty("defaultPostCount"));
        defaultPostCountTextField.getDocument().addDocumentListener(
        	new PropertyPersistingDocumentListener(properties, "defaultPostCount"));
       
        JLabel defaultPostCountLabel = new JLabel("Total number of posts after search (influences efficiency)");
        defaultPostCountLabel.setLabelFor(defaultPostCountTextField);
        
        JPanel defaultPostsCountPanel = new JPanel();
        defaultPostsCountPanel.add(defaultPostCountLabel);
        defaultPostsCountPanel.add(defaultPostCountTextField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        generalSettings.add(defaultPostsCountPanel, gbc);

        // Max post count
        JTextField maxPostCountTextField = new JTextField();
        maxPostCountTextField.setText(properties.getProperty("maxPostCount"));
        
        maxPostCountTextField.getDocument().addDocumentListener(
        	new PropertyPersistingDocumentListener(properties, "maxPostCount"){
        	@Override
        	public void saveChange (DocumentEvent e){
        		super.saveChange(e);
				Integer maximumPosts = Integer.parseInt(maxPostCountTextField.getText());
				searchTab.getSearchController().setMaximumPosts(maximumPosts);
        	}
        });

        JLabel maxPostCountLabel = new JLabel("Total number of posts after scrolling down search (influences efficiency)");
        maxPostCountLabel.setLabelFor(maxPostCountTextField);
        
        JPanel maximumPostsRetrievedPanel = new JPanel();
        maximumPostsRetrievedPanel.add(maxPostCountLabel);
        maximumPostsRetrievedPanel.add(maxPostCountTextField);
        gbc.gridx = 0;
        gbc.gridy = 3;
        generalSettings.add(maximumPostsRetrievedPanel, gbc);

        // Colour of text in posts
        JTextField postsTextColourTextField = new PostColourChangerJTextField(properties, searchTab.getPostsPane());
        JLabel postsTextColourLabel = new JLabel("Colour of text (hex colours e.g. #FF0000; colours by name e.g. red)");
        postsTextColourLabel.setLabelFor(postsTextColourTextField);
        JPanel postsTextColourPanel = new JPanel();
        postsTextColourPanel.add(postsTextColourLabel);
        postsTextColourPanel.add(postsTextColourTextField);
        gbc.gridx = 0;
        gbc.gridy = 4;
        generalSettings.add(postsTextColourPanel, gbc);
        
        // minimum up votes for filtering results.        
        JTextField minimumUpVotesJTextField = new JTextField();
        minimumUpVotesJTextField.setText(properties.getProperty("minimumScore"));
        minimumUpVotesJTextField.getDocument().addDocumentListener(
        	new PropertyPersistingDocumentListener(properties, "minimumScore"){
            	@Override
            	public void saveChange (DocumentEvent e){
            		super.saveChange(e);
    				Integer minimumUpVotes = Integer.parseInt(minimumUpVotesJTextField.getText());
    				searchTab.getSearchController().setMinimumUpVotes(minimumUpVotes);
            	}
            });
        
        JLabel minimumUpVotesLabel = new JLabel ("Minimum up votes.");
        JPanel minimumUpVotesPanel = new JPanel();
        minimumUpVotesPanel.add(minimumUpVotesLabel);
        minimumUpVotesPanel.add(minimumUpVotesJTextField);
        minimumUpVotesJTextField.setColumns(3);
        minimumUpVotesJTextField.setToolTipText("Minimum upvotes filter of results");
        generalSettings.add(minimumUpVotesPanel);


        //// Web service settings
        JPanel webServiceOptions = new JPanel(new GridBagLayout());
        webServiceOptions.setBorder(BorderFactory.createTitledBorder(lineBorder, "Web service options"));
        settingsPanel.add(webServiceOptions);

        // Web App RESTful URL
        webAppURLJTextField = new JTextField();
        webAppURLJTextField.setText(properties.getProperty("webServiceURI"));
        webAppURLJTextField.getDocument().addDocumentListener(
        	new PropertyPersistingDocumentListener(properties, "webServiceURI"){
            	@Override
            	public void saveChange (DocumentEvent e){
            		super.saveChange(e);
    				String webAppURLString = webAppURLJTextField.getText();
    				searchTab.getSearchController().setWebServiceURI(webAppURLString);
            	}
            });
        
        JLabel webAppURLLabel = new JLabel("Web service URI:");
        webAppURLLabel.setLabelFor(webAppURLJTextField);
        JPanel webAppURLPanel = new JPanel();
        webAppURLPanel.add(webAppURLLabel);
        webAppURLPanel.add(webAppURLJTextField);
        gbc.gridx = 0;
        gbc.gridy = 0;
        webServiceOptions.add(webAppURLPanel, gbc);
        
    	//// Local Index settings
        JPanel localIndexOptions = new LocalIndexingSettingsPanel(properties, searchTab);
        settingsPanel.add(localIndexOptions);
    }

    

    private class ResizableInputPaneCheckBox extends JCheckBox {
        public ResizableInputPaneCheckBox(String s) {
            super(s);
            setSelected(true);
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    properties.setProperty("resizableInputArea", "true");
                    searchTab.getQueryInputPane().setInputAreaIsResizable(true);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    properties.setProperty("resizableInputArea", "false");
                    searchTab.getQueryInputPane().setInputAreaIsResizable(false);
                }
            });
        }
    }

    private class IndexSearchHighlightCheckBox extends JCheckBox {
        public IndexSearchHighlightCheckBox(String s) {
            super(s);
            setSelected(true);
            PostsPane posts = searchTab.getPostsPane();
            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    posts.enableHighlights();
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    posts.disableHighlights();
                }
            });
        }
    }
    
}
