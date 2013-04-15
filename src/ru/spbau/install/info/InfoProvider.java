package ru.spbau.install.info;


import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import ru.spbau.install.info.specific.JreUrls;
import ru.spbau.install.info.specific.JreHomePath;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 8:58 PM
 */
public class InfoProvider {
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
        return JreHomePath.getLinuxJrePath();
    }
}
