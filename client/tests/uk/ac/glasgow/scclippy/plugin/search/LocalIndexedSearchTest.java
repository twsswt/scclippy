package uk.ac.glasgow.scclippy.plugin.search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.glasgow.scclippy.plugin.settings.Settings;

import java.io.FileNotFoundException;

import static junit.framework.Assert.assertTrue;


public class LocalIndexedSearchTest {

    Search searcher;

    @Before
    public void setUp() throws FileNotFoundException {
        searcher = new LocalIndexedSearch();
        Settings.indexPath = "D:/finalIndex";
    }

    @Test
    public void testBasicSearch() throws Exception {
        searcher.search("String", 10);
        assertTrue(Search.getFiles() != null);
    }

    @Test
    public void testNullSearch() throws Exception {
        searcher.search(null, 10);
        assertTrue(Search.getFiles() == null);
    }

    @Test
    public void testEmptySearch() throws Exception {
        searcher.search("", 10);
        assertTrue(Search.getFiles() == null);
    }

    @Test
    public void testZeroResultsSearch() throws Exception {
        searcher.search("String", 0);
        assertTrue(Search.getFiles() == null);
    }

    @Test
    public void testMaxResultsSearch() throws Exception {
        searcher.search("String", Integer.MAX_VALUE);
        assertTrue(Search.getFiles() != null);
    }

    @Test
    public void testMinResultsSearch() throws Exception {
        searcher.search("String", Integer.MIN_VALUE);
        assertTrue(Search.getFiles() == null);
    }

    @After
    public void tearDown() throws FileNotFoundException {
        Search.files = null;
    }

}