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

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.streethawk.library.core.Util;

/**
 * For push handling at by applications
 */
public class PushDataForApplication extends NotificationBase{

    private final String SUBTAG = "PushDataForApplication ";

    public PushDataForApplication(){
        super();
    }

    /**
     * ACTION_OPEN_URL = 1;
     */
    public static final int ACTION_OPEN_URL = 1;
    /**
     * ACTION_LAUNCH_ACTIVITY = 2
     */
    public static final int ACTION_LAUNCH_ACTIVITY = 2;
    /**
     * ACTION_RATE_APP = 3
     */
    public static final int ACTION_RATE_APP = 3;
    /**
     * ACTION_USER_REGISTRATION_SCREEN = 4
     */
    public static final int ACTION_USER_REGISTRATION_SCREEN = 4;
    /**
     * ACTION_USER_LOGIN_SCREEN = 5
     */
    public static final int ACTION_USER_LOGIN_SCREEN = 5;
    /**
     * ACTION_UPDATE_APP = 6
     */
    public static final int ACTION_UPDATE_APP = 6;
    /**
     * ACTION_CALL_TELEPHONE_NUMBER = 7
     */
    public static final int ACTION_CALL_TELEPHONE_NUMBER = 7;
    /**
     * ACTION_SIMPLE_PROMPT = 8
     */
    public static final int ACTION_SIMPLE_PROMPT = 8;
    /**
     * ACTION_FEEDBACK = 9
     */
    public static final int ACTION_FEEDBACK = 9;
    /**
     * ACTION_ENABLE_BLUETOOTH = 10
     */
    public static final int ACTION_ENABLE_BLUETOOTH = 10;
    /**
     * ACTION_ENABLE_PUSH_MSG = 11
     */
    public static final int ACTION_ENABLE_PUSH_MSG = 11;
    /**
     * ACTION_ENABLE_LOCATION = 12
     */
    public static final int ACTION_ENABLE_LOCATION = 12;

    /**
     * ACTION_ENABLE_LOCATION = 13
     */
    public static final int ACTION_INTERACTIVE_PUSH = 13;


    /**
     * RESULT_ACCEPTED = 1
     */
    public static final int RESULT_ACCEPTED = 1;
    /**
     * RESULT_DECLINED = -1
     */
    public static final int RESULT_DECLINED = -1;
    /**
     * RESULT_POSTPONED = 0;
     */
    public static final int RESULT_POSTPONED = 0;

    private int     mAction;
    private String  mMsgID;
    private String  mTitle;
    private String  mMessage;
    private String  mData;
    private Float   mPortion;
    private int     mOrientation;
    private int     mSpeed;
    private boolean mDisplayWithoutDialog;
    private String  mSound;
    private int     mBadge;
    private String  mContentAvailable   = null;  // For interactive push
    private String  mCategory           = null;  // for interactive push

    /*We are not using custom button as of now*/

    private String  mBtn1Title     = null;      // Custom button 1 title
    private String  mBtn2Title     = null;      // Custom button 2 title
    private String  mBtn3Title     = null;      // Custom button 3 title
    private String  mIC1           = null;     // Icon code for button 1
    private String  mIC2           = null;     // Icon code for button 2
    private String  mIC3           = null;     // Icon code for button 3

