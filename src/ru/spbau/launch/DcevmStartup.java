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
import ru.spbau.install.Downloader;
import ru.spbau.install.info.InfoProvider;

import java.io.File;
import java.io.IOException;

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

    /*
     * It's executed in AWT Event thread
     */
    @Override
    public void runActivity(final Project project) {
        JreStateProvider jreState = ApplicationManager.getApplication().getComponent(JreStateProvider.class);

        jreState.setUnready();

        if (!jreState.isReady()) {
            downloadAndPatchOpenProjects(project);
        }
    }

    private void downloadAndPatchOpenProjects(final Project project) {
        int result = Messages.showYesNoDialog(DIALOG_MESSAGE, DIALOG_TITLE, null);
        if (result == 0) {
            new Task.Backgroundable(project, DIALOG_TITLE, true) {
                final JreStateProvider jreState = ApplicationManager.getApplication().getComponent(JreStateProvider.class);

                @Override
                public void run(ProgressIndicator indicator) {
                    indicator.setText(INDICATOR_TEXT);
                    try {
                        File downloadedFile = Downloader.downloadDcevm(InfoProvider.getInstallDirectory(), indicator);
                        jreState.setReady();

                        //TODO unzip it :)

                    } catch (IOException e) {
                        //TODO: from what thread?
                        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder(ERROR_MESSAGE, MessageType.ERROR, null)
                                .setFadeoutTime(7500)
                                .createBalloon()
                                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                        Balloon.Position.atRight);
                    }
                }

                @Override
                public void onSuccess() {
                    if (jreState.isReady()) {
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            @Override
                            public void run() {
                                patchOpenProjects();
                            }
                        });
                    }
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