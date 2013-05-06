package ru.spbau.install.info;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.install.info.specific.JreUrlsProvider;

import java.io.File;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 8:58 PM
 */
public class InfoProviderImpl implements InfoProvider {

  private static final String JRE_DIRECTORY = "DCEVM_JRE";
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
        return null;
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

  @Override
  @NotNull
  public String getInstallDirectory() {
    return INSTALL_DIRECTORY;
  }
}
