package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Represents a pane/TextArea containing query text
 */
public class HistoryPane extends JTextArea {

    private static Border border;

    static {
        Border matteBorder = BorderFactory.createMatteBorder(1, 5, 1, 1, JBColor.DARK_GRAY);
        Border marginBorder = BorderFactory.createEmptyBorder(0, 8, 0, 0);
        border = BorderFactory.createCompoundBorder(matteBorder, marginBorder);
    }

    public HistoryPane(String query) {
        super(query);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(border);
        setEditable(false);
    }
}
