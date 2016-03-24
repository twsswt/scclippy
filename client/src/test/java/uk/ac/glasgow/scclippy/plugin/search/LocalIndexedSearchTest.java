package uk.ac.glasgow.scclippy.plugin.search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.intellij.openapi.ui.InputException;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static org.junit.Assert.*;


public class LocalIndexedSearchTest {

    StackoverflowSearch searcher;

    @Before
    public void setUp() throws FileNotFoundException {
        searcher =
        new LocalIndexedSearch(Paths.get("target/lucene-index").toAbsolutePath());
    }

    @Test
    public void testBasicSearch() throws Exception {
        searcher.updateSearch("String", 10, 0);
        assertNotNull(searcher.getLastSearchResult());
    }

    @Test(expected=InputException.class)
    public void testNullSearch() throws Exception {
        searcher.updateSearch(null, 10, 0);
    }

    @Test(expected=InputException.class)
    public void testEmptySearch() throws Exception {
        searcher.updateSearch("", 10, 0);
    }

    @Test(expected=InputException.class)
    public void testZeroResultsSearch() throws Exception {
        searcher.updateSearch("String", 0, 0);
    }

    @Test(expected=InputException.class)
    public void testMaxResultsSearch() throws Exception {
        searcher.updateSearch("String", Integer.MAX_VALUE, 0);
    }

    @Test(expected=InputException.class)
    public void testMinResultsSearch() throws Exception {
        searcher.updateSearch("String", Integer.MIN_VALUE, 0);
    }

    @After
    public void tearDown() throws FileNotFoundException {
       searcher = null;
    }

}