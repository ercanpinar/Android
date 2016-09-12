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


    String BOTTOM = "bottom";
    String TOP = "top";
    String LEFT = "left";
    String RIGHT = "right";

    String SHSHARED_PREF_FEEDLIST = "shfeedlist";  //Shared prefs store list of feeds received
    String VALUE                  = "value";
    String ALL_FEEDS                = "all";
    String OFFSET                   = "offset";
    String SHFEEDID                 = "feed_id";
    String SHRESULT                 = "result";
    String FEEDRESULT_STATUS        = "status";

    int CODE_FEED_ACK     = 8200;
    int CODE_FEED_RESULT  = 8201;
    String FEED_EXPIRED   = "feed_expired";

    String STATUS               = "status";
    String RESULT_ID            = "id";
    String RESULT_RESULT        = "result";
    String RESULT_FEED_DELETE   = "feed_delete";

    String TIP_ID           = "id";
    String TITLE            = "title";
    String CONTENT          = "content";
    String TITLE_COLOR      = "titlecolor";
    String CONTENT_COLOR    = "contentcolor";
    String BG_COLOR         = "bgclr";
    String TARGET           = "target";
    String CHILD            = "child";
    String PLACEMENT        = "placement";
    String DELAY            = "delay";
    String NEXT_BUTTON      = "nextBtn";
    String PREV_BUTTON      = "prevBtn";
    String PAIR             = "pair";
    String IMG_URL          = "img";
    String PARENT           = "view";
    String SETUP            = "setup";
    String INIT_TOOL        = "tool";     //INIT is same as setup
    String TOUR             = "tour";
    String TIP              = "tip";
    String MODAL            = "modal";
    String FEED             = "feed";
    String NEWS             = "news";
    String DATA             = "d";
    String CUSTOM_DATA      = "customData";
    String PAYLOAD_SHFEEDID = "id";

    String CLOSE_BUTTON     = "closeBtn";
    String DND              = "DND";
    String DND_TITLE        = "title";
    String DND_CONTENT      = "content";
    String DND_B1           = "b1";
    String DND_B2           = "b2";


    /*Triggers params*/

    String SETUP_DISPLAY            = "display";
    String SETUP_TRIGGER            = "trigger";
    String SETUP_TARGET             = "target";
    String SETUP_VIEW               = "view";
    String SETUP_DELAY              = "delay";
    String SETUP_TOOL               = "tool";
    String SETUP_WIDGET             = "widget";
    String SETUP_WIDGET_TYPE        = "type";
    String SETUP_WIDGET_LABEL       = "label";
    String SETUP_WIDGET_CSS         = "css";
    String SETUP_WIDGET_BGCOLOR     = "bgcolor";
    String SETUP_WIDGET_PLACEMENT   = "placement";


    String SETUP_TOOL_TOUR          = "tour";
    String SETUP_TOOL_FEED          = "feed";
    String SETUP_TOOL_TIP           = "tip";
    String SETUP_TOOL_MODAL         = "modal";

    /*Trigger clicks*/
    String TRIGER_CLICK             = "click";
    String TRIGER_TOUCH             = "touch";
    String TRIGER_HOVER             = "hover";

    /*Triger types*/
    String DOT                      = "dot";







}
