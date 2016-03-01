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
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
		
		if (!indexIsReady()){
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
				"SELECT Id,Body,LastEditDate FROM posts WHERE tags LIKE '%<java>%'");
		
		while (resultSet.next()){
						
			Document document = createDocument(resultSet);
			documentsIndexed ++;
			indexWriter.addDocument(document);		
			
		}
	}

	private boolean indexIsReady() {
		File indexDirectory = indexDirectoryPath.toFile();

		return 	indexDirectory.exists() && 
				indexDirectory.isDirectory() &&
				indexDirectory.listFiles().length > 0 &&
				!asList(indexDirectory.list()).contains("write.lock");
				
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
		Long lastEditDate = resultSet.getLong("LastEditDate");
		
		Document document = new Document();
		Field pathField = new StringField("Id", postId, Field.Store.YES);
		document.add(pathField);
		document.add(new LongField("LastEditDate", lastEditDate, Field.Store.NO));
		document.add(new TextField("Body", new StringReader(body)));
		return document;
	}

	public int getNumberofRecordsIndexed() {
		return documentsIndexed;
	}
	
	public TopDocs searchDocuments (String queryString, Integer desiredHits) throws IOException, ParseException {
		
		if (!indexIsReady())
			throw new IOException (
				format(
					"The index at path [%s] is not currently available.  Perhaps it is being prepared for use?.",
					indexDirectoryPath));
		
		IndexSearcher searcher = getSearcher();

		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		QueryParser parser = new QueryParser("Body", analyzer);
		String line = queryString.trim();
		Query query = parser.parse(QueryParser.escape(line));

		return searcher.search(query, desiredHits);

	}
	
	public Document getDocument(int documentIndex) throws IOException{
		return getSearcher().doc(documentIndex);
	}

	private IndexSearcher getSearcher()	throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDirectoryPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
	
}