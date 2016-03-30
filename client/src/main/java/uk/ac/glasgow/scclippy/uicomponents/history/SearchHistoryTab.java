package uk.ac.glasgow.scclippy.uicomponents.history;

import com.intellij.ui.JBColor;

import uk.ac.glasgow.scclippy.lucene.StackoverflowEntry;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchChangeListener;

import javax.swing.*;
import javax.swing.border.Border;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a class that contains components in the query history panel/tab
 */
public class SearchHistoryTab extends JScrollPane implements SearchChangeListener {

    private final static int SCROLL_STEP_SIZE = 10;
    private final static int MAX_HISTORY_SIZE = 100;

    private JComponent contentPanel;

    Queue<JTextArea> inputHistoryQueue;

    public SearchHistoryTab() {
    	inputHistoryQueue = new LinkedList<>();
    	initialiseBorder();
        initialiseHistoryPanel();
    }

    private void initialiseHistoryPanel() {
    	contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
    	this.getViewport().setView(contentPanel);
        getVerticalScrollBar().setUnitIncrement(SCROLL_STEP_SIZE);
        
    }

    public void notifySearchChanged(String query, List<StackoverflowEntry> stackoverflowEntries) {
        JTextArea historyPane = createQueryHistoryPane(query);
        inputHistoryQueue.add(historyPane);
        
        while (inputHistoryQueue.size() > MAX_HISTORY_SIZE) {
            JTextArea area = inputHistoryQueue.poll();
            contentPanel.remove(area);
        }

        contentPanel.add(historyPane, 0);
    }
    
    private Border border;

    private void initialiseBorder () {
        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.DARK_GRAY);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    private JTextArea createQueryHistoryPane(String query) {
        JTextArea queryHistoryTextArea = new JTextArea(query);
        queryHistoryTextArea.setLineWrap(true);
        queryHistoryTextArea.setWrapStyleWord(true);
        queryHistoryTextArea.setBorder(border);
        queryHistoryTextArea.setEditable(false);
        return queryHistoryTextArea;
    }

}