    public void convertPushDataToPushDataForApp(PushNotificationData data, PushDataForApplication dataForApplication) {
        if (null == dataForApplication)
            return;
        if (null != data) {
            dataForApplication.setTitle(data.getTitle());
            dataForApplication.setMessage(data.getMsg());
            dataForApplication.setData(data.getData());
            dataForApplication.setMsgId(data.getMsgId());
            float tmpPortion = -1.0f;
            int tmpOrientation = -1;
            int tmpSpeed = -1;
            int tmpBadge = 0;
            int tmpCode = 0;
            Boolean tmpNoDialog = false;
            try {
                tmpPortion = Float.parseFloat(data.getPortion());
            } catch (NumberFormatException e) {
                tmpPortion = -1.0f;
            } catch (NullPointerException e) {
                tmpPortion = -1.0f;
            }
            try {
                tmpOrientation = Integer.parseInt(data.getOrientation());
            } catch (NumberFormatException e) {
                tmpOrientation = -1;
            } catch (NullPointerException e) {
                tmpOrientation = -1;
            }
            try {
                tmpSpeed = Integer.parseInt(data.getSpeed());
            } catch (NumberFormatException e) {
                tmpSpeed = -1;
            } catch (NullPointerException e) {
                tmpSpeed = -1;
            }
            try {
                tmpBadge = Integer.parseInt(data.getBadge());
            } catch (NumberFormatException e) {
                tmpBadge = -0;
            } catch (NullPointerException e) {
                tmpBadge = -0;
            }
            try {
                tmpCode = Integer.parseInt(data.getCode());
            } catch (NumberFormatException e) {
                tmpCode = 0;
            } catch (NullPointerException e) {
                tmpCode = 0;
            }

            switch (tmpCode) {
                case CODE_OPEN_URL:
                    dataForApplication.setAction(PushDataForApplication.ACTION_OPEN_URL);
                    break;
                case CODE_LAUNCH_ACTIVITY:
                    dataForApplication.setAction(PushDataForApplication.ACTION_LAUNCH_ACTIVITY);
                    break;
                case CODE_RATE_APP:
                    dataForApplication.setAction(PushDataForApplication.ACTION_RATE_APP);
                    break;
                case CODE_USER_REGISTRATION_SCREEN:
                    dataForApplication.setAction(PushDataForApplication.ACTION_USER_REGISTRATION_SCREEN);
                    break;
                case CODE_USER_LOGIN_SCREEN:
                    dataForApplication.setAction(PushDataForApplication.ACTION_USER_LOGIN_SCREEN);
                    break;
                case CODE_UPDATE_APP:
                    dataForApplication.setAction(PushDataForApplication.ACTION_UPDATE_APP);
                    break;
                case CODE_CALL_TELEPHONE_NUMBER:
                    dataForApplication.setAction(PushDataForApplication.ACTION_CALL_TELEPHONE_NUMBER);
                    break;
                case CODE_SIMPLE_PROMPT:
                    dataForApplication.setAction(PushDataForApplication.ACTION_SIMPLE_PROMPT);
                    break;
                case CODE_FEEDBACK:
                    dataForApplication.setAction(PushDataForApplication.ACTION_FEEDBACK);
                    break;
                case CODE_IBEACON:
                    dataForApplication.setAction(PushDataForApplication.ACTION_ENABLE_BLUETOOTH);
                    break;
                case CODE_ACCEPT_PUSHMSG:
                    dataForApplication.setAction(PushDataForApplication.ACTION_ENABLE_PUSH_MSG);
                    break;
                case CODE_ENABLE_LOCATION:
                    dataForApplication.setAction(PushDataForApplication.ACTION_ENABLE_LOCATION);
                    break;
                case CODE_CUSTOM_ACTIONS:
                    dataForApplication.setAction(PushDataForApplication.ACTION_INTERACTIVE_PUSH);
                    break;
                default:
                    dataForApplication.setAction(0);
            }
            dataForApplication.setPortion(tmpPortion);
            dataForApplication.setOrientation(tmpSpeed);
            dataForApplication.setOrientation(tmpOrientation);
            String tmpNoDialogStr = data.getNoDialog();
            if (tmpNoDialogStr != null) {
                if (!tmpNoDialogStr.isEmpty())
                    tmpNoDialog = true;
            }
            dataForApplication.setDisplayWithoutConfirmation(tmpNoDialog);
            dataForApplication.setSound(data.getSound());
            dataForApplication.setBadge(tmpBadge);
        } else {
            Log.e(Util.TAG, "PushNotificationData is null in convertPushDataToPushDataForApp");
            return;
        }
    }




