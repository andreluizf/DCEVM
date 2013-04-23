package ru.spbau.launch;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.event.HyperlinkEvent;

/**
 * User: user
 * Date: 4/23/13
 * Time: 5:33 PM
 */
public class DownloadConfirmation {

    public static final String AGREE = "allow";
    public static final String DECLINE = "decline";

    @Nullable
    private Runnable onAllowDownload;
    @Nullable
    private Runnable onDeclineDownload;


    public DownloadConfirmation(@Nullable Runnable onAllowDownload, @Nullable Runnable onDeclineDownload) {
        this.onAllowDownload = onAllowDownload;
        this.onDeclineDownload = onDeclineDownload;
    }

    public void askForPermission() {
        Notifications.Bus.notify(new Notification("Dcevm Plugin",
                "Dcevm jre download confirmation",
                getText(), NotificationType.INFORMATION,
                new DownloadConfirmationListener()));
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
                } else if (DECLINE.equals(description)) {
                    if (onDeclineDownload != null) {
                        onDeclineDownload.run();
                    }
                    notification.expire();
                }
            }
        }
    }

    private static String getText() {
        return
                "<html>Please click <a href='" + AGREE + "'>download</a> to download DCEVM jre " +
                        "or <a href='" + DECLINE + "'>decline</a> otherwise.";
    }

}
