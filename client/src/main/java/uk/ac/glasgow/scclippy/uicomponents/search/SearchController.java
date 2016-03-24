package uk.ac.glasgow.scclippy.uicomponents.search;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.plugin.search.LocalIndexedSearch;
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
	
	private Integer maximumPosts;
	private Integer minimumUpVotes;

    public SearchController(Path indexPath, String webServiceURI) {
    	    	    	
    	searchMechanisms = new HashMap<String, StackoverflowSearch>();
        localIndexedSearch = new LocalIndexedSearch(indexPath);
		searchMechanisms.put("Local Index", localIndexedSearch);
        
        webServiceSearch = new WebServiceSearch(webServiceURI);
		searchMechanisms.put("Web Service", webServiceSearch);
        
        stackoverflowJSONAPISearch = new StackoverflowJSONAPISearch();
		searchMechanisms.put("StackExchange API", stackoverflowJSONAPISearch);

        sortOptions = new HashMap<String,SortType>();
        sortOptions.put("Relevance", SortType.RELEVANCE);
        sortOptions.put("Score", SortType.SCORE);
        
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
	
	public List<StackoverflowEntry> updateSearchAndSort (String query, String searchKey, String sortKey) throws Exception{
		StackoverflowSearch currentStackOverflowSearch =
			searchMechanisms.get(searchKey);
		
		currentStackOverflowSearch.updateSearch(query, maximumPosts, minimumUpVotes);

		SortType sortType = sortOptions.get(sortKey);
		currentStackOverflowSearch.updateSort(sortType);
		
		return currentStackOverflowSearch.getLastSearchResult();
	}

	public void setIndexPath(Path indexPath) {
		this.localIndexedSearch.setIndexPath(indexPath);
	}
	
	public void setWebServiceURI(String webServiceURI){
		this.webServiceSearch.setWebServiceURI(webServiceURI);
	}
	
	public void setMaximumPosts (Integer maximumPosts){
		this.maximumPosts = maximumPosts;
	}

	public void setMinimumUpVotes(
		Integer minimumUpVotes) {
		this.minimumUpVotes =  minimumUpVotes;
		
	}



}
