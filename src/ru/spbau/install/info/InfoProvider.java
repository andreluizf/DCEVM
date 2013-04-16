package ru.spbau.install.info;


import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import ru.spbau.install.info.specific.JreUrls;

import java.io.File;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 8:58 PM
 */
public class InfoProvider {

    //TODO maybe find plugin directory and then
    public static final String JRE_DIRECTORY = "DCEVM_JRE";

    @NotNull
    public static String getJreUrl() {
        if (SystemInfo.is32Bit) {
            if (SystemInfo.isLinux) {
                return JreUrls.getLinuxUrl();
            }
        }
        if (SystemInfo.is64Bit) {
            if (SystemInfo.isLinux) {
                return JreUrls.getLinuxUrl();
            }
        }
        return null;
    }

    @NotNull
    public static String getInstallDirectory() {

        return PathManager.getPluginsPath() + File.separator + JRE_DIRECTORY;
    }
}
