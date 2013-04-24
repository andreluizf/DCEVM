package ru.spbau.launch;

/**
 * User: user
 * Date: 4/24/13
 * Time: 8:17 PM
 */
public interface JreStateProvider {
    boolean isReady();

    void setReady();

    void setUnready();
}
