package uk.ac.glasgow.scclippy.plugin.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.ui.InputException;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;

/**
 * Manages the state of search results, including updating searches and sorting according to preference.
 */
public abstract class StackoverflowSearch {

    private List<StackoverflowEntry> lastSearchResult = null;
    
    private int minimumScore;

    public void updateSearch (@NotNull String query, int posts, Integer minimumUpVotes) throws Exception{
    	if (!validateInput(query, posts))
    		throw new InputException("Invalid search query.", null);
    	
		query = query.trim();
    	
    	List<StackoverflowEntry> searchResult = searchIndex(query, posts);
    	
    	List<StackoverflowEntry> filteredResult = new ArrayList<StackoverflowEntry>();
    	
    	for (StackoverflowEntry stackoverflowEntry : searchResult)
    		if (stackoverflowEntry.score > minimumScore)
    			filteredResult.add(stackoverflowEntry);
    	
    	lastSearchResult = filteredResult;
    			
    }
    
    protected abstract List<StackoverflowEntry> searchIndex(@NotNull String query, int posts) throws Exception;

    /**
     * Input validator for search
     * @param query query string
     * @param posts number of posts
     * @return false if invalid input, true otherwise
     */
    private boolean validateInput(String query, int posts) {
        if (query == null || posts <= 0) {
            return false;
        }
        if (query.trim().equals("") || posts > 1000) {
            return false;
        }
        return true;
    }

    public List<StackoverflowEntry> getLastSearchResult() {
        return lastSearchResult;
    }
    
    public void updateSort (SortType sortType){
    	if (sortType.equals(SortType.SCORE))
    		Collections.sort(lastSearchResult, scoreComparator);
    }
    
	private static final Comparator<StackoverflowEntry> scoreComparator = new Comparator<StackoverflowEntry>(){

		@Override
		public int compare(StackoverflowEntry o1, StackoverflowEntry o2) {
			return o1.score.compareTo(o2.score);
		}
	};

}
