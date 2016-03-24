package uk.ac.glasgow.scclippy.uicomponents.search;

import com.intellij.openapi.ui.ComboBox;
import uk.ac.glasgow.scclippy.plugin.search.*;
import uk.ac.glasgow.scclippy.uicomponents.history.SearchHistoryTab;
import uk.ac.glasgow.scclippy.uicomponents.settings.IntegerSavingJTextField;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a class that contains components in the search panel/tab
 */
public class SearchTab {

    Search localSearch = new LocalIndexedSearch();
    Search webServiceSearch = new WebServiceSearch();
    Search stackExchangeSearch = new StackExchangeSearch();

    private JComponent searchPanel;
    private JScrollPane scroll;

    public static InputPane inputPane;
    public Posts posts = new Posts();

    JLabel stackExchangeSearchRequestsLabel = new JLabel("Unknown requests left.");

    public SearchTab(SearchHistoryTab searchHistoryTab) {
        initSearchPanel(searchHistoryTab);
    }

    void initSearchPanel(SearchHistoryTab searchHistoryTab) {
        searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.PAGE_AXIS));

        scroll = new SearchPanelScroll(searchPanel, posts, localSearch, webServiceSearch, stackExchangeSearch);
        inputPane = new InputPane(posts, searchHistoryTab, this);

        String[] searchOption = {"Local Index", "Web Service", "StackExchange API"};
        JComboBox searchOptions = new ComboBox(searchOption);
        JLabel searchOptionsLabel = new JLabel("Search with");
        searchOptionsLabel.setLabelFor(searchOptions);
        searchOptions.setSelectedIndex(1);
        searchOptions.addActionListener(e -> {
            JComboBox cb = (JComboBox) e.getSource();
            String selectedSearchOption = (String) cb.getSelectedItem();

            for (int i = 0; i < searchOption.length; i++) {
                if (searchOption[i].equals(selectedSearchOption)) {
                    Search.currentSearchType = Search.SearchType.values()[i];
                    break;
                }
            }
            if (searchOption[2].equals(selectedSearchOption)) {
                stackExchangeSearchRequestsLabel.setVisible(true);
            } else {
                stackExchangeSearchRequestsLabel.setVisible(false);
            }
        });

        stackExchangeSearchRequestsLabel.setVisible(false);

        String[] sortOption = {"Relevance", "Score"};
        JComboBox sortOptions = new ComboBox(sortOption);
        JLabel sortOptionsLabel = new JLabel("Sort results by");
        sortOptionsLabel.setLabelFor(sortOptions);
        sortOptions.setSelectedIndex(0);
        sortOptions.addActionListener(e -> {
            JComboBox cb = (JComboBox) e.getSource();
            String selectedSortOption = (String) cb.getSelectedItem();

            for (int i = 0; i < sortOption.length; i++) {
                if (sortOption[i].equals(selectedSortOption)) {
                    ResultsSorter.currentSortOption = ResultsSorter.SortType.values()[i];
                    break;
                }
            }
        });


        int minimumUpvotesDefaultValue = 0;
        JTextField minimumUpvotes = new IntegerSavingJTextField(ResultsSorter.minimumScore, minimumUpvotesDefaultValue);
        int minimumUpvotesFieldSize = 3;
        minimumUpvotes.setColumns(minimumUpvotesFieldSize);
        minimumUpvotes.setToolTipText("Minimum upvotes filter of results");

        // google button
        JButton searchWithGoogleButton = new GoogleSearchButton("Google Search");
        searchWithGoogleButton.setToolTipText("Open browser to search for Stackoverflow posts");

        // write a question button
        JButton writeQuestionButton = new AskAQuestionButton("Ask a question");

        // top panel
        JComponent topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.add(searchOptionsLabel);
        topPanel.add(searchOptions);
        topPanel.add(stackExchangeSearchRequestsLabel);
        topPanel.add(sortOptionsLabel);
        topPanel.add(sortOptions);
        topPanel.add(minimumUpvotes);
        topPanel.add(searchWithGoogleButton);
        topPanel.add(writeQuestionButton);

        // add components to search panel
        searchPanel.add(topPanel);
        searchPanel.add(inputPane.getComponent());
        posts.addTo(searchPanel);
    }

    public Posts getPosts() {
        return posts;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public Search getLocalSearch() {
        return localSearch;
    }

    public Search getWebServiceSearch() {
        return webServiceSearch;
    }

    public Search getStackExchangeSearch() {
        return stackExchangeSearch;
    }
}