    /**
     * API for getting action code associated with a push message from StreetHawk service
     * @return action code
     */
    public int getAction() {
        return mAction;
    }

    /**
     * API for getting message ID associated with a push message from StreetHawk service
     * @return msgID
     */
    public String getMsgId() {
        return mMsgID;
    }

    /**
     * API to get title associated with a push message from StreetHawk server
     * @return title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * API for get message associated with a push message from StreetHawk server
     * @return message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * API to get data associated with a push message from StreetHawk server
     * @return data
     *
     */
    public String getData() {
        return mData;
    }

    /**
     * API to get portion associated with a push message from StreetHawk server
     * Portion defines percentage of screen covered by the animated webview frame as displayed when action  = ACTION_OPEN_URL
     * 0 < portoin <=1
     * @return portion
     */
    public float getPortion() {
        return mPortion;
    }

    /**
     * API to get orientation associated with a push message from StreetHawk server
     * orientation defines direction from which web view will pop out of screen action  = ACTION_OPEN_URL
     * Valid values are 0 =bottom, 1 = top 2 = left 3 = right
     * @return orientation
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * API to get speed associated with a push message from StreetHawk server
     * Speed defines time takes for completing animation of webview for action  = ACTION_OPEN_URL
     * @return speed
     */
    public int getSpeed() {
        return mSpeed;
    }

    /**
     * API to get boolea value for displaying confirmation dialog when action = ACTION_OPEN_URL
     * if set to true, user will not be prompted for confirmation dialog and in app webview will pop up directly
     * @return true to display confirmation doalog else return false
     */
    public Boolean getDisplayWithoutConfirmation() {
        return mDisplayWithoutDialog;
    }

    /**
     * API returns name of sound file associated with push message
     * @return name of sound file to be played for notification
     */
    public String getSound() {
        return mSound;
    }

    /**
     * API returns number to be displayed as batch when push message arrives.
     * Not all android devices can display badge.
     * @return badge number
     */
    public int getBadge() {
        return mBadge;
    }

    /**
     * API to get value of content available
     * @return
     */
    public String getContentAvailable(){return mContentAvailable;}



    /**
     * get custom title for button 1
     * @return title for button 1
     */
    public String getBtn1Title(){return this.mBtn1Title;}

    /**
     * get title for button 2
     * @return title for button 2
     */
    public String getBtn2Title(){return this.mBtn2Title;}

    /**
     * get title for button 3
     * @return title for button 3
     */
    public String getBtn3Title(){return this.mBtn3Title;}

    /**
     * get icon for button 1
     * @return icon name for button 1
     */
    public String getBtn1Icon(){return this.mIC1;}

    /**
     * get icon for button 2
     * @return icon for button 2
     */
    public String getBtn2Icon(){return this.mIC2;}

    /**
     * get icon for button 3
     * @return icon for button 3
     */
    public String getBtn3Icon(){return this.mIC3;}



    /**
     * API for setting action code
     * @param action
     */
    public void setAction(int action) {
        mAction = action;
    }

    /**
     * API for setting msgID
     * @param msgID
     */
    public void setMsgId(String msgID) {
        mMsgID = msgID;
    }

    /**
     * API for setting title
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * API for setting message
     * @param message
     */
    public void setMessage(String message) {
        mMessage = message;
    }

    /**
     * API for setting data
     * @param data
     */
    public void setData(String data) {
        mData = data;
    }

    /**
     * API for setting portion for in app url display
     * @param portion
     */
    public void setPortion(float portion) {
        mPortion = portion;
    }

    /**
     * API for setting orientation in app url display
     * @param orientation
     */
    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    /**
     * API for setting speed for in app url display
     * @param speed
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * API for setting no dialog for in app url display
     * @param displayWithoutDialog
     */
    public void setDisplayWithoutConfirmation(Boolean displayWithoutDialog) {
        mDisplayWithoutDialog = displayWithoutDialog;
    }

    /**
     * API for setting sound
     * @param sound
     */
    public void setSound(String sound) {
        mSound = sound;
    }

