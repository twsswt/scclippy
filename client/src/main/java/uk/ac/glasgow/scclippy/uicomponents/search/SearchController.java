package uk.ac.glasgow.scclippy.uicomponents.search;

import static java.util.Collections.sort;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.search.LocalIndexedSearch;
import uk.ac.glasgow.scclippy.plugin.search.SearchException;
import uk.ac.glasgow.scclippy.plugin.search.SortType;
import uk.ac.glasgow.scclippy.plugin.search.StackoverflowJSONAPISearch;
import uk.ac.glasgow.scclippy.plugin.search.StackoverflowSearch;
import uk.ac.glasgow.scclippy.plugin.search.WebServiceSearch;

public class SearchController {
	
    private Map<String,StackoverflowSearch> searchMechanisms;
    
	private StackoverflowJSONAPISearch stackoverflowJSONAPISearch;
	private WebServiceSearch webServiceSearch;
	private LocalIndexedSearch localIndexedSearch;

	private Map<String,SortType> sortOptions;
	
	private Integer defaultMaximumPosts;
	private Integer extraPostsToRetrieveOnScoll;

	private Integer minimumUpVotes;
	private String query;
	
	private String searchType;
	private String sortKey;

	private Integer currentMaximumPosts;
	
	private List<StackoverflowEntry> searchResult;

	private List<StackoverflowEntry> filteredResult;

	private PostsPane postsPane;
	
	private boolean queryMustBeRefreshed;
	private boolean queryMustBeFiltered;
	private boolean queryMustBeSorted;

    public SearchController() {
    	    	    	
    	searchMechanisms = new HashMap<String, StackoverflowSearch>();

        sortOptions = new HashMap<String,SortType>();
        sortOptions.put("Relevance", SortType.RELEVANCE);
        sortOptions.put("Score", SortType.SCORE);
        
        queryMustBeRefreshed = false;
        queryMustBeFiltered = false;
        queryMustBeSorted = false;
        
    }

	public String[] getSearchOptionKeys() {
		return searchMechanisms.keySet().toArray(new String[]{});
	}

	public String[] getSortOptionKeys() {
		return sortOptions.keySet().toArray(new String[]{});
	}

	public int getRemainingStackoverflowJSONAPICalls() {
		return stackoverflowJSONAPISearch.getRemainingCalls();
	}
	
	public void updateSearchAndSort () throws SearchException {
		
		if (queryMustBeRefreshed){
			
			if (query.equals(""))
				searchResult = new ArrayList<StackoverflowEntry>();
			else {
				StackoverflowSearch currentStackOverflowSearch =
					searchMechanisms.get(searchType);
				
				searchResult = currentStackOverflowSearch.searchIndex(query, currentMaximumPosts);
			}
		}
		
		if (queryMustBeFiltered || queryMustBeRefreshed){		
			filteredResult = new ArrayList<StackoverflowEntry>();
			for (StackoverflowEntry stackoverflowEntry : searchResult)
	    		if (stackoverflowEntry.score > minimumUpVotes)
	    			filteredResult.add(stackoverflowEntry);

		}
		
		if (queryMustBeSorted || queryMustBeFiltered || queryMustBeRefreshed)
			performSort(filteredResult);
		
		queryMustBeSorted = false;
		queryMustBeRefreshed = false;
		queryMustBeFiltered = false;
		System.out.println(filteredResult.size());
		//Notify listeners.
		postsPane.update(filteredResult);
	}
	
	private void performSort (List<StackoverflowEntry> stackoverflowEntries){
		SortType sortType = sortOptions.get(sortKey);
		if (sortType.equals(SortType.SCORE))
    		sort(stackoverflowEntries, scoreComparator);
    }
    
	private static final Comparator<StackoverflowEntry> scoreComparator = new Comparator<StackoverflowEntry>(){

		@Override
		public int compare(StackoverflowEntry o1, StackoverflowEntry o2) {
			return o1.score.compareTo(o2.score);
		}
	};

	public void setIndexPath(Path indexPath) {
		this.localIndexedSearch.setIndexPath(indexPath);
	}
	
	public void setWebServiceURI(String webServiceURI){
		this.webServiceSearch.setWebServiceURI(webServiceURI);
	}
	
	public void setDefaultMaximumPostsToRetrieve (Integer defaultMaximumPosts){
		this.defaultMaximumPosts = defaultMaximumPosts;
	}
	
	public void setExtraPostsToRetrieveOnScroll(Integer extraPostsToRetrieveOnScroll){
		this.extraPostsToRetrieveOnScoll = extraPostsToRetrieveOnScroll;
	}

	public void setMinimumUpVotes(Integer minimumUpVotes) {
		this.minimumUpVotes =  minimumUpVotes;
		queryMustBeFiltered = true;
	}
	
	public void setQuery(String query){
		this.query = query;
		this.queryMustBeRefreshed = true;
		this.resetMaximumPostsToRetrieve();
	}
	
	public void setSearchType(String searchType){
		this.searchType = searchType;
		this.queryMustBeRefreshed = true;
	}
	
	public void setSortType(String sortType){
		queryMustBeSorted = true;
		this.sortKey = sortType;
	}

	public void incrementMaximumPostsToRetrieveOnScroll() {
		this.currentMaximumPosts += extraPostsToRetrieveOnScoll;
		this.queryMustBeRefreshed = true;
	}
	
	public void resetMaximumPostsToRetrieve (){
		this.currentMaximumPosts = defaultMaximumPosts;
	}

	public void addSearchMechanism(
		String string, StackoverflowSearch stackoverflowJSONAPISearch) {
		
		searchMechanisms.put(string, stackoverflowJSONAPISearch);
	}

	public void setPostsPane(PostsPane postsPane) {
		this.postsPane = postsPane;
		
	}
	
}
