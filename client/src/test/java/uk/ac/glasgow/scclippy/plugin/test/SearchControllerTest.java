package uk.ac.glasgow.scclippy.plugin.test;

import org.easymock.EasyMockRule;

import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.search.SearchController;
import uk.ac.glasgow.scclippy.plugin.search.StackoverflowSearch;
import uk.ac.glasgow.scclippy.uicomponents.search.PostsPane;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;

public class SearchControllerTest extends EasyMockSupport {

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	public SearchController searchController;
	
	@Mock
	public StackoverflowSearch mockSearch;
	
	@Before
	public void setUp() throws Exception {
				
		searchController = new SearchController();
		searchController.setMinimumUpVotes(1);
	}

	@Test
	public void testUpdateSearchAndSort() throws Exception {
		
		expect(mockSearch.searchIndex("String",1)).andReturn(new ArrayList<StackoverflowEntry>());
		expect(mockSearch.searchIndex("String",2)).andReturn(new ArrayList<StackoverflowEntry>());
		
		replayAll ();
		
		searchController.addSearchMechanism("StackExchange API", mockSearch);
		
		searchController.setDefaultMaximumPostsToRetrieve(1);
		searchController.resetMaximumPostsToRetrieve();
		searchController.setExtraPostsToRetrieveOnScroll(1);
		searchController.setMinimumUpVotes(0);

		PostsPane postsPane = new PostsPane();
		
		searchController.setSearchType("StackExchange API");
		searchController.setSortType("Score");
		searchController.setQuery("String");
		searchController.addSearchChangeListener(postsPane);
		searchController.incrementMaximumPostsToRetrieveOnScroll();
		
		verifyAll();
	}

}
