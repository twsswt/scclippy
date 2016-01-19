package uk.ac.glasgow.scclippy.uicomponents;

import uk.ac.glasgow.scclippy.plugin.Search;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button for searching with Stack Exchange API
 */
public class StackOverflowSearchButton extends JButton {
    public StackOverflowSearchButton(String s) {
        super(s);
        addActionListener(new StackoverflowSearchActionListener());
    }

    private class StackoverflowSearchActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Search.stackexchangeSearch(SearchTab.inputPane.inputArea.getText());
            SearchTab.posts.update(Search.files);
        }

    }
}
