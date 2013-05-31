package ru.spbau.launch.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * User: user
 * Date: 4/10/13
 * Time: 4:46 PM
 */
public class JreStateProviderImpl implements JreStateProvider {
  private static final String DCEVM_DOWNLOAD_STATE = "DCEVM_DOWNLOAD_STATE";
  private static final String DCEVM_VERSION = "DCEVM_VERSION";
  private volatile boolean isReady;
  private volatile float version;
  private AtomicBoolean downloadStarted = new AtomicBoolean(false);

  @Override
  public boolean tryStartDownloading() {
    return downloadStarted.compareAndSet(false, true);
  }

  @Override
  public boolean isDownloadingOrDownloaded() {
    return downloadStarted.get();
  }

  @Override
  public void cancelDownload() {
    downloadStarted.set(false);
  }

  public JreStateProviderImpl() {
    isReady = PropertiesComponent.getInstance().getBoolean(DCEVM_DOWNLOAD_STATE, false);
    version = PropertiesComponent.getInstance().getFloat(DCEVM_VERSION, -1);
  }

  @Override
  public boolean isReady() {
    return isReady;
  }

  //It could be executed from everywhere
  @Override
  public void setReady(@Nullable Float downloadedVersion) {
    version = (downloadedVersion == null) ? 0 : downloadedVersion;
    isReady = true;
    saveState();
  }

  @Override
  public float getVersion() {
    return version;
  }

  @Override
  public void setUnready() {
    isReady = false;
    saveState();
  }

  public void setDeleted() {
    isReady = false;
    version = -1;
    saveState();
  }

  private void saveState() {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          @Override
          public void run() {
            PropertiesComponent.getInstance().setValue(DCEVM_DOWNLOAD_STATE, Boolean.toString(isReady));
            PropertiesComponent.getInstance().setValue(DCEVM_VERSION, Float.toString(version));
          }
        });
      }
    });
  }
}
