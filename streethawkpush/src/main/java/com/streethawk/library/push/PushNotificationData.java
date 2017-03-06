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

import android.util.Log;

import com.streethawk.library.core.Util;

class PushNotificationData {

    private final String SUBTAG = "PushNotificationData ";

    String mMsgId = null;
    String mCode = null;
    String mTitle = null;
    String mMsg = null;
    String mData = null;
    String mPortion = null;
    String mOrientation = null;
    String mSpeed = null;
    String mNoDialog = null;
    String mSound = null;
    String mBadge = null;
    String mContentAvailable = null;      // for interactive push
    String mCategory = null;      // for interactive push


    /* Start customisable  button*/

    String mBtn1Title = null;   // Custom button 1 title
    String mBtn2Title = null;   // Custom button 2 title
    String mBtn3Title = null;   // Custom button 3 title

    int mIC1 = -1;     // Icon code for button 1
    int mIC2 = -1;     // Icon code for button 2
    int mIC3 = -1;     // Icon code for button 3

    /* End customisable push*/

    public void setMsgId(String msgId) {
        this.mMsgId = msgId;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public void setPortion(String portion) {
        this.mPortion = portion;
    }

    public void setOrientation(String orientation) {
        this.mOrientation = orientation;
    }

    public void setSpeed(String speed) {
        this.mSpeed = speed;
    }

    public void setNoDialog(String NoDialog) {
        this.mNoDialog = NoDialog;
    }

    public void setBadge(String badge) {
        this.mBadge = badge;
    }

    public void setSound(String sound) {
        this.mSound = sound;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }


    /* Start customisable push */
    public void setBtn1Title(String title) {
        this.mBtn1Title = title;
    }

    public void setBtn2Title(String title) {
        this.mBtn2Title = title;
    }

    public void setBtn3Title(String title) {
        this.mBtn3Title = title;
    }

    public void setBtn1Icon(int icon) {
        this.mIC1 = icon;
    }

    public void setBtn2Icon(int icon) {
        this.mIC2 = icon;
    }

    public void setBtn3Icon(int icon) {
        this.mIC3 = icon;
    }
    /* End customisable push */

    /*For interactive push*/
    public void setContentAvailable(String contentAvailable) {
        this.mContentAvailable = contentAvailable;
    }

    public String getMsgId() {
        return this.mMsgId;
    }

    public String getCode() {
        return this.mCode;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getMsg() {
        return this.mMsg;
    }

    public String getData() {
        return this.mData;
    }

    public String getPortion() {
        return this.mPortion;
    }

    public String getOrientation() {
        return this.mOrientation;
    }

    public String getSpeed() {
        return this.mSpeed;
    }

    public String getNoDialog() {
        return this.mNoDialog;
    }

    public String getBadge() {
        return this.mBadge;
    }

    public String getSound() {
        return this.mSound;
    }

    /* Start interactive push */
    public String getBtn1Title() {
        return this.mBtn1Title;
    }

    public String getBtn2Title() {
        return this.mBtn2Title;
    }

    public String getBtn3Title() {
        return this.mBtn3Title;
    }

    public int getBtn1Icon() {
        return this.mIC1;
    }

    public int getBtn2Icon() {
        return this.mIC2;
    }

    public int getBtn3Icon() {
        return this.mIC3;
    }
    /* End interactive push */

    /*For interactive push*/
    public String getContentAvailable() {
        return this.mContentAvailable;
    }

    public String getCategory() {
        return this.mCategory;
    }


    /**
     * Display push data in logcat
     */
    public void displayMyData(String Tag) {
        String NEWLINE = "\n";
        String myData = "displayMyData" + NEWLINE +
                "MsgId " + mMsgId + NEWLINE +
                "Code " + mCode + NEWLINE +
                "Title " + mTitle + NEWLINE +
                "Msg " + mMsg + NEWLINE +
                "Data " + mData + NEWLINE +
                "Portion " + mPortion + NEWLINE +
                "Orientation " + mOrientation + NEWLINE +
                "Speed " + mSpeed + NEWLINE +
                "NoDialog " + mNoDialog + NEWLINE +
                "Sound " + mSound + NEWLINE +
                "Badge " + mBadge + NEWLINE +
                "Content_Available " + mContentAvailable + NEWLINE +
                "Category " + mCategory + NEWLINE +

                /*Start interactive push*/
                "Btn1Title" + mBtn1Title + NEWLINE +
                "Btn2Title" + mBtn2Title + NEWLINE +
                "Btn3Title" + mBtn3Title + NEWLINE +
                "Icon_Btn1" + mIC1 + NEWLINE +
                "Icon_Btn2" + mIC2 + NEWLINE +
                "Icon_Btn3" + mIC3
                /*End interactive push*/;
        if (null == Tag)
            Log.i(Util.TAG, SUBTAG + myData);
        else
            Log.i(Tag, SUBTAG + myData);
    }
}

