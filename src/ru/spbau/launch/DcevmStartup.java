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
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.io.ZipUtil;
import org.jetbrains.annotations.NotNull;
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
    public static final String CANCEL_TEXT = "Stop downloading";

    private JreStateProvider jreState;

    /*
     * It's executed in AWT Event thread
     */
    @Override
    public void runActivity(final Project project) {
        jreState = ApplicationManager.getApplication().getComponent(JreStateProvider.class);

        //for testing purposes only
        jreState.setUnready();

        if (!jreState.isReady()) {
            downloadAndPatchOpenProjects(project);
        }
    }

    private void downloadAndPatchOpenProjects(final Project project) {
        int result = Messages.showYesNoDialog(DIALOG_MESSAGE, DIALOG_TITLE, null);

        if (result == 0) {
            //It's executed in a background thread
            new Task.Backgroundable(project, DIALOG_TITLE, true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setText(INDICATOR_TEXT);
                    try {
                        @NotNull File dcevmRoot = new File(InfoProvider.getInstallDirectory());
                        FileUtil.createDirectory(dcevmRoot);
                        File downloadedFile = Downloader.downloadDcevm(InfoProvider.getInstallDirectory(), indicator);
                        jreState.setReady();

                        //TODO delete
                        System.out.println("DCEVM downloaded into: " + InfoProvider.getInstallDirectory());

                        ZipUtil.extract(downloadedFile, dcevmRoot, null);
                        FileUtil.delete(downloadedFile);
                    } catch (IOException e) {
                        deleteDcevmJreDir();

                        //TODO delete
                        System.out.println("IOException: " + e.getMessage());

                        if (!indicator.isCanceled()) {
                            ApplicationManager.getApplication().invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                                    if (statusBar != null) {
                                        JBPopupFactory.getInstance()
                                            .createHtmlTextBalloonBuilder(ERROR_MESSAGE, MessageType.ERROR, null)
                                            .setFadeoutTime(7500)
                                            .createBalloon()
                                            .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                              Balloon.Position.atRight);
                                    }
                                }
                            });
                        }
                    }
                }

                //it's executed in AWT Event thread
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
                    deleteDcevmJreDir();
                }

            }.setCancelText(CANCEL_TEXT).queue();
        }
    }

    private void patchOpenProjects() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project: openProjects) {
            TemplateAlterJreSettingsReplacer.replaceWith(project, InfoProvider.getInstallDirectory(), true);
        }
    }

    private void deleteDcevmJreDir() {
        File root = new File(InfoProvider.getInstallDirectory());

        //TODO delete
        System.out.println("Deleting " + root.getAbsolutePath());

        FileUtil.delete(root);
    }

}
