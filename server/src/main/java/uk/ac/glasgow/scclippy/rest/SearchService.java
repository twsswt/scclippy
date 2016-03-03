package uk.ac.glasgow.scclippy.rest;
 
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
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

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneIndexer;
import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneSearcher;
 
@Path("/search")
public class SearchService {
	
	private java.nio.file.Path indexAbsolutePath;
	
	@Context
	public ServletContext servletContext;
		
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response indexSearch(
			@PathParam("query") String query, 
			@DefaultValue("Body") @QueryParam("field") String field, 
			@DefaultValue("5") @QueryParam("posts") Integer postCount) {
		
		indexAbsolutePath = 
			Paths.get(servletContext.getInitParameter("luceneIndexPath")).toAbsolutePath();

		Connection connection;
		try {
			connection = createDatabaseConnection();
		} catch (SQLException e) {
			return Response.status(200).entity("Couldn't connect to Stackoverflow database.").build();
		}
				
		if (!indexExists()){
			prepareLuceneIndex(connection);		
			return Response.status(200).entity("Search index is not currently available.").build();
		}
		
		return createSearchResponse(connection, query, postCount);
		
	}

	private Connection createDatabaseConnection() throws SQLException {
		String databaseDriverClassName = "org.postgresql.Driver";
		try {
			Class.forName(databaseDriverClassName);
		} catch (ClassNotFoundException e1) {
			throw new SQLException(
				format("Couldn't find database driver for SQL connection [%s].",databaseDriverClassName));
		}
		
		String dbURL = servletContext.getInitParameter("dbURL");
		String dbUsername = servletContext.getInitParameter("dbUsername");
		String dbPassword = servletContext.getInitParameter("dbPassword");
		
		return 
				DriverManager.getConnection(dbURL, dbUsername, dbPassword);
	}

	private Response createSearchResponse(Connection connection, String query, Integer postCount) {

		List<StackoverflowEntry> searchResults = null;
		try {
			StackoverflowLuceneSearcher stackoverflowLuceneSearcher = 
				new StackoverflowLuceneSearcher(connection, indexAbsolutePath);	

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
	
	private boolean indexExists() {
		File indexDirectory = indexAbsolutePath.toFile();

		return 	indexDirectory.exists() && 
				indexDirectory.isDirectory() &&
				indexDirectory.listFiles().length > 0;
	}
	
	private void prepareLuceneIndex(
		final Connection connection) {

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

 
}
