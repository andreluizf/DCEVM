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
//        return "http://localhost:8000/j2re-dcevm-linux64.zip";
        return "https://dl.dropboxusercontent.com/u/62224416/j2re-dcevm-linux64.zip";
    }

    public static String getLinuxJvmLib() {
        return "https://github.com/sweetybanana/DCEVM/raw/master/lib/jre.zip";
    }
}
