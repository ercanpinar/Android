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

package com.streethawk.library.growth;

import org.json.JSONObject;

public interface IGrowth{
    /**
     * Returns shareUrl for user to share with friends
     * @param shareUrl
     */
    public void onReceiveShareUrl(final String shareUrl);

    /**
     * returns error response
     * @param errorResponse
     */
    public void onReceiveErrorForShareUrl(final JSONObject errorResponse);

    /**
     *Function returns deeplink url to be launched on successful match
     * @param deeplinkUrl
     */
    public void onReceiveDeepLinkUrl(final String deeplinkUrl);
}
