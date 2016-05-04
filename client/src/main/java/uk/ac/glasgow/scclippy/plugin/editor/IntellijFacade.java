package uk.ac.glasgow.scclippy.plugin.editor;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.DataManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;

/**
 * Provides simplified access to Intellij features needed by the plugin.
 */
public class IntellijFacade {

    public static Editor getEditor() {
        DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
        if (dataContext == null)
            return null;

        Project project = DataKeys.PROJECT.getData(dataContext);
        if (project == null)
            return null;

        com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null)
            return null;

        return editor;
    }
    
    private static final NotificationGroup notificationGroup =
        new NotificationGroup("My notification group", NotificationDisplayType.BALLOON, true);

    public static void createErrorNotification(@NotNull String message) {
    	createNotification(message, NotificationType.ERROR);
    }

    public static void createInfoNotification(String message) {
    	createNotification(message, NotificationType.INFORMATION);
    }

    private static void createNotification(String message, NotificationType type) {
    	
    	if (message == null)
    		throw new RuntimeException();
    	
    	ApplicationManager.getApplication().invokeLater(() -> {
    		Notification notification
                = notificationGroup.createNotification(message, type);

        Editor editor = IntellijFacade.getEditor();
        if (editor != null) {
            Notifications.Bus.notify(notification, editor.getProject());
        }
    });
}


}
