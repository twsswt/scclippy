package uk.ac.glasgow.scclippy.plugin.search;

import java.util.Arrays;

/**
 * Class for sorting results/files from search
 */
public class ResultsSorter {

    public enum SortType { RELEVANCE, BY_SCORE }

    public static SortType currentSortOption = SortType.RELEVANCE;

    public static int[] minimumScore = new int[]{0};

    /**
     * Sorts results/files by score
     */
    public static void sortFilesByScore() {
        Arrays.sort(Search.files);
    }
}
