package ru.spbau.install.info;

import org.jetbrains.annotations.NotNull;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:59 PM
 */
public interface InfoProvider {
    @NotNull
    String getJreUrl();

    @NotNull
    String getInstallDirectory();
}
