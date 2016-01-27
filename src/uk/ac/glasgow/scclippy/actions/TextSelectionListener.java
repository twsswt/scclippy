package uk.ac.glasgow.scclippy.actions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseAdapter;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import uk.ac.glasgow.scclippy.uicomponents.SearchTab;

/**
 * Code/Text mouse selection listener
 */
public class TextSelectionListener extends EditorMouseAdapter {

    @Override
    public void mouseReleased(EditorMouseEvent e) {
        super.mouseReleased(e);

        Editor editor = uk.ac.glasgow.scclippy.plugin.Editor.getEditor();
        if (editor == null)
            return;

        // update query input with the selection
        SearchTab.inputPane.inputArea.setText(editor.getSelectionModel().getSelectedText());
    }

}
