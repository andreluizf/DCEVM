package ru.spbau.install.info;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:59 PM
 */
public interface InfoProvider {

  /**
   * @return zip archive url containing dcevm jre for current platform
   */
  @Nullable
  String getJreUrl();

  @Nullable
  Float getCurrentServerJreVersion();

  @NotNull
  String getInstallDirectory();

}
