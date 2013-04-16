package ru.spbau.install.info;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.extensions.PluginId;
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

    private static final String JRE_DIRECTORY = "JRE";
    private static final String INSTALL_DIRECTORY = getPluginDirectory() + File.separator + JRE_DIRECTORY;

    private static String getPluginDirectory() {
        final String[] result = new String[1];
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                PluginId pluginId = PluginManager.getPluginByClassName(InfoProvider.class.getName());
                IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
                result[0] = descriptor.getPath().getAbsolutePath();
            }
        });
        return result[0];
    }

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
        return INSTALL_DIRECTORY;
    }
}
