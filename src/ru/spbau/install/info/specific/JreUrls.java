package ru.spbau.install.info.specific;

/**
 * User: user
 * Date: 4/10/13
 * Time: 7:21 PM
 */
public class JreUrls {
    private JreUrls() {
    }

    public static String getLinuxUrl() {
        return "https://github.com/sweetybanana/DCEVM/raw/master/lib/j2re-dcevm-linux64.zip";
    }

    public static String getLinuxJvmLib() {
        return "https://github.com/sweetybanana/DCEVM/raw/master/lib/jre.zip";
    }
}
