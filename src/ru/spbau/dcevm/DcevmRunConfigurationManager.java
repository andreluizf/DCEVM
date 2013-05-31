package ru.spbau.dcevm;

import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * User: user
 * Date: 4/25/13
 * Time: 3:10 PM
 */
public class DcevmRunConfigurationManager {
  
  @NotNull private final DcevmFileManager myFileManager;

  public DcevmRunConfigurationManager(@NotNull DcevmFileManager fileManager) {
    myFileManager = fileManager;
  }
  
  public void patchIfNecessary(@NotNull Project project) {
    PropertiesComponent component = PropertiesComponent.getInstance(project);
    if (component.getBoolean(DcevmConstants.PROJECT_PATCHED_KEY, false)) {
      return;
    }
    component.setValue(DcevmConstants.PROJECT_PATCHED_KEY, Boolean.TRUE.toString());
    
    // Template configuration.
    RunManagerImpl runManager = (RunManagerImpl)RunManagerImpl.getInstance(project);
    ApplicationConfigurationType instance = ApplicationConfigurationType.getInstance();
    assert instance != null;
    ConfigurationFactory factory = instance.getConfigurationFactories()[0];
    ApplicationConfiguration templateApplicationConfig =
      (ApplicationConfiguration)runManager.getConfigurationTemplate(factory).getConfiguration();
    patchConfiguration(templateApplicationConfig);
    
    // Usual configurations.
    for (RunConfiguration configuration : RunManager.getInstance(project).getAllConfigurations()) {
      if (configuration instanceof ApplicationConfiguration) {
        patchConfiguration((ApplicationConfiguration)configuration);
      }
    }
  }

  private void patchConfiguration(ApplicationConfiguration configuration) {
    configuration.ALTERNATIVE_JRE_PATH = myFileManager.getDcevmDir().getAbsolutePath();
    configuration.ALTERNATIVE_JRE_PATH_ENABLED = true;
  }

}
