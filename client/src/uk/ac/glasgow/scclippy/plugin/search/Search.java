package uk.ac.glasgow.scclippy.plugin.search;

import org.jetbrains.annotations.NotNull;
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
    public abstract void search(@NotNull String query, int posts) throws Exception;

    /**
     * Input validator for search
     * @param query query string
     * @param posts number of posts
     * @return false if invalid input, true otherwise
     */
    protected boolean inputValidator(String query, int posts) {
        if (query == null || posts <= 0) {
            return false;
        }
        if (query.trim().equals("") || posts > 1000) {
            return false;
        }
        return true;
    }

    public static File[] getFiles() {
        return files;
    }
}
