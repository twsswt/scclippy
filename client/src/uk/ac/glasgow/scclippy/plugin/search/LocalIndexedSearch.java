package uk.ac.glasgow.scclippy.plugin.search;

import org.apache.lucene.queryparser.classic.ParseException;
import uk.ac.glasgow.scclippy.plugin.lucene.SearchFiles;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;

import java.io.IOException;

/**
 * Class for searching with a local index
 */
public class LocalIndexedSearch extends Search {

    private final static String DEFAULT_FIELD = "contents";

    /**
     * Updates the files with the search from a local index
     * @See Search#search
     */
    public void search(String query, int posts) throws Exception {
        query = query.trim();
        if (query.equals("")) {
            files = null;
            return;
        }

        try {
            files = SearchFiles.search(
                    Settings.indexPath,
                    DEFAULT_FIELD,
                    query,
                    posts
            );
        } catch (IOException e) {
            throw new Exception("Searching failed due to I/O problems. Try again.");
        } catch (ParseException e) {
            throw new Exception("Searching failed. Try again.");
        }

        Search.currentSearchType = SearchType.LOCAL_INDEX;
    }
}
