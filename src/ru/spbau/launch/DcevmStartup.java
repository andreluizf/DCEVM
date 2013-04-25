package ru.spbau.launch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ru.spbau.install.download.DownloadManager;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class DcevmStartup implements StartupActivity {

    private static JreStateProvider jreState;

    static {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                jreState = ServiceManager.getService(JreStateProvider.class);
            }
        });
    }


    @Override
    public void runActivity(final Project project) {
        System.out.println("JRE state: " + jreState.isReady());

        if (jreState.isReady()) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            ServiceManager.getService(TemplateReplacer.class).patch(project);
                        }
                    });
                }
            });
            return;
        }

        if (jreState.tryStartDownloading()) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    ServiceManager.getService(DownloadManager.class).requestForDownload(project);
                }
            });
        }
    }

}
