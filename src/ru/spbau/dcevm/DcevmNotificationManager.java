package ru.spbau.dcevm;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * @author Denis Zhdanov
 * @since 5/31/13 5:21 PM
 */
public class DcevmNotificationManager {

  public void notifyNoDcevmAvailableForLocalEnvironment(@NotNull Project project) {
    new Notification(DcevmConstants.DCEVM_NAME, DcevmConstants.DCEVM_NAME, "No DCEVM is assembled for the current environment",
                     NotificationType.INFORMATION).notify(project);
  }

  public void notifyCannotSetJavaExecutable(@NotNull Project project) {
    new Notification(DcevmConstants.DCEVM_NAME, DcevmConstants.DCEVM_NAME, "Can't set execution permission for java runtime",
                     NotificationType.INFORMATION).notify(project);
  }

  public void askPermissionToDownloadDcevm(@NotNull Project project, @NotNull final Runnable callback) {
    Notification notification = new Notification(
      DcevmConstants.DCEVM_NAME,
      "DCEVM is available for your environment",
      "<html>Would you like to <a href=''>download</a> it?",
      NotificationType.INFORMATION,
      new NotificationListener() {
        @Override
        public void hyperlinkUpdate(@NotNull Notification notification,
                                    @NotNull HyperlinkEvent event)
        {
          notification.expire();
          callback.run();
        }
      }
    );
    notification.notify(project);
  }

  public void askPermissionToUpdateDcevm(@NotNull Project project, @NotNull final Runnable callback) {
    Notification notification = new Notification(
      DcevmConstants.DCEVM_NAME,
      "New DCEVM version is available for your environment",
      "<html>Would you like to <a href=''>download</a> it?",
      NotificationType.INFORMATION,
      new NotificationListener() {
        @Override
        public void hyperlinkUpdate(@NotNull Notification notification,
                                    @NotNull HyperlinkEvent event)
        {
          notification.expire();
          callback.run();
        }
      }
    );
    notification.notify(project);
  }
}
