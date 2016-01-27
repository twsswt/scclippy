package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a class that contains components in the query history panel/tab
 */
public class SearchHistoryTab {

    static Queue<JTextArea> inputHistoryQueue = new LinkedList<>();
    static JComponent historyPanel = new JPanel();
    static JBScrollPane historyPanelScroll = new JBScrollPane(historyPanel);

    private static int MAIN_SCROLL_STEP = 10;

    static void update(String query) {
        JTextArea historyPane = new HistoryPane(query);

        inputHistoryQueue.add(historyPane);
        if (inputHistoryQueue.size() > 100) {
            JTextArea area = inputHistoryQueue.poll();
            historyPanel.remove(area);
        }

        historyPanel.add(historyPane, 0);
    }

    public static void initHistoryPanel() {
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.PAGE_AXIS));
        historyPanelScroll.getVerticalScrollBar().setUnitIncrement(MAIN_SCROLL_STEP);
    }
}
