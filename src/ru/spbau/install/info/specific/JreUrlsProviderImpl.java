package ru.spbau.install.info.specific;

/**
 * User: user
 * Date: 4/10/13
 * Time: 7:21 PM
 */
public class JreUrlsProviderImpl implements JreUrlsProvider {

    private static final String LOCALHOST_TEST = "http://localhost:8000/j2re-dcevm-linux64.zip";
    private static final String DROPBOX_LINUX_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-linux64.zip";
    private static final String DROPBOX_MACOS_64_URL = "https://dl.dropboxusercontent.com/u/62224416/j2re-macos64.zip";

    @Override
    public String getLinuxUrl() {
        return DROPBOX_LINUX_64_URL;
    }

    @Override
    public String getMacOsUrl() {
        return DROPBOX_MACOS_64_URL;
    }

}
