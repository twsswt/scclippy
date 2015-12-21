package uk.ac.glasgow.scclippy.uicomponents;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;


public class PostMouseListener extends MouseAdapter {

    int id;

    static String codeStartTag = "<code>";
    static String codeEndTag = "</code>";
    static int inputDialogMaxSnippetLength = 100;

    public PostMouseListener(int id) {
        this.id = id;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // double click
        if (e.getClickCount() != 2 || MainWindow.files == null || MainWindow.files[id] == null) {
            return;
        }

        String text = MainWindow.files[id].getContent();
        List<String> snippets = getSnippetsFromText(text);

        if (snippets.size() == 1) {
            // insert directly
            insertTextIntoEditor(snippets.get(0));
        }
        else if (snippets.size() > 1) {
            // ask user for input
            String chosenSnippet = getChosenSnippet(getSnippetOptions(snippets));

            if ((chosenSnippet != null) && (chosenSnippet.length() > 0)) {
                int index = indexOfChosenSnippet(chosenSnippet);
                insertTextIntoEditor(snippets.get(index));
            }
        }
    }

    private int indexOfChosenSnippet(String chosenSnippet) {
        return Integer.parseInt(chosenSnippet.substring(0, chosenSnippet.indexOf(":"))) - 1;
    }

    private List<String> getSnippetsFromText(String text) {
        List<String> snippets = new LinkedList<>();
        int start, end = 0;
        while ((start = text.indexOf(codeStartTag, end)) != -1 &&
                (end = text.indexOf(codeEndTag, start)) != -1) {
            snippets.add(text.substring(start + codeStartTag.length(), end));
        }

        return snippets;
    }

    private void insertTextIntoEditor(String text) {
        if (MainWindow.currentEditor == null)
            return;

        Document doc = MainWindow.currentEditor.getDocument();
        int offset = MainWindow.currentEditor.getCaretModel().getOffset();

        ApplicationManager.getApplication().runWriteAction(() -> {
            doc.setText(
                    doc.getText(new TextRange(0, offset)) +
                            text +
                            doc.getText(new TextRange(offset, doc.getText().length()))
            );
        });
    }

    public String getChosenSnippet(Object[] possibilities) {
        return (String) JOptionPane.showInputDialog(
                Search.searchPanel,
                "Choose which code snippet:\n",
                "Code snippet",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0]);
    }

    public Object[] getSnippetOptions(List<String> snippets) {
        Object[] snippetOptions = new Object[snippets.size()];
        for (int i = 0; i < snippets.size(); i++) {
            if (snippets.get(i).length() > 100)
                snippetOptions[i] = (i + 1) + ":" + snippets.get(i).substring(0, inputDialogMaxSnippetLength);
            else
                snippetOptions[i] = (i + 1) + ":" + snippets.get(i);
        }

        return snippetOptions;
    }
}