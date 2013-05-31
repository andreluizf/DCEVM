package ru.spbau.install.info;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.net.HttpConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.install.info.specific.JreUrlsProvider;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 8:58 PM
 */
public class InfoProviderImpl implements InfoProvider {

  private static final String JRE_DIRECTORY = "JRE";
  private static final String INSTALL_DIRECTORY = getPluginDirectory() + File.separator + JRE_DIRECTORY;

  private JreUrlsProvider myUrlProvider;

  public InfoProviderImpl(JreUrlsProvider myUrlProvider) {
    this.myUrlProvider = myUrlProvider;
  }

  private static String getPluginDirectory() {
    PluginId pluginId = PluginManager.getPluginByClassName(InfoProviderImpl.class.getName());
    IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
    assert (descriptor != null);
    return descriptor.getPath().getAbsolutePath();
  }

  @Override
  @Nullable
  public String getJreUrl() {
    if (SystemInfo.is32Bit) {
      if (SystemInfo.isLinux) {
        return myUrlProvider.getLinux32Url();
      }
    }
    if (SystemInfo.is64Bit) {
      if (SystemInfo.isLinux) {
        return myUrlProvider.getLinux64Url();
      }
      if (SystemInfo.isMac) {
        return myUrlProvider.getMacOsUrl();
      }
    }
    return null;
  }

  @Nullable
  @Override
  public Float getCurrentServerJreVersion() {
    String jreUrl = getJreUrl();
    if (jreUrl == null)
      return null;
    String url = jreUrl.substring(0, jreUrl.lastIndexOf(".")) + ".version";

    HttpURLConnection connection = null;
    Float version = null;
    try {
      connection = HttpConfigurable.getInstance().openHttpConnection(url);
      final InputStream is = connection.getInputStream();
      if (is == null) {
        throw new IOException("Failed to open connection");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      try {
        version = Float.valueOf(reader.readLine());
      }
      catch (NumberFormatException e) {
      }
      is.close();
    }
    catch (IOException e) {
      return null;
    }
    finally {
      if (connection != null) connection.disconnect();
    }

    return version;
  }

  @Override
  @NotNull
  public String getInstallDirectory() {
    return INSTALL_DIRECTORY;
  }
}