    /**
     * API for setting badge
     * @param badge
     */
    public void setBadge(int badge) {
        mBadge = badge;
    }

    /**
     * API for setting content available for payload
     * @param contentAvailable
     */
    public void setContentAvailable(String contentAvailable){this.mContentAvailable=contentAvailable;}


    /**
     * Set title for notification button 1
     * @param title title for notification button 1
     */
    public void setBtn1Title(String title){this.mBtn1Title = title;}

    /**
     * Set title for notification button 2
     * @param title title for notification button 2
     */
    public void setBtn2Title(String title){this.mBtn2Title = title;}

    /**
     * Set title for notification button 3
     * @param title title for notification button 3
     */
    public void setBtn3Title(String title){this.mBtn3Title = title;}

    /**
     * Set icon title for button 1
     * @param icon icon for button 1
     */
    public void setBtn1Icon(String icon){this.mIC1 = icon;}

    /**
     * Set icon title for button 2
     * @param icon for button 2
     */
    public void setBtn2Icon(String icon){this.mIC2 = icon;}

    /**
     * Set title for button 3
     * @param icon for button 3
     */
    public void setBtn3Icon(String icon){this.mIC3 = icon;}







    /**
     * Display push data received from applicaton.
     * @param LogTag
     */
    public void displayDataForDebugging(String LogTag) {
        String NEWLINE = "\n";
        if (null == LogTag)
            LogTag = Util.TAG;
        String myData = "displayMyData" + NEWLINE +
                "MsgId " + this.mMsgID + NEWLINE +
                "Action " + mAction + NEWLINE +
                "Title "+ mTitle + NEWLINE +
                "Msg " + this.mMessage + NEWLINE +
                "Data " + mData + NEWLINE +
                "Portion "+  mPortion + NEWLINE +
                "Orientation "+ mOrientation + NEWLINE +
                "Speed " + mSpeed + NEWLINE +
                "NoDialog " + this.mDisplayWithoutDialog + NEWLINE +
                "Sound " + mSound + NEWLINE +
                "Badge " + mBadge + NEWLINE +

                /*Start interactive push*/
                "Btn1Title" + mBtn1Title + NEWLINE +
                "Btn2Title" + mBtn2Title + NEWLINE +
                "Btn3Title" + mBtn3Title + NEWLINE +
                "Icon_Btn1" + mIC1 + NEWLINE +
                "Icon_Btn2" + mIC2 + NEWLINE +
                "Icon_Btn3" + mIC3
                /*End interactive push*/
                ;
        Log.i(LogTag, myData);
    }

    /**
     * Notify push result to StreetHawk SDK. Use this API if you are not using StreetHawks on click listeners
     * @param context
     * @param result
     */
    public void sendPushResult(Context context, int result) {
        if (this.mMsgID == null) {
            Log.e(Util.TAG,SUBTAG+ "Invalid msgId" + mMsgID);
            return;
        } else {
            if ((result != RESULT_ACCEPTED) && (result != RESULT_DECLINED) && (result != RESULT_POSTPONED)) {
                Log.e(Util.TAG,SUBTAG+ "Invalid result code" + result + "valid values are RESULT_ACCEPTED or RESULT_DECLINED or RESULT_POSTPONED");
                return;
            }
            sendResultBroadcast(context,this.getMsgId(),result);
        }
    }

