package uk.ac.glasgow.scclippy.plugin.search;

import uk.ac.glasgow.scclippy.plugin.lucene.SearchFiles;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;

/**
 * Class for searching with a local index
 */
public class LocalIndexedSearch extends Search {

    private final static String DEFAULT_FIELD = "contents";

    /**
     * Performs search using a local index
     * @param query query string
     * @param posts number of returned results/posts
     * @return error message or null if successful
     */
    public String search(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return "";

        try {
            files = SearchFiles.search(
                    Settings.indexPath,
                    DEFAULT_FIELD,
                    query,
                    posts
            );
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
        }
        Search.currentSearchType = SearchType.LOCAL_INDEX;

        return null;
    }
}
