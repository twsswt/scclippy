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
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Utility class for indexing the Stackoverflow PostgreSQL Database using Lucene.
 * @author tws
 */
public class StackoverflowLuceneIndexer {
	
	private final Connection connection;
	
	private final Path indexDirectoryPath;
		
	public StackoverflowLuceneIndexer (Connection connection, Path indexDirectoryPath) {
		this.connection = connection;		
		this.indexDirectoryPath = indexDirectoryPath;
	}	

	public void indexDocuments() throws IOException, SQLException {
		
		System.out.println("Started indexing records.");
						
		IndexWriter indexWriter = createIndexWriter();
		
		Statement statement = connection.createStatement();
		
		String questionQueryString = 
			"SELECT Id,Body,LastEditDate FROM posts WHERE tags LIKE '%<java>%'";
		
		processStackoverflowPostQuery(indexWriter, statement, questionQueryString);

		String answerQueryString =
			"SELECT P2.Score As Score, P2.ParentId As ParentId, P2.id As Id, P2.Body As Body " +
			"FROM Posts AS P1 JOIN Posts AS P2 ON P1.Id = P2.ParentId " + 
			"WHERE P1.Tags LIKE '%<java>%'";
		
		processStackoverflowPostQuery(indexWriter, statement, answerQueryString);
		
		statement.close();
		
		indexWriter.close();
		
		System.out.println("Finished indexing records.");
		
	}

	private void processStackoverflowPostQuery(
		IndexWriter indexWriter, Statement statement, String questionQueryString) 
			throws SQLException, IOException {
		
		ResultSet resultSet = 
			statement.executeQuery(questionQueryString);
		
		System.out.println("Completed database query.");
		
		int documentsIndexed = 0;
		
		while (resultSet.next()){
						
			Document document = createIndexDocument(resultSet);
			indexWriter.addDocument(document);
			if (documentsIndexed % 100 == 0){
				System.out.print(".");
				indexWriter.commit();
			}
			documentsIndexed += 100;
			
		}
		resultSet.close();
	}
		
	private IndexWriter createIndexWriter() throws IOException {
		Directory indexDirectory = FSDirectory.open(indexDirectoryPath);
		Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE);		
		IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
		return indexWriter;
	}

	private Document createIndexDocument(ResultSet resultSet) throws SQLException {
		
		String body = resultSet.getString("Body");
		String postId = resultSet.getString("Id");

		Document document = new Document();
		document.add(new StringField("Id", postId, Field.Store.YES));
		document.add(new TextField("Body", new StringReader(body)));
		return document;
		
	}
			
}
