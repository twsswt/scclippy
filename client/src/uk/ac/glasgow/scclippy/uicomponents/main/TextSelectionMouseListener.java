package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseAdapter;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchTab;

/**
 * Code/Text mouse selection listener
 */
public class TextSelectionMouseListener extends EditorMouseAdapter {

    @Override
    public void mouseReleased(EditorMouseEvent e) {
        super.mouseReleased(e);

        Editor editor = uk.ac.glasgow.scclippy.plugin.editor.Editor.getEditor();
        if (editor == null)
            return;

        // update query input with the selection
        SearchTab.inputPane.inputArea.setText(editor.getSelectionModel().getSelectedText());
    }

}
