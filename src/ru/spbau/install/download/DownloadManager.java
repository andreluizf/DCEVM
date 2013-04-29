package ru.spbau.install.download;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import ru.spbau.install.download.util.ConfirmationNotification;
import ru.spbau.launch.util.JreStateProvider;
import ru.spbau.launch.util.TemplateReplacer;

/**
 * User: user
 * Date: 4/25/13
 * Time: 3:52 PM
 */
public class DownloadManager {

    public void requestForDownload(final Project project) {
        Notifications.Bus.register(ConfirmationNotification.GROUP_DISPLAY_ID, NotificationDisplayType.STICKY_BALLOON);
        ConfirmationNotification confirmation = new ConfirmationNotification(new DownloadAndPatch(project), null);
        confirmation.askForPermission();
    }


    private class DownloadAndPatch implements Runnable {
        private Project project;

        public DownloadAndPatch(Project project) {
            this.project = project;
        }

        @Override
        public void run() {
            JreDownloader jreDownloader = ServiceManager.getService(JreDownloader.class);
            jreDownloader.setOnSuccessCallback(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            ServiceManager.getService(TemplateReplacer.class).patchOpenProjects();
                            System.out.println("open projects patched");
                        }
                    });
                }
            });
            jreDownloader.setOnCancelCallback(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CANCEL!");
                    ServiceManager.getService(JreStateProvider.class).cancelDownload();
                }
            });
            System.out.println("Starting downloading...");
            jreDownloader.download(project);
        }
    }

}
