package ru.spbau.launch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import ru.spbau.install.download.Downloader;
import ru.spbau.install.info.InfoProvider;

import java.io.IOException;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class DcevmStartup implements StartupActivity {

    @Override
    public void runActivity(final Project project) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JreStateProvider jreState = ApplicationManager.getApplication().getComponent(JreStateProvider.class);

                if (!jreState.isReady()) {
                    downloadAndPatchOpenProjects(project);
                }
            }
        });
    }

    private void downloadAndPatchOpenProjects(final Project project) {
        int result = Messages.showYesNoDialog("Download dcevm?", "DCEVM plugin", null);
        if (result == 0) {
            new Task.Backgroundable(project, "DCEVM plugin", true) {
                @Override
                public void run(ProgressIndicator indicator) {
                    indicator.setText("Downloading DCEVM jre");
                    Downloader dcevmLoader = new Downloader(InfoProvider.getInstallDirectory());
                    try {
                        dcevmLoader.downloadDcevm(indicator);
                    } catch (IOException e) {

                        //TODO: from what thread?
                        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder("<html>DCEVM download:<br>IO Error during downloading</html>", MessageType.ERROR, null)
                                .setFadeoutTime(7500)
                                .createBalloon()
                                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                        Balloon.Position.atRight);

                    }
                }

                @Override
                public void onSuccess() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            patchOpenProjects();
                        }
                    });
                    patchOpenProjects();
                }

                @Override
                public void onCancel() {
                    //TODO: delete DCEVM home directory hoya!
                }

            }.setCancelText("Stop downloading").queue();
        }
    }

    private void patchOpenProjects() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project: openProjects) {
            TemplateAlterJreSettingsReplacer.replaceWith(project, InfoProvider.getInstallDirectory(), true);
        }
    }
}
