package ru.spbau.install;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.UrlConnectionUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.NetUtils;
import ru.spbau.launch.JreStateProvider;
import ru.spbau.install.info.InfoProvider;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * User: user
 * Date: 4/10/13
 * Time: 6:53 PM
 */
public class Downloader {
    private static final String DOWNLOADED_FILE = "DCEVM.zip";
    private static final String INDICATOR_TEXT = "Downloading DCEVM jre";

    //executed from background thread
    public static File downloadDcevm(String destAddress, final ProgressIndicator pi) throws IOException {
        final File pluginsTemp = new File(destAddress);
        if (!pluginsTemp.exists() && !pluginsTemp.mkdirs()) {
            throw new IOException(IdeBundle.message("error.cannot.create.temp.dir", pluginsTemp));
        }

        final File file = FileUtil.createTempFile(pluginsTemp, "plugin_", "_download", true, false);
        HttpURLConnection connection = null;
        try {
            connection = HttpConfigurable.getInstance().openHttpConnection(InfoProvider.getJreUrl());
            final InputStream is = UrlConnectionUtil.getConnectionInputStream(connection, pi);
            if (is == null) {
                throw new IOException("Failed to open connection");
            }

            pi.setText(INDICATOR_TEXT);
            final int contentLength = connection.getContentLength();
            pi.setIndeterminate(contentLength == -1);
            try {
                final OutputStream fos = new BufferedOutputStream(new FileOutputStream(file, false));
                try {
                    NetUtils.copyStreamContent(pi, is, fos, contentLength);
                }
                finally {
                    fos.close();
                }
            }
            finally {
                is.close();
            }

            final File newFile = new File(file.getParentFile(), DOWNLOADED_FILE);
            FileUtil.rename(file, newFile);
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().getComponent(JreStateProvider.class).setReady();
                }
            });
            return newFile;
        }
        finally {
            if (connection != null) connection.disconnect();
        }
    }

}
