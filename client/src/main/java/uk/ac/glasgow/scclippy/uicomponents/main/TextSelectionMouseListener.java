package uk.ac.glasgow.scclippy.uicomponents.main;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseAdapter;
import com.intellij.openapi.editor.event.EditorMouseEvent;

import uk.ac.glasgow.scclippy.plugin.editor.IntellijFacade;
import uk.ac.glasgow.scclippy.uicomponents.search.QueryInputPane;
import uk.ac.glasgow.scclippy.uicomponents.search.SearchController;

/**
 * Code/Text mouse selection listener
 */
public class TextSelectionMouseListener extends EditorMouseAdapter {

	private final QueryInputPane queryInputPane;
	private SearchController searchController;
	
	public TextSelectionMouseListener(QueryInputPane queryInputPane, SearchController searchController){
		this.queryInputPane = queryInputPane;
		this.searchController = searchController;
	}
	
    @Override
    public void mouseReleased(EditorMouseEvent e) {
        super.mouseReleased(e);

        Editor editor = IntellijFacade.getEditor();
        if (editor == null) return;

        // update query input with the selection
        String codeFragment = 
        	editor.getSelectionModel().getSelectedText();
        
        queryInputPane.setQueryText(codeFragment);
        searchController.setQuery(codeFragment);
    }

}
