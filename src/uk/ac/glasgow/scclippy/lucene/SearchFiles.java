package uk.ac.glasgow.scclippy.lucene;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SearchFiles {

    /**
     * Given a query, returns result of search based on indexed documents
     *
     * @param args [-index dir] [-field f] [-query string] [-paging hitsPerPage]
     */
    public static File[] search(String[] args) throws Exception {
        if (args.length != 4)
            return null;

        String index = args[0];
        String field = args[1] == null ? "contents" : args[1];
        String queryString = args[2];
        int hitsPerPage = Integer.parseInt(args[3]);

        if (hitsPerPage < 1)
            return null;

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        QueryParser parser = new QueryParser(field, analyzer);
        String line = queryString.trim();
        Query query = parser.parse(QueryParser.escape(line));

        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        File[] files = new File[hitsPerPage];

        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            String path = doc.get("path");
            String content = new String(Files.readAllBytes(Paths.get(path)));
            files[i] = new File(path, content);
        }

        return files;
    }

}



























































