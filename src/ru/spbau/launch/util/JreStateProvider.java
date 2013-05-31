package ru.spbau.launch.util;

import org.jetbrains.annotations.Nullable;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:17 PM
 */
public interface JreStateProvider {

  boolean isReady();

  float getVersion();

  void setReady(@Nullable Float downloadedVersion);

  void setUnready();

  void setDeleted();

  boolean tryStartDownloading();

  boolean isDownloadingOrDownloaded();

  void cancelDownload();

}
