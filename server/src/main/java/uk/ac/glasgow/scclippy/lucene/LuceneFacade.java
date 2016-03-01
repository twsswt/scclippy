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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Lock;
import org.json.simple.JSONObject;

/**
 * Utility class for indexing the Stackoverflow Postgres Database.s
 */
public class LuceneFacade {

	private int documentsIndexed;
	
	private final Connection connection;
	
	private final Path indexDirectoryPath;
	
	public LuceneFacade (Connection connection, Path indexDirectoryPath) {
		this.connection = connection;		
		this.indexDirectoryPath = indexDirectoryPath;
		
		if (!indexExists()){
			new Thread (){
				public void run (){
					try {
						indexDocuments();
					} catch (IOException | SQLException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}	

	private void indexDocuments() throws IOException, SQLException {
						
		IndexWriter indexWriter = createIndexWriter();
		
		Statement statement = connection.createStatement();
		
		ResultSet resultSet = 
			statement.executeQuery(
				"SELECT Id,Body,LastEditDate FROM posts WHERE tags LIKE '%<java>%' limit 1000");
				
		while (resultSet.next()){
						
			Document document = createDocument(resultSet);
			indexWriter.addDocument(document);
			indexWriter.commit();
			documentsIndexed ++;
			
		}
		resultSet.close();
		statement.close();
		indexWriter.close();
		System.out.println("Closed index.");
	}

	private boolean indexExists() {
		File indexDirectory = indexDirectoryPath.toFile();

		return 	indexDirectory.exists() && 
				indexDirectory.isDirectory() &&
				indexDirectory.listFiles().length > 0;
	}
	
	private boolean indexIsBeingWritten (){
		try {
			FSDirectory indexDirectory = FSDirectory.open(indexDirectoryPath);
			Lock lock = indexDirectory.obtainLock("write.lock");
			lock.close();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		
	}
	
	private IndexWriter createIndexWriter() throws IOException {
		Directory indexDirectory = FSDirectory.open(indexDirectoryPath);
		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE);		
		IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
		return indexWriter;
	}

	private Document createDocument(ResultSet resultSet) throws SQLException {
		
		String body = resultSet.getString("Body");
		String postId = resultSet.getString("Id");
		Timestamp lastEditDate = resultSet.getTimestamp("LastEditDate");
			
		Document document = new Document();
		Field idField = new StringField("Id", postId, Field.Store.YES);
		document.add(idField);
		document.add(new LongField("LastEditDate", lastEditDate==null?0l:lastEditDate.getTime(), Field.Store.NO));
		document.add(new TextField("Body", new StringReader(body)));
		return document;
	}

	public int getNumberofRecordsIndexed() {
		return documentsIndexed;
	}
	
	public List<StackoverflowEntry> searchDocuments (String queryString, Integer desiredHits) throws IOException, ParseException {
		
		if (!indexExists() || indexIsBeingWritten())
			throw new IOException (
				format(
					"The index at path [%s] is not currently available.  Perhaps it is being prepared for use?.",
					indexDirectoryPath));
		
		IndexSearcher searcher = getSearcher();
		Query query = createQuery(queryString);
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

	private Query createQuery(String queryString)
		throws ParseException {
		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		QueryParser parser = new QueryParser("Body", analyzer);
		String line = queryString.trim();
		Query query = parser.parse(QueryParser.escape(line));
		return query;
	}

	private StackoverflowEntry creatStackoverflowEntry(
		IndexSearcher searcher, Statement statement, ScoreDoc scoreDoc) throws IOException {
		
		try {			
			Document document = searcher.doc(scoreDoc.doc);		
			String id = document.getField("Id").stringValue();
			
			ResultSet resultSet = 
				statement.executeQuery(format("SELECT Body FROM posts WHERE Id=[%s]", id));
			
			resultSet.beforeFirst();
			String body = resultSet.getString("Body");
			return
				new StackoverflowEntry(id, scoreDoc.score, body);
			
		} catch (SQLException | IOException e) {
			throw new IOException("Couldn't retrieve information for entry.", e);
		}
	}
	
	private IndexSearcher getSearcher()	throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDirectoryPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}	
	
	
	public class StackoverflowEntry {
	
		public final float score;
		public final String body;
		public final String id;
		
		public StackoverflowEntry(String id, float score, String body){
			this.id = id;
			this.score = score;
			this.body = body;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject toJSONObject () {
			JSONObject record = new JSONObject ();
			record.put("id", id);
			record.put("body", body);
			record.put("score",score);
			return record;
		}
	}
	
}