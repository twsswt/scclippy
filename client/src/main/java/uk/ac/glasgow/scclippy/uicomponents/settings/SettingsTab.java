package uk.ac.glasgow.scclippy.uicomponents.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import uk.ac.glasgow.scclippy.plugin.search.SearchController;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;
import uk.ac.glasgow.scclippy.uicomponents.search.QueryInputPane;

/**
 * Represents a class that contains components in the settings panel/tab
 */
public class SettingsTab extends JBScrollPane {

    private JPanel settingsPanel;

    private JTextField webAppURLJTextField;
	    

    public SettingsTab(Properties properties, SearchController searchController, QueryInputPane queryInputPane, PostsPane postsPane) {
    
    	settingsPanel = new JPanel();

        Border lineBorder = BorderFactory.createLineBorder(JBColor.cyan);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
        this.getViewport().setView(settingsPanel);

        //// General settings
        JPanel generalSettingsPanel = new GeneralSettingsPanel(properties, searchController, queryInputPane, postsPane);
        settingsPanel.add(generalSettingsPanel);

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
    				searchController.setWebServiceURI(webAppURLString);
            	}
            });
        
        JLabel webAppURLLabel = new JLabel("Web service URI:");
        webAppURLLabel.setLabelFor(webAppURLJTextField);
        JPanel webAppURLPanel = new JPanel();
        webAppURLPanel.add(webAppURLLabel);
        webAppURLPanel.add(webAppURLJTextField);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 10;
        gbc.ipady = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        webServiceOptions.add(webAppURLPanel, gbc);
        
    	//// Local Index settings
        JPanel localIndexOptions = new LocalIndexingSettingsPanel(properties, searchController);
        settingsPanel.add(localIndexOptions);
    }

}
