package ru.spbau.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import ru.spbau.dcevm.Downloader;

import java.io.IOException;

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
            if (result == 0) {
                System.out.println("Starting download");
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Downloader dcevmLoader = new Downloader("/home/user/");
                        ProgressIndicator pi = new EmptyProgressIndicator();
                        try {
                            dcevmLoader.downloadDcevm(pi);
                        } catch (IOException e) {
                            Messages.showErrorDialog(e.getMessage(), "IO Error during downloading");
                        }
                    }
                });
            }
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
