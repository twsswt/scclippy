package uk.ac.glasgow.scclippy.lucene;
/*

 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * Utility class for searching the Stackoverflow PostgreSQL Database via a Lucene index of documents.
 * @author tws
 */
public class StackoverflowLuceneSearcher {
	
	private final Connection connection;
	
	private final Path indexDirectoryPath;
	
	public StackoverflowLuceneSearcher (Connection connection, Path indexDirectoryPath) {
		this.connection = connection;		
		this.indexDirectoryPath = indexDirectoryPath;		
	}	

	public List<StackoverflowEntry> searchDocuments (String queryString, Integer desiredHits) throws IOException, ParseException {

		IndexSearcher searcher = getSearcher();
		Query query = createLuceneQuery(queryString);
		TopDocs topDocs = searcher.search(query, desiredHits);
		
		List<StackoverflowEntry> result = new ArrayList<StackoverflowEntry>();
		
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs){
				StackoverflowEntry stackoverflowEntry = 
					creatStackoverflowEntry(searcher, statement, scoreDoc);
				result.add(stackoverflowEntry);
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;

	}
	
	private IndexSearcher getSearcher() throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDirectoryPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	private Query createLuceneQuery(String queryString)
		throws ParseException {
		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		QueryParser parser = new QueryParser("Body", analyzer);
		String line = queryString.trim();
		Query query = parser.parse(QueryParser.escape(line));
		return query;
	}

	private synchronized StackoverflowEntry creatStackoverflowEntry(
		IndexSearcher searcher, Statement statement, ScoreDoc scoreDoc) throws IOException {
		
		try {			
			Document document = searcher.doc(scoreDoc.doc);		
			String id = document.getField("Id").stringValue();
			
			ResultSet resultSet = 
				statement.executeQuery(format("SELECT ParentId,Body,Score FROM posts WHERE Id='%s'", id));
			
			resultSet.next();
			String parentId = resultSet.getString("parentId");
			String body = resultSet.getString("Body");
			int score = resultSet.getInt("Score");
			
			return
				new StackoverflowEntry(id, parentId, score, body);
			
		} catch (SQLException | IOException e) {
			throw new IOException("Couldn't retrieve information for entry.", e);
		}
	}
		
}
