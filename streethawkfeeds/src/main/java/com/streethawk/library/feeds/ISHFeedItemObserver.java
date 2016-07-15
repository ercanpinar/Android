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

import org.json.JSONArray;

public interface ISHFeedItemObserver {
    public final String FEED_ID = "id";
    public final String FEED_TYPE = "type";
    public final String FEED_CONTENT = "content";

    /*TYPE_NEWS*/
    public static final String TYPE_NEWS = "news";
    public static final String NEWS_TITLE = "title";
    public static final String NEWS_MESSAGE = "message";

    /*TYPE_OFFER*/
    public static final String TYPE_OFFER           = "offer";
    public static final String OFFER_TITLE          = "title";
    public static final String OFFER_DESCRIPTION    = "description";
    public static final String OFFER_DISCOUNT       = "discount";
    public static final String OFFER_IMAGE_URL      = "image_url";


    /*TYPE_CUSTOM*/
    public static final String TYPE_CUSTOM           = "custom";


    public final int RESULT_ACCEPTED = 1;
    public final int RESULT_DISMISS = -1;
    public final int RESULT_LATER = -0;

    public void shFeedReceived(JSONArray value);
}