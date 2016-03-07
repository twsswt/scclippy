package uk.ac.glasgow.scclippy.rest;
 
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.glasgow.scclippy.lucene.LuceneFacade;
import uk.ac.glasgow.scclippy.lucene.LuceneFacade.StackoverflowEntry;
 
@Path("/search")
public class SearchService {
 
	private static String indexPathString = "./target/lucene-index"; //TODO - should be configurable in the WAR.
	
	private static final String dbURL = "jdbc:postgresql://localhost:5432/stackoverflow";
	private static final String dbUsername = "stackoverflow";
	private static final String dbPassword = "deepdarkw00d";
	
	private LuceneFacade luceneFacade;
	
	public SearchService (){
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
			luceneFacade = new LuceneFacade(connection, Paths.get(indexPathString).toAbsolutePath());
		} catch (SQLException e) {
			e.printStackTrace();
		} 

	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexSearch(
			@PathParam("query") String query, 
			@DefaultValue("Body") @QueryParam("field") String field, 
			@DefaultValue("5") @QueryParam("posts") Integer postCount) {
		
		if (luceneFacade == null)
			return Response.status(200).entity("Couldn't connect to search index.").build();

		List<StackoverflowEntry> searchResults = null;
		try {
			searchResults = luceneFacade.searchDocuments(query, postCount);
			JSONArray records = new JSONArray();
			
			for (StackoverflowEntry stackoverflowEntry: searchResults)
				records.add(stackoverflowEntry.toJSONObject());
			
			JSONObject finalObject = new JSONObject();
			finalObject.put("results", records);
			
			return Response.status(200).entity(finalObject.toJSONString()).build();
		
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(200).entity("Search index is not currently available.").build();
		} catch (ParseException e) {
			return Response.status(200).entity("Invalid query.").build();
		}
		
	}
 
}
