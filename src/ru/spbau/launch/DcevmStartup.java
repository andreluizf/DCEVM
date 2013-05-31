package ru.spbau.launch;

import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.SystemProperties;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import ru.spbau.install.download.DownloadManager;
import ru.spbau.install.info.InfoProvider;
import ru.spbau.launch.util.JreStateProvider;
import ru.spbau.launch.util.RunConfigurationManipulator;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class DcevmStartup implements StartupActivity {
  private static final String ALWAYS_DOWNLOAD_PROPERTY = "always.download.dcevm";
  public static final String IS_PROJECT_PATCHED = "DCEVM_IS_PROJECT_PATCHED";
  private final InfoProvider myInfoProvider;

  @NotNull
  private JreStateProvider myStateProvider;

  public DcevmStartup(@NotNull JreStateProvider stateProvider, @NotNull InfoProvider provider) {
    myStateProvider = stateProvider;
    myInfoProvider = provider;
  }

  public static boolean isProjectPatched(Project project) {
    return PropertiesComponent.getInstance(project).getBoolean(IS_PROJECT_PATCHED, false);
  }
  public static void setProjectPatched(Project project) {
    PropertiesComponent.getInstance(project).setValue(IS_PROJECT_PATCHED, Boolean.TRUE.toString());
  }

  private static void setProjectUnpatched(Project project) {
    PropertiesComponent.getInstance(project).setValue(IS_PROJECT_PATCHED, Boolean.FALSE.toString());
  }

  @Override
  public void runActivity(final Project project) {

    if (myStateProvider.isReady()) {
      new Thread(new Runnable() {
          @Override
          public void run() {
            Float version = myInfoProvider.getCurrentServerJreVersion();
            if (version != null && version > myStateProvider.getVersion()) {
              myStateProvider.setUnready();
            }
          }
      }).start();
    }

    if (SystemProperties.getBooleanProperty(ALWAYS_DOWNLOAD_PROPERTY, false)) {
      myStateProvider.setDeleted();
      setProjectUnpatched(project);
    }

    if (myStateProvider.isReady()) {
      if (isProjectPatched(project)) {
        return;
      }
      
      UIUtil.invokeLaterIfNeeded(new Runnable() {
        @Override
        public void run() {
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
              RunConfigurationManipulator service = ServiceManager.getService(RunConfigurationManipulator.class);
              //service.replaceTemplateConfiguration(project);
              setProjectPatched(project);
              ApplicationConfigurationType configurationType = ApplicationConfigurationType.getInstance();
              if (configurationType == null) {
                return;
              }
              for (RunConfiguration rc : RunManager.getInstance(project).getConfigurations(configurationType)) {
                if (rc.getName().equals(RunConfigurationManipulator.NEW_RUN_CONFIGURATION_NAME)) {
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

    if (!myStateProvider.isDownloadingOrDownloaded()) {
      ApplicationManager.getApplication().runReadAction(new Runnable() {
        @Override
        public void run() {
          ServiceManager.getService(DownloadManager.class).requestForDownload(project);
        }
      });
    }
  }


}
