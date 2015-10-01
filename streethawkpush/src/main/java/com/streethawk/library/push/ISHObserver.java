/*
 * Copyright (c) StreetHawk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.streethawk.library.push;

/**
 *  Interface for handling Raw JSON. App developer can implement this interface in activity or service
 *  to handle Raw JSON sent from Streethawk web console.
 */
public interface ISHObserver {
    /**
     * This function is called when Raw JSON is received by Streethawk Library in form of Bundle. This method needs to be implemented
     * in application
     * @param title    Title can be displayed as title of alert dialog
     * @param message  Message can be displayed as message in alert dialog
     * @param json     Raw json to be handled by application
     */
    public void shReceivedRawJSON(String title, String message, String json);
    /**
     * API to support launch app page feature in cross platforms. Ignore this method in you are developing on Native
     * This function is called when streethawk library receives app page request from server
     * @param pageName to be diaplayed
     */
    public void shNotifyAppPage(String pageName);

    /**
     * API is called when application receives push message from server
     * Note that
     */
    public void onReceivePushData(PushDataForApplication pushData);

    /**
     * API is called when application receives push result for a push message
     */
    public void onReceiveResult(PushDataForApplication resultData,int result);

}
