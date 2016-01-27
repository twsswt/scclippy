package uk.ac.glasgow.scclippy.uicomponents.history;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a class that contains components in the query history panel/tab
 */
public class SearchHistoryTab {

    private final static int SCROLL_STEP = 10;

    private JComponent historyPanel = new JPanel();
    private JScrollPane scroll = new JBScrollPane(historyPanel);

    Queue<JTextArea> inputHistoryQueue = new LinkedList<>();

    public SearchHistoryTab() {
        initHistoryPanel();
    }

    public void initHistoryPanel() {
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.PAGE_AXIS));
        scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_STEP);
    }

    public void update(String query) {
        JTextArea historyPane = new HistoryPane(query);

        inputHistoryQueue.add(historyPane);
        if (inputHistoryQueue.size() > 100) {
            JTextArea area = inputHistoryQueue.poll();
            historyPanel.remove(area);
        }

        historyPanel.add(historyPane, 0);
    }

    public JScrollPane getScroll() {
        return scroll;
    }
}
