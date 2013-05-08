package ru.spbau.launch;

import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
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
  private static final String ALWAYS_DOWNLOAD_PROPERTY = "always.download.dcevm";

  @NotNull
  private JreStateProvider myStateProvider;

  public DcevmStartup(@NotNull JreStateProvider stateProvider) {
    myStateProvider = stateProvider;
  }

  @Override
  public void runActivity(final Project project) {
    if (SystemProperties.getBooleanProperty(ALWAYS_DOWNLOAD_PROPERTY, false)) {
      myStateProvider.setUnready();
    }

    if (myStateProvider.isReady()) {
      System.out.println("Dcevm jre is ready");
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
              RunConfigurationManipulator service = ServiceManager.getService(RunConfigurationManipulator.class);
              service.replaceTemplateConfiguration(project);
              for (RunConfiguration rc: RunManager.getInstance(project).getConfigurations(ApplicationConfigurationType.getInstance())) {
                if (rc.getName().equals(RunConfigurationManipulator.NEW_RUN_CONFIGURATION_NAME)) {
                  System.out.println("oops found");
                  return;
                }
              }
              service.createNewDcevmConfiguration(project);
            }
          });
        }
      });
      return;
    }

    System.out.println("Dcevm not ready");

    if (!myStateProvider.isDownloadingOrDownloaded()) {
      System.out.println("Dcevm is downloading");
      ApplicationManager.getApplication().runReadAction(new Runnable() {
        @Override
        public void run() {
          ServiceManager.getService(DownloadManager.class).requestForDownload(project);
        }
      });
    }
  }

}
