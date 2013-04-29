package ru.spbau.launch.util;

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
public class TemplateReplacer {
    private InfoProvider infoProvider;

    public TemplateReplacer(InfoProvider infoProvider) {
        this.infoProvider = infoProvider;
    }

    public void patchOpenProjects() {
        String alterJrePath = infoProvider.getInstallDirectory();
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project: openProjects) {
            replaceWith(project, alterJrePath, true);
        }
    }

    public void patch(Project project) {
        replaceWith(project, infoProvider.getInstallDirectory(), true);
    }


    public static void replaceWith(Project project, String alterJrePath, boolean enabled) {
        RunManagerImpl runManager = (RunManagerImpl)RunManagerImpl.getInstance(project);
        ConfigurationFactory factory = ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
        ApplicationConfiguration templateApplicationConfig = (ApplicationConfiguration)
                runManager.getConfigurationTemplate(factory).getConfiguration();
        templateApplicationConfig.ALTERNATIVE_JRE_PATH = alterJrePath;
        templateApplicationConfig.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
    }
}
