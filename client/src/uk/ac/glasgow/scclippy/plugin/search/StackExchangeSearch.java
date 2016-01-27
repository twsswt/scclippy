package uk.ac.glasgow.scclippy.plugin.search;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.glasgow.scclippy.plugin.lucene.File;
import uk.ac.glasgow.scclippy.plugin.util.StringProcessing;
import uk.ac.glasgow.scclippy.plugin.util.URLProcessing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class for searching with a Stack Exchange API for code excerpts
 */
public class StackExchangeSearch extends Search {

    private final static String STACKEXCHANGE_EXCERPTS_URL = "http://api.stackexchange.com/2.2/search/excerpts?";
    private final static String STACKEXCHANGE_PARAMS = "&sort=relevance&tagged=java&site=stackoverflow";

    /**
     * Performs a search using StackExchange API v2.2
     * @param query query string
     * @return error message or null if successful
     */
    public String search(String query, int posts) {
        query = query.trim();
        if (query.equals(""))
            return "";

        Search.files = null;

        String body = "";
        try {
            body = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = URLProcessing.readJsonFromUrlUsingGZIP(STACKEXCHANGE_EXCERPTS_URL + "body=" + body + STACKEXCHANGE_PARAMS);

        if (json == null) {
            return "Connection problems / Query failed";
        }

        if (json.getInt("quota_remaining") == 0) {
            return "Cannot make more requests today";
        }

        JSONArray items = json.getJSONArray("items");
        if (items.length() == 0) {
            return "No results. Consider changing the query " +
                    "(e.g. removing variable names) or using another option";
        }

        files = new File[items.length()];
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            String id = "" + item.getInt(item.getString("item_type") + "_id");
            String content = StringProcessing.textToHTML(item.getString("excerpt")) + "<br/>";
            int score = item.getInt("score");

            files[i] = new File(id, content, score);
        }
        Search.currentSearchType = SearchType.STACKEXCHANGE_API;

        return null;
    }

}
