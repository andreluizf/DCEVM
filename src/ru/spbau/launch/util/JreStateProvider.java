package ru.spbau.launch.util;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:17 PM
 */
public interface JreStateProvider {

  boolean isReady();

  void setReady();

  void setUnready();

  boolean tryStartDownloading();

  boolean isDownloadingOrDownloaded();

  void cancelDownload();

}
