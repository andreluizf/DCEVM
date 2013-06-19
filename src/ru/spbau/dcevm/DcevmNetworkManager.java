package ru.spbau.dcevm;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Ref;
import com.intellij.util.Consumer;
import com.intellij.util.io.UrlConnectionUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * @author Denis Zhdanov
 * @since 5/31/13 6:36 PM
 */
public class DcevmNetworkManager {
  
  private static final Logger LOG = Logger.getInstance("#" + DcevmNetworkManager.class.getName());
  
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
    final Ref<Integer> result = new Ref<Integer>();
    execute(url, new Consumer<HttpURLConnection>() {
      @Override
      public void consume(HttpURLConnection connection) {
        result.set(connection.getContentLength());
      }
    });
    return result.get();
  }

  public void download(@NotNull final File destination,
                       @NotNull final String url,
                       @NotNull final ProgressIndicator indicator,
                       @NotNull final Runnable callback)
  {
    execute(url, new Consumer<HttpURLConnection>() {
      @Override
      public void consume(HttpURLConnection connection) {
        final int contentLength = connection.getContentLength();
        
        boolean ok = false;
        InputStream in = null;
        OutputStream out = null;
        try {
          in = UrlConnectionUtil.getConnectionInputStreamWithException(connection, indicator);
          indicator.setIndeterminate(contentLength <= 0);
          out = new BufferedOutputStream(new FileOutputStream(destination, false));
          NetUtils.copyStreamContent(indicator, in, out, contentLength);
          ok = true;
        }
        catch (IOException e) {
          LOG.warn(String.format("Unexpected I/O exception occurred during downloading DCEVM from '%s' to '%s'",
                                 url, destination.getAbsolutePath()),
                   e);
        }
        finally {
          if (in != null) {
            try {
              in.close();
            }
            catch (IOException e) {
              // Ignore
            }
          }
          if (out != null) {
            try {
              out.close();
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        if (ok) {
          callback.run();
        }
      }
    });
  }

  private void execute(@NotNull String url, @NotNull Consumer<HttpURLConnection> task) {
    HttpURLConnection connection = null;
    try {
      connection = HttpConfigurable.getInstance().openHttpConnection(url);
      task.consume(connection);
      
    }
    catch (IOException e) {
      LOG.warn(String.format("Unexpected I/O exception occurred during attempt to work with content at '%s'", url), e);
    }
    finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}
