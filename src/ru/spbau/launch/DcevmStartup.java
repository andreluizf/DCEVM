package ru.spbau.launch;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import ru.spbau.install.info.InfoProvider;

import java.io.File;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class DcevmStartup implements StartupActivity {

    public static final String DIALOG_MESSAGE = "Download dcevm?";
    public static final String DIALOG_TITLE = "DCEVM plugin";
    public static final String INDICATOR_TEXT = "Downloading DCEVM jre";
    public static final String ERROR_MESSAGE = "<html>DCEVM download:<br>IO Error during downloading</html>";
    public static final String CANCEL_TEXT = "Stop downloading";

    @NotNull private JreStateProvider jreState;


    @Override
    public void runActivity(final Project project) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        Notifications.Bus.register(DownloadConfirmation.GROUP_DISPLAY_ID, NotificationDisplayType.STICKY_BALLOON);
                    }
                });
            }
        });

        DownloadConfirmation confirmation = new DownloadConfirmation(null, null);
        confirmation.askForPermission();


//        ApplicationManager.getApplication().runReadAction(new Runnable() {
//            @Override
//            public void run() {
//                jreState = ServiceManager.getService(JreStateProvider.class);
//                System.out.println("jre = " + jreState.isReady());
//            }
//        });
//
////        for testing purposes only
//        jreState.setUnready();
//
//        if (!jreState.isReady()) {
//
//            Notifications.Bus.register(DownloadConfirmation.GROUP_DISPLAY_ID, NotificationDisplayType.STICKY_BALLOON);
//
////            Runnable onAcceptDownloading = new Runnable() {
////                @Override
////                public void run() {
////                    JreDownloader downloader = ServiceManager.getService(JreDownloader.class);
////                    downloader.setOnSuccessCallback(new Runnable() {
////                        @Override
////                        public void run() {
////                            String homeDir = ServiceManager.getService(InfoProvider.class).getInstallDirectory();
////                            patchOpenProjects(homeDir);
////                        }
////                    });
////                }
////            };
////            DownloadConfirmation confirmation = new DownloadConfirmation(onAcceptDownloading, null);
//
////            DownloadConfirmation confirmation = new DownloadConfirmation(null, null);
////            confirmation.askForPermission();
//
//        } else {
//
//            final String homeDir = ServiceManager.getService(InfoProvider.class).getInstallDirectory();
//            ApplicationManager.getApplication().invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                replaceWith(project, homeDir, true);
//                            }
//                        });
//                    }
//            });
//
//        }
    }


    private void patchOpenProjects(String homeDir) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project: openProjects) {
            replaceWith(project, homeDir, true);
        }
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
