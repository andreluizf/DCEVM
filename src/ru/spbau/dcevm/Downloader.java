package ru.spbau.dcevm;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.UrlConnectionUtil;
import com.intellij.util.net.HttpConfigurable;
import com.intellij.util.net.NetUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * User: user
 * Date: 4/10/13
 * Time: 6:53 PM
 */
public class Downloader {
    private String destAddress;

    public Downloader(String destAddress) {
        this.destAddress = destAddress;
    }

    public File downloadDcevm(final ProgressIndicator pi) throws IOException {
        final File pluginsTemp = new File(destAddress);


        if (!pluginsTemp.exists() && !pluginsTemp.mkdirs()) {
            throw new IOException(IdeBundle.message("error.cannot.create.temp.dir", pluginsTemp));
        }

        final File file = FileUtil.createTempFile(pluginsTemp, "plugin_", "_download", true, false);

//        pi.setText(IdeBundle.message("progress.connecting"));

        URLConnection connection = null;
        String dcevmUrl = DcevmUrlProvider.getLinuxUrl();
        String finalName = "DCEVM.zip";

        try {
            connection = HttpConfigurable.getInstance().openHttpConnection(dcevmUrl);
            final InputStream is = UrlConnectionUtil.getConnectionInputStream(connection, pi);
            if (is == null) {
                throw new IOException("Failed to open connection");
            }

            pi.setText("Downloading DCEVM jre");
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


            final File newFile = new File(file.getParentFile(), finalName);
            FileUtil.rename(file, newFile);
            return newFile;
        }
        finally {
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).disconnect();
            }
        }
    }

}
