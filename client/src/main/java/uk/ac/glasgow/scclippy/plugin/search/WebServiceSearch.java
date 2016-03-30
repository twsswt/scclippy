package uk.ac.glasgow.scclippy.plugin.search;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;

import static java.lang.String.format;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for searching the plugin's associated web service managed index and database.
 */
public class WebServiceSearch implements StackoverflowSearch {

	private String webServiceURI;
	
	public WebServiceSearch (String webServiceURI){
		this.webServiceURI = webServiceURI;
	}
	
	public void setWebServiceURI (String webServiceURI){
		this.webServiceURI = webServiceURI;
	}
	
    public List<StackoverflowEntry> searchIndex(@NotNull String query, int posts) throws SearchException {

        try {
			query = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SearchException(e);
		}
        
        String queryURITemplate = "%s%s?posts%d";
        String queryURI = format(queryURITemplate, webServiceURI, query, posts);
        
        JSONObject webserviceQueryResult = JSONContentURLProcessing.readJsonFromUrl(queryURI);

        if (webserviceQueryResult == null) {
            throw new SearchException("Query failed. Check connection to server.");
        }

        List<StackoverflowEntry> result = new ArrayList<StackoverflowEntry>();
        
        JSONArray items = webserviceQueryResult.getJSONArray("result");
        
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = (JSONObject) items.get(i);
            
            String postId = item.getString("id");
            String parentId = item.getString("parentId");
            String body = item.getString("body");
            int score = item.getInt("score");
            
            StackoverflowEntry stackoverflowEntry = 
            	new StackoverflowEntry(postId, parentId, score, body);
			result.add(stackoverflowEntry);
            
        }
        return result;        	
    }
}
