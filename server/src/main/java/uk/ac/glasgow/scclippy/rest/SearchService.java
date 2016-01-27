package uk.ac.glasgow.scclippy.rest;
 
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.glasgow.scclippy.plugin.lucene.File;
import uk.ac.glasgow.scclippy.plugin.lucene.SearchFiles;
 
@Path("/search")
public class SearchService {
 
	private static String indexPath = "D:/finalIndex"; //TODO
	
	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexSearch(
			@PathParam("query") String query, 
			@DefaultValue("contents") @QueryParam("field") String field, 
			@DefaultValue("5") @QueryParam("posts") Integer postCount) {
		
		File[] output = null;
		try {
			output = SearchFiles.search(indexPath, field, query, postCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (output == null)
			return Response.status(200).entity("").build();
		
		JSONArray arr = new JSONArray();
		for (int i = 0; i < output.length; i++) {
			JSONObject obj = new JSONObject();
			obj.put("id", output[i].getFileName());
			obj.put("content", output[i].getContent());
			obj.put("score", output[i].getScore());
			arr.add(obj);
		}
		JSONObject finalObject = new JSONObject();
		finalObject.put("results", arr);
		
		return Response.status(200).entity(finalObject.toJSONString()).build();
	}
 
}