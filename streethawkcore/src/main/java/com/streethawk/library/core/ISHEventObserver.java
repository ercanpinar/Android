package com.streethawk.library.core;

/**
 * Implement ISHEventObserver to get notified when your application is successfully registered with StreetHawk
 */
public interface ISHEventObserver {
    /**
     * Function is called when install s registered with the device
     *
     * @param installId installid for your install
     */
    public void onInstallRegistered(String installId);
}
