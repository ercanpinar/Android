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
package com.streethawk.library.feeds;
interface Constants {
    String SHFEEDTIMESTAMP        = "shfeedtimestamp";

    //Platform types
    int PLATFORM_ANDROID_NATIVE = 0;
    int PLATFORM_PHONEGAP       = 1;
    int PLATFORM_TITANIUM       = 2;
    int PLATFORM_XAMARIN        = 3;
    int PLATFORM_UNITY          = 4;

    /*Debugging*/
    String NEWLINE = "\n";
    String COLON = " : ";

    String SHSHARED_PREF_FEEDLIST = "shfeedlist";  //Shared prefs store list of feeds received
    String VALUE                    = "value";
    String ALL_FEEDS                = "all";
    String OFFSET                   = "offset";
    String SHFEEDID                 = "feed_id";
    String SHRESULT                 = "result";
    String FEEDRESULT_STATUS        = "status";

    int CODE_FEED_ACK               = 8200;
    int CODE_FEED_RESULT            = 8201;
    String FEED_EXPIRED             = "feed_expired";

    String STATUS                   = "result";
    String RESULT_ID                = "step_id";
    String RESULT_RESULT            = "status";
    String RESULT_FEED_DELETE       = "feed_delete";
    String RESULT_FEED_COMPLETED    = "complete";
    String ID                       = "id";
}
