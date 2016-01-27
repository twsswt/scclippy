package uk.ac.glasgow.scclippy.plugin.search;

import uk.ac.glasgow.scclippy.plugin.lucene.File;

/**
 * Search interface
 */
public abstract class Search {

    public static File[] files = null;

    public enum SearchType { LOCAL_INDEX, WEB_SERVICE, STACKEXCHANGE_API }

    public static SearchType currentSearchType = SearchType.WEB_SERVICE;

    public abstract String search(String query, int posts);
}
