package uk.ac.glasgow.scclippy.rest;
 
import java.io.File;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneSearcher;
import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneIndexer;
 
@Path("/search")
public class SearchService {
	
	private static String indexPathString = "./target/lucene-index"; //TODO - should be configurable in the WAR.
	
	private static final String dbURL = "jdbc:postgresql://localhost:5432/stackoverflow";
	private static final String dbUsername = "stackoverflow";
	private static final String dbPassword = "deepdarkw00d";

	private final java.nio.file.Path indexAbsolutePath;
	private final StackoverflowLuceneSearcher stackoverflowLuceneSearcher;
	
	public SearchService (){
		
		
		
		indexAbsolutePath = Paths.get(indexPathString).toAbsolutePath();
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		prepareLuceneIndex(connection);
		stackoverflowLuceneSearcher = 
			new StackoverflowLuceneSearcher(connection, indexAbsolutePath);	

	}

	private void prepareLuceneIndex(
		final Connection connection) {

		if (!indexExists())
			new Thread() {
				public void run() {
					StackoverflowLuceneIndexer luceneIndexer =
						new StackoverflowLuceneIndexer(
							connection, indexAbsolutePath);
					try {
						luceneIndexer.indexDocuments();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}.start();

	}
	
	private boolean indexExists() {
		File indexDirectory = indexAbsolutePath.toFile();

		return 	indexDirectory.exists() && 
				indexDirectory.isDirectory() &&
				indexDirectory.listFiles().length > 0;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexSearch(
			@PathParam("query") String query, 
			@DefaultValue("Body") @QueryParam("field") String field, 
			@DefaultValue("5") @QueryParam("posts") Integer postCount) {
		
		if (stackoverflowLuceneSearcher == null || !indexExists())
			return Response.status(200).entity("Search index is not currently available.").build();

		List<StackoverflowEntry> searchResults = null;
		try {
			searchResults = stackoverflowLuceneSearcher.searchDocuments(query, postCount);
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
