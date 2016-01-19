package uk.ac.glasgow.scclippy.uicomponents;

import uk.ac.glasgow.scclippy.plugin.Search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StackoverflowSearchActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Search.stackexchangeSearch(SearchTab.inputPane.inputArea.getText());
    }

}
