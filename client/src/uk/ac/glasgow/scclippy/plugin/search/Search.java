package uk.ac.glasgow.scclippy.plugin.search;

import uk.ac.glasgow.scclippy.plugin.lucene.File;

/**
 * Search interface
 */
public abstract class Search {

    protected static File[] files = null;

    public enum SearchType { LOCAL_INDEX, WEB_SERVICE, STACKEXCHANGE_API}
    public static SearchType currentSearchType = SearchType.WEB_SERVICE;

    /**
     * Performs search using a local index
     * @param query query string
     * @param posts number of returned results/posts
     * @throws Exception
     */
    public abstract void search(String query, int posts) throws Exception;

    public static File[] getFiles() {
        return files;
    }
}
