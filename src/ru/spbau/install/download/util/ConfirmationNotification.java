package ru.spbau.install.download.util;

import com.intellij.notification.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.launch.util.JreStateProvider;

import javax.swing.event.HyperlinkEvent;

/**
 * User: user
 * Date: 4/23/13
 * Time: 5:33 PM
 */
public class ConfirmationNotification {

  public static final String AGREE = "allow";
  public static final String DECLINE = "decline";
  public static final String GROUP_DISPLAY_ID = "Dcevm plugin";

  @Nullable
  private final Runnable onAllowDownload;
  @Nullable
  private final Runnable onDeclineDownload;

  private final Project myProject;

  public ConfirmationNotification(final Project myProject, @Nullable Runnable onAllowDownload, @Nullable Runnable onDeclineDownload) {
    this.onAllowDownload = onAllowDownload;
    this.onDeclineDownload = onDeclineDownload;
    this.myProject = myProject;
  }

  public void askForPermission() {
    final String question;
    if (ServiceManager.getService(JreStateProvider.class).getVersion() > 0) {
      question = "A new version of DCEVM is available";
    } else {
      question = "No DCEVM is found at local environment";
    }

    UIUtil.invokeLaterIfNeeded(new Runnable() {
      @Override
      public void run() {
        new Notification(GROUP_DISPLAY_ID, question, getText(), NotificationType.INFORMATION,
                         new DownloadConfirmationListener()).notify(myProject);
      }
    });
  }


  private class DownloadConfirmationListener implements NotificationListener {
    @Override
    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
      if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        final String description = event.getDescription();
        if (AGREE.equals(description)) {
          if (onAllowDownload != null) {
            onAllowDownload.run();
          }
          notification.expire();
        }
        else if (DECLINE.equals(description)) {
          if (onDeclineDownload != null) {
            onDeclineDownload.run();
          }
          notification.expire();
        }
      }
    }
  }

  private static String getText() {
    return "<html>Would you like to <a href='" + AGREE + "'>download</a> it?";
  }

}
