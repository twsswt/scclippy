package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.ui.ComboBox;

import uk.ac.glasgow.scclippy.plugin.search.SearchController;

public class SearchTopPanel extends JPanel {

	private ComboBox searchMechanismComboBox;
	private ComboBox resultSortOptionsComboBox;
    private JLabel stackExchangeSearchRequestsLabel;

	
	public SearchTopPanel (SearchController searchController, QueryInputPane queryInputPane){
		
    	stackExchangeSearchRequestsLabel = new JLabel("Unknown requests left.");
        stackExchangeSearchRequestsLabel.setVisible(false);

        String[] searchOptionKeys = searchController.getSearchOptionKeys();
        searchMechanismComboBox = new ComboBox(searchOptionKeys);
        JLabel searchOptionsLabel = new JLabel("Search with");
        searchOptionsLabel.setLabelFor(searchMechanismComboBox);
        searchMechanismComboBox.setSelectedIndex(1);
        
        searchMechanismComboBox.addActionListener(e -> {
            String selectedSearchOption = (String) searchMechanismComboBox.getSelectedItem();

            if (selectedSearchOption.equals("StackExchange API")) {
                stackExchangeSearchRequestsLabel.setVisible(true);
            } else {
                stackExchangeSearchRequestsLabel.setVisible(false);
            }
        });

        String[] sortOptionKeys = searchController.getSortOptionKeys();
        resultSortOptionsComboBox = new ComboBox(sortOptionKeys);
        JLabel sortOptionsLabel = new JLabel("Sort results by");
        sortOptionsLabel.setLabelFor(resultSortOptionsComboBox);
        resultSortOptionsComboBox.setSelectedIndex(0);

        JButton searchWithGoogleButton = new GoogleSearchButton(queryInputPane);

        JButton writeQuestionButton = new AskAQuestionButton();

        // top panel
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        add(searchOptionsLabel);
        add(searchMechanismComboBox);
        add(stackExchangeSearchRequestsLabel);
        add(sortOptionsLabel);
        add(resultSortOptionsComboBox);
        add(searchWithGoogleButton);
        add(writeQuestionButton);

	}
}
