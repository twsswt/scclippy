package uk.ac.glasgow.scclippy.plugin.search;

import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.lucene.StackoverflowLuceneSearcher;

/**
 * Class for searching with a local index and database.
 */
public class LocalIndexedSearch	implements StackoverflowSearch {
	
	private Path indexPath;
	
	public LocalIndexedSearch(Path indexPath){
		this.indexPath = indexPath;
	}
	
	public void setIndexPath(Path indexPath){
		this.indexPath = indexPath;
	}
	

	@Override
	public List<StackoverflowEntry> searchIndex(@NotNull String queryString, int desiredHits) throws SearchException {
		
		try {
			Connection connection = createDatabaseConnection();
			
			StackoverflowLuceneSearcher stackoverflowLuceneSearcher = 
				new StackoverflowLuceneSearcher (connection, indexPath);
			return stackoverflowLuceneSearcher.searchDocuments(queryString, desiredHits);
		} catch (IOException | SQLException e) {
			throw new SearchException(e);
		}		
	}
	
	private Connection createDatabaseConnection() throws SQLException {
		String databaseDriverClassName = "org.postgresql.Driver";
		try {
			Class.forName(databaseDriverClassName);
		} catch (ClassNotFoundException e1) {
			throw new SQLException(
				format("Couldn't find database driver for SQL connection [%s].",databaseDriverClassName));
		}
		
		String dbURL =  "localhost:5432/stackoverflow";
		String dbUsername = "stackoverflow";
		String dbPassword = "deepdarkw00d";
		
		return 
				DriverManager.getConnection(dbURL, dbUsername, dbPassword);
	}

}
