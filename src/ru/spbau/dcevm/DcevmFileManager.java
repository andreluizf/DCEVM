package ru.spbau.dcevm;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Denis Zhdanov
 * @since 5/31/13 4:59 PM
 */
public class DcevmFileManager {

  @NotNull private static final String JRE_DIR_NAME = "jre";
  @NotNull private static final String JAVA_EXEC = "bin" + File.separator + "java";
  
  @NotNull private static final NotNullLazyValue<String> PLUGIN_HOME = new NotNullLazyValue<String>() {
    @NotNull
    @Override
    protected String compute() {
      PluginId pluginId = PluginManager.getPluginByClassName(DcevmFileManager.class.getName());
      IdeaPluginDescriptor descriptor = PluginManager.getPlugin(pluginId);
      assert (descriptor != null);
      return descriptor.getPath().getAbsolutePath(); 
    }
  };
  
  @NotNull
  public File getDcevmDir() {
    return new File(PLUGIN_HOME.getValue(), JRE_DIR_NAME);
  }

  @NotNull
  public File getJavaExecutable() {
    return new File(getDcevmDir(), JAVA_EXEC);
  }

}
