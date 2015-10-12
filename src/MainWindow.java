import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by User on 7.10.2015 ?..
 */
public class MainWindow implements ToolWindowFactory {

    public static JTextArea selectionOutput = new JTextArea();


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Component component = toolWindow.getComponent();

        selectionOutput.setAlignmentX(JLabel.LEFT);
        selectionOutput.setAlignmentY(JLabel.TOP);
        selectionOutput.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.green));

        component.getParent().add(selectionOutput);
    }
}
