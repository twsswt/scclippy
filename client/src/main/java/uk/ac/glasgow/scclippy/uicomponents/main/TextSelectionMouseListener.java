package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseAdapter;
import com.intellij.openapi.editor.event.EditorMouseEvent;

import uk.ac.glasgow.scclippy.plugin.editor.IntelijFacade;
import uk.ac.glasgow.scclippy.uicomponents.search.QueryInputPane;

/**
 * Code/Text mouse selection listener
 */
public class TextSelectionMouseListener extends EditorMouseAdapter {

	private final QueryInputPane queryInputPane;
	
	public TextSelectionMouseListener(QueryInputPane queryInputPane){
		this.queryInputPane = queryInputPane;
	}
	
    @Override
    public void mouseReleased(EditorMouseEvent e) {
        super.mouseReleased(e);

        Editor editor = IntelijFacade.getEditor();
        if (editor == null) return;

        // update query input with the selection
        String codeFragment = 
        	editor.getSelectionModel().getSelectedText();
        
        queryInputPane.setQueryText(codeFragment);
    }

}
