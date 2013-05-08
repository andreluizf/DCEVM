package ru.spbau.launch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;
import ru.spbau.install.download.DownloadManager;
import ru.spbau.launch.util.JreStateProvider;
import ru.spbau.launch.util.RunConfigurationManipulator;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class DcevmStartup implements StartupActivity {

  @NotNull
  private JreStateProvider myStateProvider;

  public DcevmStartup(@NotNull JreStateProvider stateProvider) {
    myStateProvider = stateProvider;
  }

  @Override
  public void runActivity(final Project project) {
    if (SystemProperties.getBooleanProperty("always.download.dcevm", false)) {
      myStateProvider.setUnready();
    }

    if (myStateProvider.isReady()) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
              RunConfigurationManipulator service = ServiceManager.getService(RunConfigurationManipulator.class);
              service.replaceTemplateConfiguration(project);
            }
          });
        }
      });
      return;
    }

    if (!myStateProvider.isDownloading()) {
      ApplicationManager.getApplication().runReadAction(new Runnable() {
        @Override
        public void run() {
          ServiceManager.getService(DownloadManager.class).requestForDownload(project);
        }
      });
    }
  }

}
