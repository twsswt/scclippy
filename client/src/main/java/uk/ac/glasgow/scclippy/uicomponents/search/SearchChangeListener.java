package uk.ac.glasgow.scclippy.uicomponents.search;

import java.util.List;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;

public interface SearchChangeListener {
	
	public void notifySearchChanged (String query, List<StackoverflowEntry> result);

}
