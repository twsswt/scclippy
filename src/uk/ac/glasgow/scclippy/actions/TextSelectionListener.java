package uk.ac.glasgow.scclippy.actions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import uk.ac.glasgow.scclippy.uicomponents.Search;

/**
 * Code/Text selection listener
 */
public class TextSelectionListener implements SelectionListener {

    @Override
    public void selectionChanged(SelectionEvent e) {

        Editor editor = uk.ac.glasgow.scclippy.plugin.Editor.getEditor();
        if (editor == null)
            return;
        Document doc = editor.getDocument();
        Search.inputPane.inputArea.setText(doc.getText(e.getNewRange()));
    }
}
