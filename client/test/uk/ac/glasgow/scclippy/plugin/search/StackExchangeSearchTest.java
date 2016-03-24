package uk.ac.glasgow.scclippy.plugin.search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;


public class StackExchangeSearchTest {

    StackoverflowSearch searcher;

    @Before
    public void setUp() {
        searcher = new StackoverflowJSONAPISearch();
    }

    @Test
    public void testBasicSearch() throws Exception {
        searcher.updateSearch("String", 10, 0);
        assertNotNull(searcher.getLastSearchResult());
    }

    @Test(expected=NullPointerException.class)
    public void testNullSearch() throws Exception {
        searcher.updateSearch(null, 10, 0);
    }

    @Test
    public void testEmptySearch() throws Exception {
        searcher.updateSearch("", 10, 0);
        assertNull(searcher.getLastSearchResult());
    }

    @Test
    public void testZeroResultsSearch() throws Exception {
        searcher.updateSearch("String", 0, 0);
        assertNull(searcher.getLastSearchResult());
    }

    @Test
    public void testMaxResultsSearch() throws Exception {
        searcher.updateSearch("String", Integer.MAX_VALUE, 0);
        assertNotNull(searcher.getLastSearchResult());
    }

    @Test
    public void testMinResultsSearch() throws Exception {
        searcher.updateSearch("String", Integer.MIN_VALUE, 0);
        assertNull(searcher.getLastSearchResult());
    }

    @After
    public void tearDown() throws FileNotFoundException {
        searcher = null;
    }

}