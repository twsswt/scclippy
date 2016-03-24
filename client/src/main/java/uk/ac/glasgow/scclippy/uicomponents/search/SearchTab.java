package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.FlowLayout;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.intellij.openapi.ui.ComboBox;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;
import uk.ac.glasgow.scclippy.uicomponents.history.SearchHistoryTab;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class SearchTab {
		
    private JComponent searchPanel;
    private JScrollPane scroll;
    
    private ComboBox searchMechanismComboBox;
	private ComboBox resultSortOptionsComboBox;
    private JLabel stackExchangeSearchRequestsLabel;

    private QueryInputPane queryInputPane;
    private PostsPane postsPane;

	private SearchController searchController;

    public SearchTab(Properties properties, SearchHistoryTab searchHistoryTab) {
    	
        initSearchPanel(properties, searchHistoryTab);
        
    	Path indexPath = Paths.get(properties.getProperty("indexPath")).toAbsolutePath();
        String webServiceURI = properties.getProperty("webServiceURI");
        
        searchController = new SearchController (indexPath, webServiceURI);
    }

    private void initSearchPanel(Properties properties, SearchHistoryTab searchHistoryTab) {
    	
        searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));

        scroll = new SearchPanelScroll(searchPanel, this);

        queryInputPane = new QueryInputPane(searchHistoryTab, this);
        postsPane = new PostsPane();
        
    	stackExchangeSearchRequestsLabel = new JLabel("Unknown requests left.");
        stackExchangeSearchRequestsLabel.setVisible(false);

        String[] searchOptionKeys = searchController.getSearchOptionKeys();
        searchMechanismComboBox = new ComboBox(searchOptionKeys);
        JLabel searchOptionsLabel = new JLabel("Search with");
        searchOptionsLabel.setLabelFor(searchMechanismComboBox);
        searchMechanismComboBox.setSelectedIndex(1);
        
        searchMechanismComboBox.addActionListener(e -> {
            String selectedSearchOption = (String) searchMechanismComboBox.getSelectedItem();

            if (selectedSearchOption.equals("StackExchange API")) {
                stackExchangeSearchRequestsLabel.setVisible(true);
            } else {
                stackExchangeSearchRequestsLabel.setVisible(false);
            }
        });

        String[] sortOptionKeys = searchController.getSortOptionKeys();
        resultSortOptionsComboBox = new ComboBox(sortOptionKeys);
        JLabel sortOptionsLabel = new JLabel("Sort results by");
        sortOptionsLabel.setLabelFor(resultSortOptionsComboBox);
        resultSortOptionsComboBox.setSelectedIndex(0);

        JButton searchWithGoogleButton = new GoogleSearchButton(queryInputPane);

        JButton writeQuestionButton = new AskAQuestionButton();

        // top panel
        JComponent topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(searchOptionsLabel);
        topPanel.add(searchMechanismComboBox);
        topPanel.add(stackExchangeSearchRequestsLabel);
        topPanel.add(sortOptionsLabel);
        topPanel.add(resultSortOptionsComboBox);
        topPanel.add(searchWithGoogleButton);
        topPanel.add(writeQuestionButton);

        // add components to search panel
        searchPanel.add(topPanel);
        searchPanel.add(queryInputPane.getComponent());
        searchPanel.add(postsPane);
    }

    public PostsPane getPostsPane() {
        return postsPane;
    }

    public JScrollPane getScroll() {
        return scroll;
    }
    
	public void updateSearch ()  {
        
		String query = queryInputPane.getQueryText();
        try {
        	
        	String currentSearchKey = (String)searchMechanismComboBox.getSelectedItem();

    		String currentSortKey = (String)resultSortOptionsComboBox.getSelectedItem();
    	    		
			List<StackoverflowEntry> searchAndSortResult =
    			searchController.updateSearchAndSort(query, currentSearchKey, currentSortKey);
	
            int requests = searchController.getRemainingStackoverflowJSONAPICalls();
            stackExchangeSearchRequestsLabel.setText(requests + " requests left.");

            postsPane.update(searchAndSortResult);

        } catch (Exception e) {
        	IntelijFacade.createErrorNotification(e.getMessage());
        }

	}
	
	public QueryInputPane getQueryInputPane() {
		return queryInputPane;
	}

	public SearchController getSearchController() {
		return searchController;
	}
}
