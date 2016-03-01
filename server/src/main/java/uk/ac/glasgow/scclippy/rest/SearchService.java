package uk.ac.glasgow.scclippy.rest;
 
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.ac.glasgow.scclippy.lucene.LuceneFacade;
 
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

		TopDocs topDocs;
		try {
			topDocs = luceneFacade.searchDocuments(query, postCount);
		} catch (IOException e) {
			return Response.status(200).entity("Search index is not currently available.").build();
		} catch (ParseException e) {
			return Response.status(200).entity("Invalid query.").build();
		}			
		
		JSONArray records = new JSONArray();
		
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			JSONObject record = new JSONObject();
			
			Document document;
			try {
				document = luceneFacade.getDocument(scoreDoc.doc);
				
				IndexableField body = document.getField("Body");
				
				record.put("id", document.getField("Id").stringValue());
				record.put("body",body == null?"":body.stringValue());
				record.put("score", scoreDoc.score);
				
				records.add(record);

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		JSONObject finalObject = new JSONObject();
		finalObject.put("results", records);
		
		return Response.status(200).entity(finalObject.toJSONString()).build();
	}
 
}