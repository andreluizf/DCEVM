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
     * @return url for downloading dcevm jre for current platform
     */
    @Nullable
    String getJreUrl();

    @NotNull
    String getInstallDirectory();
}
