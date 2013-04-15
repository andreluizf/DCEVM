package ru.spbau.intellij;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import ru.spbau.dcevm.Downloader;
import ru.spbau.host.LocalUserAddressProvider;

import java.io.IOException;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class Starter implements StartupActivity {
    @Override
    public void runActivity(Project project) {
        int result = Messages.showYesNoDialog("Download dcevm?", "DCEVM plugin", null);
        if (result == 0) {
            new Task.Backgroundable(project, "DCEVM plugin", true) {
                @Override
                public void run(ProgressIndicator indicator) {
                    indicator.setText("Downloading DCEVM jre");
                    indicator.setFraction(0.0);
                    indicator.setFraction(1.0);
                    Downloader dcevmLoader = new Downloader(LocalUserAddressProvider.getDcevmHomeAddress());
                    try {
                        dcevmLoader.downloadDcevm(indicator);
                    } catch (IOException e) {
                        Messages.showErrorDialog(e.getMessage(), "IO Error during downloading");
                    }
                }

                @Override
                public void onSuccess() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                            for (Project project: openProjects) {
                                changeTemplateAlterJre(project, LocalUserAddressProvider.getDcevmHomeAddress(), true);
                            }
                        }
                    });
                }

                @Override
                public void onCancel() {
                    // delete DCEVM home directory hoya!
                }

            }.setCancelText("Stop downloading").queue();
        }
    }

    public void changeTemplateAlterJre(Project project, String alterJrePath, boolean enabled) {
        RunManagerImpl runManager = (RunManagerImpl)RunManagerImpl.getInstance(project);
        ConfigurationFactory factory = ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
        ApplicationConfiguration templateApplicationConfig = (ApplicationConfiguration)
                runManager.getConfigurationTemplate(factory).getConfiguration();
        templateApplicationConfig.ALTERNATIVE_JRE_PATH = alterJrePath;
        templateApplicationConfig.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
    }
}