    private PushNotificationData copyToPushNotificationDataObject() {
        PushNotificationData object = new PushNotificationData();
        String tmpPortion = Float.toString(-1.0f);
        String tmpOrientation = Integer.toString(-1);
        String tmpSpeed = Integer.toString(-1);
        String tmpBadge = Integer.toString(0);
        String tmpDisplayWithoutDialog = Boolean.toString(false);
        try {
            tmpOrientation = Integer.toString(mOrientation);
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }
        try {
            tmpSpeed = Integer.toString(mSpeed);
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }
        try {
            tmpPortion = Float.toString(mPortion);
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }
        try {
            tmpBadge = Integer.toString(mBadge);
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }
        try {
            tmpDisplayWithoutDialog = Boolean.toString(mDisplayWithoutDialog);
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
        }

        switch (mAction) {
            case ACTION_OPEN_URL:
                object.setCode(Integer.toString(CODE_OPEN_URL));
                break;
            case ACTION_LAUNCH_ACTIVITY:
                object.setCode(Integer.toString(CODE_LAUNCH_ACTIVITY));
                break;
            case ACTION_RATE_APP:
                object.setCode(Integer.toString(CODE_RATE_APP));
                break;
            case ACTION_USER_REGISTRATION_SCREEN:
                object.setCode(Integer.toString(CODE_USER_REGISTRATION_SCREEN));
                break;
            case ACTION_USER_LOGIN_SCREEN:
                object.setCode(Integer.toString(CODE_USER_LOGIN_SCREEN));
                break;
            case ACTION_UPDATE_APP:
                object.setCode(Integer.toString(CODE_UPDATE_APP));
                break;
            case ACTION_CALL_TELEPHONE_NUMBER:
                object.setCode(Integer.toString(CODE_CALL_TELEPHONE_NUMBER));
                break;
            case ACTION_SIMPLE_PROMPT:
                object.setCode(Integer.toString(CODE_SIMPLE_PROMPT));
                break;
            case ACTION_FEEDBACK:
                object.setCode(Integer.toString(CODE_FEEDBACK));
                break;
            case ACTION_ENABLE_BLUETOOTH:
                object.setCode(Integer.toString(CODE_IBEACON));
                break;
            case ACTION_ENABLE_PUSH_MSG:
                object.setCode(Integer.toString(CODE_ACCEPT_PUSHMSG));
                break;
            case ACTION_ENABLE_LOCATION:
                object.setCode(Integer.toString(CODE_ENABLE_LOCATION));
                break;
            default:
                object.setCode(Integer.toString(0));
        }
        object.setMsgId(mMsgID);
        object.setTitle(mTitle);
        object.setMsg(mMessage);
        object.setData(mData);
        object.setPortion(tmpPortion);
        object.setOrientation(tmpOrientation);
        object.setSpeed(tmpSpeed);
        object.setBadge(tmpBadge);
        object.setNoDialog(tmpDisplayWithoutDialog);
        object.setSound(mSound);
        return object;
    }


    /**
     * Handles onclick event on Positive button. If you are using custom dialog,associate this positive listener with your custom dialogs positive button
     * @param dialog custom dialog instance
     * @param context application context
     * @return positive onClicklistener
     */
    public View.OnClickListener getPositiveButtonOnClickListener(Dialog dialog,Context context) {
        SHForegroundNotification shForegroundNotification = new SHForegroundNotification(context);
        return shForegroundNotification.getPositiveButtonListenerForApplication(dialog,copyToPushNotificationDataObject());
    }

    /**
     * Handles onclick event on neutral button. If you are using custom dialog,associate this positive listener with your custom dialogs neutral button
     * @param dialog custom dialog instance
     * @param context application context
     * @return neutral onClicklistener
     */
    public View.OnClickListener getNeutralButtonOnClickListener(Dialog dialog,Context context) {
        SHForegroundNotification shForegroundNotification = new SHForegroundNotification(context);
        return shForegroundNotification.getNeutralButtonListenerForApplication(dialog,copyToPushNotificationDataObject());

    }

    /**
     * Handles onclick event on negative button. If you are using custom dialog,associate this positive listener with your custom dialogs negative button
     * @param dialog custom dialog instance
     * @param context application context
     * @return negative onclick listener
     */
    public View.OnClickListener getNegativeButtonOnClickListener(Dialog dialog,Context context) {
        SHForegroundNotification shForegroundNotification = new SHForegroundNotification(context);
        return shForegroundNotification.getNegativeButtonListenerForApplication(dialog,copyToPushNotificationDataObject());

    }

}

