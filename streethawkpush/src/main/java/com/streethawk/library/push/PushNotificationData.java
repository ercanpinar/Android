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

class PushNotificationData{

    private final String SUBTAG = "PushNotificationData ";


    String mMsgId         = null;
    String mCode          = null;
    String mTitle         = null;
    String mMsg           = null;
    String mData          = null;
    String mPortion       = null;
    String mOrientation   = null;
    String mSpeed         = null;
    String mNoDialog      = null;
    String mSound         = null;
    String mBadge         = null;

    public void setMsgId(String msgId){this.mMsgId=msgId;}
    public void setCode(String code){this.mCode=code;}
    public void setTitle(String title){this.mTitle =title;}
    public void setMsg(String msg){this.mMsg=msg;}
    public void setData(String data){this.mData=data;}
    public void setPortion(String portion){this.mPortion=portion;}
    public void setOrientation(String orientation){this.mOrientation=orientation;}
    public void setSpeed(String speed){this.mSpeed=speed;}
    public void setNoDialog(String NoDialog){this.mNoDialog = NoDialog;}
    public void setBadge(String badge){this.mSound=badge;}
    public void setSound(String sound){this.mBadge=sound;}

    public String getMsgId(){return this.mMsgId;}
    public String getCode(){return this.mCode;}
    public String getTitle(){return this.mTitle;}
    public String getMsg(){return this.mMsg;}
    public String getData(){return this.mData;}
    public String getPortion(){return this.mPortion;}
    public String getOrientation(){return this.mOrientation;}
    public String getSpeed(){return this.mSpeed;}
    public String getNoDialog(){return this.mNoDialog;}
    public String getBadge(){return this.mSound;}
    public String getSound(){return this.mBadge;}

    public void displayMyData(){
        String NEWLINE = "\n";
        String myData = "displayMyData" + NEWLINE +
                "MsgId "+ mMsgId + NEWLINE +
                "Code " + mCode + NEWLINE +
                "Title "+ mTitle + NEWLINE +
                "Msg " + mMsg + NEWLINE +
                "Data " + mData + NEWLINE +
                "Portion "+  mPortion + NEWLINE +
                "Orientation "+ mOrientation + NEWLINE +
                "Speed " + mSpeed + NEWLINE +
                "NoDialog "+ mNoDialog + NEWLINE +
                "Sound " + mSound + NEWLINE +
                "Badge " + mBadge;
        Log.i(Util.TAG, SUBTAG + myData);
    }
}

