package com.streethawk.library.core;

/**
 * Created by anuragkondeya on 18/11/2015.
 */
public interface ISHEventObserver {
    /**
     * Function is called when install s registered with the device
     * @param installId
     */
    public void onInstallRegistered(String installId);
}
