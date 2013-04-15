package ru.spbau.launch;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;


/**
 * User: user
 * Date: 4/10/13
 * Time: 4:46 PM
 */
public class JreStateProvider implements ApplicationComponent {
    private static final String DCEVM_DOWNLOAD_STATE = "DCEVM_DOWNLOAD_STATE";
    private volatile boolean isReady;

    public JreStateProvider() {
    }
    public void initComponent() {
        isReady = PropertiesComponent.getInstance().getBoolean(DCEVM_DOWNLOAD_STATE, false);
    }
    public void disposeComponent() {
    }
    @NotNull
    public String getComponentName() {
        return "JreStateProvider";
    }

    public boolean isReady() {
        return isReady;
    }
    public void setReady() {
        isReady = true;
        PropertiesComponent.getInstance().setValue(DCEVM_DOWNLOAD_STATE, Boolean.toString(isReady));
    }
    public void setUnready() {
        isReady = false;
        PropertiesComponent.getInstance().setValue(DCEVM_DOWNLOAD_STATE, Boolean.toString(isReady));
    }
}
