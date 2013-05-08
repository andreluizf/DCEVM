package ru.spbau.install.download;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import ru.spbau.install.download.util.ConfirmationNotification;
import ru.spbau.launch.DcevmStartup;
import ru.spbau.launch.util.JreStateProvider;
import ru.spbau.launch.util.RunConfigurationManipulator;

/**
 * User: user
 * Date: 4/25/13
 * Time: 3:52 PM
 */
public class DownloadManager {
  private JreStateProvider myJreStateProvider;

  public DownloadManager(JreStateProvider jreStateProvider) {
    myJreStateProvider = jreStateProvider;
  }

  public void requestForDownload(final Project project) {
    Notifications.Bus.register(ConfirmationNotification.GROUP_DISPLAY_ID, NotificationDisplayType.STICKY_BALLOON);
    ConfirmationNotification confirmation = new ConfirmationNotification(project, new DownloadAndPatch(project), new Runnable() {
      @Override
      public void run() {
        myJreStateProvider.cancelDownload();
      }
    });
    confirmation.askForPermission();
  }


  private class DownloadAndPatch implements Runnable {
    private final Project myProject;

    public DownloadAndPatch(Project myProject) {
      this.myProject = myProject;
    }

    @Override
    public void run() {
      if (!myJreStateProvider.tryStartDownloading()) {
        return;
      }
      JreDownloader jreDownloader = ServiceManager.getService(JreDownloader.class);
      jreDownloader.setOnSuccessCallback(new Runnable() {
        @Override
        public void run() {
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
              RunConfigurationManipulator manipulator = ServiceManager.getService(RunConfigurationManipulator.class);
              manipulator.replaceTemplateConfigurationOnOpenedProjects();
              manipulator.createNewDcevmConfiguration(myProject);
              DcevmStartup.setProjectPatched(myProject);
            }
          });
        }
      });
      jreDownloader.setOnCancelCallback(new Runnable() {
        @Override
        public void run() {
          ServiceManager.getService(JreStateProvider.class).cancelDownload();
        }
      });
      jreDownloader.download(myProject);
    }
  }



}
