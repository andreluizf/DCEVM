package ru.spbau.install.download;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.ZipUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.install.download.util.HttpDownloader;
import ru.spbau.install.info.InfoProvider;
import ru.spbau.launch.util.JreStateProvider;

import java.io.File;
import java.io.IOException;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:28 PM
 */
public class JreDownloader {

    public static final String DIALOG_TITLE = "DCEVM plugin";
    public static final String INDICATOR_TEXT = "Downloading DCEVM jre";
    public static final String CANCEL_TEXT = "Stop downloading";
    public static final String DOWNLOAD_ERROR = "Dcevm download error";
    public static final String ERROR_DESCRIPTION = "IO error while downloading happened";


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

    /**
     *
     * @param onSuccessCallback Executed on AWT Event Thread
     */
    public void setOnSuccessCallback(@Nullable Runnable onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    /**
     *
     * @param onCancelCallback Executed on AWR Event Thread
     */
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
                    File downloadedFile = HttpDownloader.download(jreUrl, homeDir, indicator);

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
                        Notifications.Bus.notify(new Notification(DIALOG_TITLE, DOWNLOAD_ERROR, ERROR_DESCRIPTION, NotificationType.ERROR));
                    } else {
                        onCancelCallback.run();
                    }
                } catch (ProcessCanceledException e) {
                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            jreState.cancelDownload();
                        }
                    });
                    throw e;
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
