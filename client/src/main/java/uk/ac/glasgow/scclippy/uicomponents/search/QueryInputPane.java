package uk.ac.glasgow.scclippy.uicomponents.search;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intellij.ui.JBColor;

import uk.ac.glasgow.scclippy.plugin.search.SearchException;

public class QueryInputPane extends JScrollPane {

    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";
    public static final int INPUT_TEXT_AREA_ROWS = 5;

    private final JTextArea queryTextArea;

    private Border border;
	private boolean resizableInputArea;

    public QueryInputPane(SearchController searchController) {

        initialiseBorder ();
        queryTextArea = new JTextArea();
        getViewport().setView(queryTextArea);

        queryTextArea.setLineWrap(true);
        queryTextArea.setWrapStyleWord(true);
        queryTextArea.setBorder(border);
        queryTextArea.setRows(INPUT_TEXT_AREA_ROWS);
        queryTextArea.getDocument().addDocumentListener(new InputPaneListener());

        InputMap input = queryTextArea.getInputMap();

        // 'shift + enter' for typing a new line
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);

        // 'enter' to perform search
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        input.put(enter, TEXT_SUBMIT);

        ActionMap actions = queryTextArea.getActionMap();
        
        AbstractAction executeSearchAction = 
        	new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = queryTextArea.getText();
            	searchController.setQuery(query);
                try {
					searchController.updateSearchAndSort();
				} catch (SearchException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        };
        
        actions.put(TEXT_SUBMIT, executeSearchAction);
        
    }

    private void initialiseBorder() {
    	Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.CYAN);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    private class InputPaneListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkRows();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkRows();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // no effect
        }

        private void checkRows() {
            if (resizableInputArea)
                queryTextArea.setRows(queryTextArea.getLineCount());
        }
    }    


	public void setInputAreaIsResizable(boolean b) {
		this.resizableInputArea = b;
		if (!resizableInputArea)
            queryTextArea.setRows(QueryInputPane.INPUT_TEXT_AREA_ROWS);

		
	}

	
	public void setQueryText(String codeFragment) {
		queryTextArea.setText(codeFragment);
	}

	public String getQueryText() {
		return queryTextArea.getText();
	}

}
