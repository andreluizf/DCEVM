package ru.spbau.dcevm;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.platform.templates.github.DownloadUtil;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author Denis Zhdanov
 * @since 5/31/13 6:36 PM
 */
public class DcevmNetworkManager {
  
  private static final Logger LOG = Logger.getInstance("#" + DcevmNetworkManager.class.getName());
  private DcevmNotificationManager myDcevmNotificationManager;

  public DcevmNetworkManager(@NotNull DcevmNotificationManager dcevmNotificationManager) {
    myDcevmNotificationManager = dcevmNotificationManager;
  }

  public void download(@NotNull final Project project,
                       @NotNull final File destination,
                       @NotNull final String url,
                       @NotNull final ProgressIndicator indicator,
                       @NotNull final Runnable callback)
  {
    try {
      DownloadUtil.downloadContentToFile(indicator, url, destination);
      callback.run();
    }
    catch (IOException e) {
      LOG.debug(e);
      myDcevmNotificationManager.notifyServerError(project);
    }
  }


  /**
   * Tries to derive size of the content referenced by the given url.
   * <p/>
   * Performs I/O, that's why is expected to not be called from EDT.
   *
   * @param url  url which points to the target content
   * @return     positive value which identifies size of the content referenced by the given url;
   *             non-positive value as an indication that target content size is unknown
   */
  public int getSize(@NotNull String url) {
    try {
      return HttpRequests.request(url).connect(new HttpRequests.RequestProcessor<Integer>() {
        @Override
        public Integer process(@NotNull HttpRequests.Request request) throws IOException {
          return request.getConnection().getContentLength();
        }
      });
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

}
