package uk.ac.glasgow.scclippy.plugin.search;

import org.apache.lucene.queryparser.classic.ParseException;
import org.jetbrains.annotations.NotNull;
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
    public void search(@NotNull String query, int posts) throws Exception {
        if (!inputValidator(query, posts)) {
            files = null;
            return;
        }
        query = query.trim();

        try {
            files = SearchFiles.search(
                    Settings.indexPath,
                    DEFAULT_FIELD,
                    query,
                    posts
            );
        } catch (IOException e) {
            throw new Exception("Searching failed due to I/O problems. Try again.");
        }

        Search.currentSearchType = SearchType.LOCAL_INDEX;
    }
}
