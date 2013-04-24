package ru.spbau.launch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.io.ZipUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.install.Downloader;
import ru.spbau.install.info.InfoProvider;

import java.io.File;
import java.io.IOException;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:28 PM
 */
public class JreDownloader {

    public static final String DIALOG_MESSAGE = "Download dcevm?";
    public static final String DIALOG_TITLE = "DCEVM plugin";
    public static final String INDICATOR_TEXT = "Downloading DCEVM jre";
    public static final String ERROR_MESSAGE = "<html>DCEVM download:<br>IO Error during downloading</html>";
    public static final String CANCEL_TEXT = "Stop downloading";

    private final String homeDir;
    private final String jreUrl;
    private JreStateProvider jreState;

    private Runnable onSuccessCallback;
    private Runnable onCancelCallback;

    public JreDownloader(InfoProvider infoProvider, JreStateProvider jreState) {
        this.homeDir = infoProvider.getInstallDirectory();
        this.jreUrl = infoProvider.getJreUrl();
        this.jreState = jreState;
    }

    public void setOnSuccessCallback(@Nullable Runnable onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    public void setOnCancelCallback(@Nullable Runnable onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
    }

    public void download(final Project project) {
        //It's executed in a background thread
        new Task.Backgroundable(project, DIALOG_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText(INDICATOR_TEXT);
                try {
                    File dcevmRoot = new File(homeDir);
                    FileUtil.createDirectory(dcevmRoot);
                    @NotNull Downloader downloader = ServiceManager.getService(Downloader.class);
                    File downloadedFile = downloader.downloadDcevm(homeDir, indicator);

                    jreState.setReady();

                    //TODO delete
                    System.out.println("DCEVM downloaded into: " + homeDir);

                    ZipUtil.extract(downloadedFile, dcevmRoot, null);
                    FileUtil.delete(downloadedFile);

                    //TODO think about it :)
                    if (!SystemInfo.isWindows) {
                        String java = homeDir + File.separator + "bin" + File.separator + "java";
                        System.out.println(java);
                        FileUtil.setExecutableAttribute(java, true);
                    }

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
                    } else {
                        onCancelCallback.run();
                    }
                }
            }

            //it's executed in AWT Event thread
            @Override
            public void onSuccess() {
                if (jreState.isReady()) {
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                }
            }

            @Override
            public void onCancel() {
                deleteDcevmJreDir();
                if (onCancelCallback != null) {
                    onCancelCallback.run();
                }
            }

        }.setCancelText(CANCEL_TEXT).queue();
    }


    private void deleteDcevmJreDir() {
        File root = new File(homeDir);
        //TODO delete
        System.out.println("Deleting " + root.getAbsolutePath());
        FileUtil.delete(root);
    }


}
