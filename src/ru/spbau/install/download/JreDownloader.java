package ru.spbau.install.download;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
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

  private JreStateProvider jreState;

  @NotNull
  private final String homeDir;
  @Nullable
  private final String jreUrl;

  @Nullable
  private Runnable onSuccessCallback;
  @Nullable
  private Runnable onCancelCallback;



  public JreDownloader(InfoProvider infoProvider, JreStateProvider jreState) {
    this.homeDir = infoProvider.getInstallDirectory();
    this.jreUrl = infoProvider.getJreUrl();
    this.jreState = jreState;
  }

  /**
   * @param onSuccessCallback Executed on AWT Event Thread
   */
  public void setOnSuccessCallback(@Nullable Runnable onSuccessCallback) {
    this.onSuccessCallback = onSuccessCallback;
  }

  /**
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
          if (jreUrl == null) {
            new Notification(DIALOG_TITLE, "Dcevm jre url not found", ERROR_DESCRIPTION, NotificationType.ERROR).notify(project);
            indicator.cancel();
            indicator.checkCanceled();
          }

          File dcevmRoot = new File(homeDir);
          FileUtil.createDirectory(dcevmRoot);
          File downloadedFile = HttpDownloader.download(jreUrl, homeDir, indicator);

          jreState.setReady();

          ZipUtil.extract(downloadedFile, dcevmRoot, null);
          FileUtil.delete(downloadedFile);

          //TODO think about it :)
          if (!SystemInfo.isWindows) {
            String java = homeDir + File.separator + "bin" + File.separator + "java";
            FileUtil.setExecutableAttribute(java, true);
          }

        }
        catch (IOException e) {
          deleteDcevmJreDir();
          if (!indicator.isCanceled()) {
            new Notification(DIALOG_TITLE, DOWNLOAD_ERROR, ERROR_DESCRIPTION, NotificationType.ERROR).notify(project);
          }
          else {
            if (onCancelCallback != null) onCancelCallback.run();
          }
        }
        catch (ProcessCanceledException e) {
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
    FileUtil.delete(root);
  }


}
