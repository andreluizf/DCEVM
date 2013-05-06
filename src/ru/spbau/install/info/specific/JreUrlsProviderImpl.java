package ru.spbau.install.info.specific;

import com.intellij.util.SystemProperties;

/**
 * User: user
 * Date: 4/10/13
 * Time: 7:21 PM
 */
public class JreUrlsProviderImpl implements JreUrlsProvider {

  private static final String LOCALHOST_TEST = "http://localhost:8000/j2re-dcevm-linux64.zip";
  private static final String DROPBOX_LINUX_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-linux64.zip";
  private static final String DROPBOX_MACOS_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-macos64.zip";
  public static final String LOCAL_DCEVM_DOWNLOAD = "local.dcevm.download";

  @Override
  public String getLinux64Url() {
    if (SystemProperties.getBooleanProperty(LOCAL_DCEVM_DOWNLOAD, false)) {
      return LOCALHOST_TEST;
    }
    return DROPBOX_LINUX_64_URL;
  }

  @Override
  public String getMacOsUrl() {
    return DROPBOX_MACOS_64_URL;
  }

}
