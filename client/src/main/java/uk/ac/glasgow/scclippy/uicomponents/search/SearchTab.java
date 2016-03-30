package uk.ac.glasgow.scclippy.uicomponents.search;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import uk.ac.glasgow.scclippy.plugin.search.SearchController;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class SearchTab extends JPanel {
			    
    private QueryInputPane queryInputPane;
    private PostsPane postsPane;
    private SearchTopPanel searchTopPanel;

    public SearchTab(SearchController searchController) {

    	SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        queryInputPane = new QueryInputPane(searchController);
        postsPane = new PostsPane();
        PostsScrollPane postsScrollPane = new PostsScrollPane(postsPane, searchController);
        searchController.addSearchChangeListener(postsPane);
        
        searchTopPanel = new SearchTopPanel(searchController, queryInputPane);
        
        springLayout.putConstraint(SpringLayout.NORTH, searchTopPanel, 5, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, searchTopPanel, 5, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.WEST, searchTopPanel, 5, SpringLayout.WEST, this);
        
        springLayout.putConstraint(SpringLayout.NORTH, queryInputPane, 5, SpringLayout.SOUTH, searchTopPanel);
        springLayout.putConstraint(SpringLayout.EAST, queryInputPane, 5, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.WEST, queryInputPane, 5, SpringLayout.WEST, this);
        
        springLayout.putConstraint(SpringLayout.NORTH, postsScrollPane, 5, SpringLayout.SOUTH, queryInputPane);
        springLayout.putConstraint(SpringLayout.EAST, postsScrollPane, 5, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.WEST, postsScrollPane, 5, SpringLayout.WEST, this);
 
        springLayout.putConstraint(SpringLayout.SOUTH, postsScrollPane, 5, SpringLayout.SOUTH, this);
         
        add(searchTopPanel);
        add(queryInputPane);
        add(postsScrollPane);
    }

    public PostsPane getPostsPane() {
        return postsPane;
    }

	public QueryInputPane getQueryInputPane() {
		return queryInputPane;
	}

}
