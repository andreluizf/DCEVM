package ru.spbau.dcevm;

import com.intellij.execution.ui.JreProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Denis Zhdanov
 * @since 5/31/13 8:51 PM
 */
public class DcevmJreProvider implements JreProvider {
  
  @NotNull private final DcevmFileManager myFileManager;

  public DcevmJreProvider(@NotNull DcevmFileManager fileManager) {
    myFileManager = fileManager;
  }

  @NotNull
  @Override
  public String getJrePath() {
    File dir = myFileManager.getDcevmDir();
    return (dir.isDirectory() && dir.list().length > 0) ? dir.getAbsolutePath() : "";
  }
}
