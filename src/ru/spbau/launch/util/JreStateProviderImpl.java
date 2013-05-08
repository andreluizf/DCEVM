package ru.spbau.launch.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * User: user
 * Date: 4/10/13
 * Time: 4:46 PM
 */
public class JreStateProviderImpl implements JreStateProvider {
  private static final String DCEVM_DOWNLOAD_STATE = "DCEVM_DOWNLOAD_STATE";
  private volatile boolean isReady;
  private AtomicBoolean downloadStarted = new AtomicBoolean(false);

  @Override
  public boolean tryStartDownloading() {
    return downloadStarted.compareAndSet(false, true);
  }

  @Override
  public boolean isDownloading() {
    return downloadStarted.get();
  }

  @Override
  public void cancelDownload() {
    downloadStarted.set(false);
  }

  public JreStateProviderImpl() {
    isReady = PropertiesComponent.getInstance().getBoolean(DCEVM_DOWNLOAD_STATE, false);
  }

  @Override
  public boolean isReady() {
    return isReady;
  }

  //It could be executed from everywhere
  @Override
  public void setReady() {
    isReady = true;
    saveState();
  }

  @Override
  public void setUnready() {
    isReady = false;
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
          }
        });
      }
    });
  }
}
