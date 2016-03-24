package uk.ac.glasgow.scclippy.plugin;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.scclippy.plugin.search.StackoverflowJSONAPISearch;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchController;

public class SearchControllerTest {

	private SearchController searchController;
	
	@Before
	public void setUp() throws Exception {
				
		searchController = new SearchController();
		searchController.setMinimumUpVotes(1);
	}


	@Test
	public void testUpdateSearchAndSort() throws Exception {
		
        StackoverflowJSONAPISearch stackoverflowJSONAPISearch = new StackoverflowJSONAPISearch();
		searchController.addSearchMechanism("StackExchange API", stackoverflowJSONAPISearch);
		
		searchController.setDefaultMaximumPostsToRetrieve(1);
		searchController.resetMaximumPostsToRetrieve();
		searchController.setExtraPostsToRetrieveOnScroll(1);
		searchController.setMinimumUpVotes(0);

		PostsPane postsPane = new PostsPane();
		
		searchController.setSearchType("StackExchange API");
		searchController.setSortType("Score");
		searchController.setQuery("String");
		searchController.setPostsPane(postsPane);
		searchController.updateSearchAndSort();
		searchController.incrementMaximumPostsToRetrieveOnScroll();
		searchController.updateSearchAndSort();
		
		
	}

}
