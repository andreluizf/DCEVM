package ru.spbau.launch.util;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import ru.spbau.install.info.InfoProvider;

/**
 * User: user
 * Date: 4/25/13
 * Time: 3:10 PM
 */
public class RunConfigurationManipulator {
  public static final String NEW_RUN_CONFIGURATION_NAME = "DCEVM";
  private InfoProvider myInfoProvider;

  public RunConfigurationManipulator(InfoProvider infoProvider) {
    this.myInfoProvider = infoProvider;
  }

  public void replaceTemplateConfigurationOnOpenedProjects() {
    String alterJrePath = myInfoProvider.getInstallDirectory();
    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
    for (Project project : openProjects) {
      replaceTemplateWith(project, alterJrePath, true);
    }
  }

  public void replaceTemplateConfiguration(Project project) {
    replaceTemplateWith(project, myInfoProvider.getInstallDirectory(), true);
  }

  public void createNewDcevmConfiguration(Project project) {
    RunnerAndConfigurationSettings newRunConfiguration;
    RunnerAndConfigurationSettings selected = RunManager.getInstance(project).getSelectedConfiguration();
    if (selected != null && getApplicationConfigurationFactory().equals(selected.getFactory())) {
      newRunConfiguration = RunManager.getInstance(project).createConfiguration(selected.getConfiguration().clone(), getApplicationConfigurationFactory());
      newRunConfiguration.setName(NEW_RUN_CONFIGURATION_NAME);
    } else {
      newRunConfiguration = RunManager.getInstance(project).createRunConfiguration(NEW_RUN_CONFIGURATION_NAME, getApplicationConfigurationFactory());
    }
    patchConfiguration((ApplicationConfiguration)newRunConfiguration.getConfiguration(), myInfoProvider.getInstallDirectory(), true);
    ((RunManagerImpl)RunManager.getInstance(project)).addConfiguration(newRunConfiguration, false);
  }

  public static ConfigurationFactory getApplicationConfigurationFactory() {
    return ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
  }

  private static ApplicationConfiguration getTemplateApplicationConfiguration(Project project) {
    RunManagerImpl runManager = (RunManagerImpl)RunManagerImpl.getInstance(project);
    ConfigurationFactory factory = getApplicationConfigurationFactory();
    return (ApplicationConfiguration)runManager.getConfigurationTemplate(factory).getConfiguration();
  }

  public static void replaceTemplateWith(Project project, String alterJrePath, boolean enabled) {
    ApplicationConfiguration templateApplicationConfig = getTemplateApplicationConfiguration(project);
    patchConfiguration(templateApplicationConfig, alterJrePath, enabled);
  }

  private static void patchConfiguration(ApplicationConfiguration configuration, String alterJrePath, boolean enabled) {
    configuration.ALTERNATIVE_JRE_PATH = alterJrePath;
    configuration.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
  }

}
