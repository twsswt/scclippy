package uk.ac.glasgow.scclippy.plugin.search;

import java.util.List;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;

/**
 * Defines a general purpose stackoverflow search interfaces.
 */
public interface StackoverflowSearch {

	public abstract List<StackoverflowEntry> searchIndex(String query, int maximumPostsToRetrieve) throws SearchException;

}
