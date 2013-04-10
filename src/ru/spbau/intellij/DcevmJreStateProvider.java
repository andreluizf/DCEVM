package ru.spbau.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * User: user
 * Date: 4/10/13
 * Time: 4:46 PM
 */
public class DcevmJreStateProvider implements ApplicationComponent {
    private boolean dcevmReady;

    public DcevmJreStateProvider() {
    }

    public void initComponent() {
        dcevmReady = validateDcevmJre();
        if (!dcevmReady) {
            int result = Messages.showYesNoDialog("Download DCEVM Jre?", "DCEVM plugin", null);
            Messages.showErrorDialog("Download: " + (result == 0 ? "OK" : "NO"), "You have chosen");
        }
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "DcevmJreStateProvider";
    }

    public boolean isDcevmReady() {
        if (!dcevmReady) {
            dcevmReady = validateDcevmJre();
        }
        return dcevmReady;
    }

    private boolean validateDcevmJre() {
        // some validation
        return false;
    }

}
