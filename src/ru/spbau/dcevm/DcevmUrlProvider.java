package ru.spbau.dcevm;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.Nullable;

/**
 * User: user
 * Date: 4/25/13
 * Time: 7:06 PM
 */
public class DcevmUrlProvider {
  //private static final String DROPBOX_LINUX_32_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-linux32.zip";
  //private static final String DROPBOX_LINUX_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-linux64.zip";
  //private static final String DROPBOX_WIN_32_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-win32.zip";
  //private static final String DROPBOX_WIN_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-win64.zip";
  //private static final String DROPBOX_MACOS_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-macos64.zip";

  private static final String LINUX32 = "http://dcevm.nentjes.com/j2re-dcevm-linux32.zip";
  private static final String LINUX64 = "http://dcevm.nentjes.com/j2re-dcevm-linux64.zip";
  private static final String MACOS64 = "http://dcevm.nentjes.com/j2re-macos64.zip";
  private static final String WIN32 = "http://dcevm.nentjes.com/j2re-dcevm-win32.zip";
  private static final String WIN64 = "http://dcevm.nentjes.com/j2re-dcevm-win64.zip";

  /**
   * @return    url which points to the target assembled DCEVM to use at the current environment;
   *            <code>null</code> if no DCEVM for the current environment has been assembled
   */
  @Nullable
  public String getUrl() {
    if (SystemInfo.isLinux) {
      if (SystemInfo.is32Bit) {
        return LINUX32;
      }
      else if (SystemInfo.is64Bit) {
        return LINUX64;
      }
    }
    else if (SystemInfo.isMac) {
      return MACOS64;
    }
    else if (SystemInfo.isWindows) {
      if (SystemInfo.is32Bit) {
        return WIN32;
      }
      else if (SystemInfo.is64Bit) {
        return WIN64;
      }
    }
    return null;
  }
}
