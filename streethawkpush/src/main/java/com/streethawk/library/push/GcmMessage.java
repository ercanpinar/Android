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

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

class GcmMessage extends NotificationBase implements Constants{
    public GcmMessage() {}
    public PushDataForApplication getPushMessageDataForApplication(Context context,Bundle extras){
        PushDataForApplication dataForApplication = new PushDataForApplication();
        PushNotificationData tempData = new PushNotificationData();
        getPushMessageData(context,tempData,extras);
        if(null!=tempData){
            dataForApplication.setTitle(tempData.getTitle());
            dataForApplication.setMessage(tempData.getMsg());
            dataForApplication.setData(tempData.getData());
            dataForApplication.setMsgId(tempData.getMsgId());
            float tmpPortion = -1.0f;
            int tmpOrientation = -1;
            int tmpSpeed = -1;
            int tmpBadge = 0;
            int tmpCode = 0;
            Boolean tmpNoDialog = false;
            try{
                tmpPortion= Float.parseFloat(tempData.getPortion());
            }catch(NumberFormatException e){
                tmpPortion = -1.0f;
            }
            catch(NullPointerException e){
                tmpPortion = -1.0f;
            }
            try{
                tmpOrientation= Integer.parseInt(tempData.getOrientation());
            }catch(NumberFormatException e){
                tmpOrientation = -1;
            }catch(NullPointerException e){
                tmpOrientation = -1;
            }
            try{
                tmpSpeed= Integer.parseInt(tempData.getSpeed());
            }catch(NumberFormatException e){
                tmpSpeed = -1;
            }catch(NullPointerException e){
                tmpSpeed = -1;
            }
            try{
                tmpBadge= Integer.parseInt(tempData.getBadge());
            }catch(NumberFormatException e){
                tmpBadge = -0;
            }catch(NullPointerException e){
                tmpBadge = -0;
            }
            try{
                tmpCode = Integer.parseInt(tempData.getCode());
            }catch(NumberFormatException e){
                tmpCode = 0;
            }catch(NullPointerException e){
                tmpCode = 0;
            }

            switch(tmpCode){
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
                default:
                    dataForApplication.setAction(0);
            }
            dataForApplication.setPortion(tmpPortion);
            dataForApplication.setOrientation(tmpSpeed);
            dataForApplication.setOrientation(tmpOrientation);
            String tmpNoDialogStr = tempData.getNoDialog();
            if(tmpNoDialogStr!=null){
                if(!tmpNoDialogStr.isEmpty())
                    tmpNoDialog = true;
            }
            dataForApplication.setDisplayWithoutConfirmation(tmpNoDialog);
            dataForApplication.setSound(tempData.getSound());
            dataForApplication.setBadge(tmpBadge);
            return dataForApplication;
        }
        return null;
    }

    /**
     * Get push data stored in database
     * @param context
     * @param pushData
     * @param bundle
     */
    private void getPushMessageData(Context context,PushNotificationData pushData,Bundle bundle){
        String code=null;
        String msgId=null;
        String from=null ;
        String sound=null;
        String data=null ;
        String badge=null;
        String alert=null;
        String title=null;
        String msg=null;
        String NoConfirm  = "false";
        String portion=null;
        String orientation=null;
        String speed=null;
        String installID = null;
        String contentAvailable = null;
        String category = null;


        /* Start custom button */

        String mBtnJSON;

        String mB1Title ="";
        String mB1Icon ="";

        String mB2Title ="";
        String mB2Icon ="";

        String mB3Title ="";
        String mB3Icon ="";

        /* End custom button*/

        //Aps mAps = null;
        String appString;
        int lengthTitle = 0;
        // boolean isShowConfirm = true;
        installID = bundle.getString(PUSH_INSTALLID);
        String storedInstallId = Util.getInstallId(context);
        if (installID == null) {
            return;
        }
        if (null == storedInstallId) {
            return;
        }
        if (!(installID.equals(storedInstallId))) {
            return;
        }
        code = bundle.getString(PUSH_CODE);
        msgId = bundle.getString(PUSH_MSG_ID);
        data = bundle.getString(PUSH_DATA);



        String tempTitlelength = bundle.getString(PUSH_TITLE_LENGTH);
        if (null != tempTitlelength) {
            try {
                lengthTitle = Integer.parseInt(tempTitlelength);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                NoConfirm = Boolean.toString(false);
                title = null;
                msg = null;
            }
        }
        portion = bundle.getString(PUSH_PORTION);
        orientation = bundle.getString(PUSH_ORIENTATION);
        speed = bundle.getString(PUSH_SPEED);
        String tempConfirm = bundle.getString(PUSH_SHOW_DIALOG);
        if (null != tempConfirm) {
            if(tempConfirm.isEmpty())
                NoConfirm = "false";
            else
                NoConfirm = "true";
        }else{
            NoConfirm = "false";
        }
        appString = bundle.getString(PUSH_APS);
        if (!TextUtils.isEmpty(appString)) {
            //mAps = new Aps();
            try {
                JSONObject apsJson = new JSONObject(appString);
                try {
                    alert = apsJson.getString(PUSH_ALERT);
                    if (null != alert) {
                        int tmpLength = lengthTitle;
                        title = alert.substring(0, Math.min(alert.length(), lengthTitle));
                        if (lengthTitle != 0 && lengthTitle != alert.length()) {
                            tmpLength += 1; //removing space between title and message
                        }
                        msg = alert.substring(tmpLength, alert.length());
                        if (title != null) {
                            if (title.isEmpty())
                                title = null;
                        }
                        if (msg != null) {
                            if (msg.isEmpty())
                                msg = null;
                        }
                    }
                } catch (JSONException e) {
                    alert = null;
                    title = null;
                    msg = null;
                }
                try {
                    badge = apsJson.getString(PUSH_BADGE);
                } catch (JSONException e) {
                    badge ="-1";
                }

                /*Start interactive push*/

                try {
                    contentAvailable = apsJson.getString(PUSH_CONTENT_AVAILABLE);
                } catch (JSONException e) {
                    contentAvailable = null;
                }

                try {

                    category = apsJson.getString(PUSH_CATEGORY);
                } catch (JSONException e) {
                    category = null;
                }

                /*End interactive push*/


                try {
                    sound = apsJson.getString(PUSH_SOUND);
                } catch (JSONException e) {
                    sound = "DEFAULT";
                }
            } catch (JSONException e) {
            }
        }

        /*Start custom button*/
        if(null==title){
            title = bundle.getString(PUSH_TITLE);
        }
        if(null==msg){
            msg=bundle.getString(PUSH_MSG);
        }

        mBtnJSON = bundle.getString(PUSH_BUTTON1);
        if(null!=mBtnJSON){
            if (!TextUtils.isEmpty(mBtnJSON)) {
                try {
                    JSONObject apsJson = new JSONObject(mBtnJSON);
                    mB1Title = apsJson.getString(PUSH_BTN_TITLE);
                    mB1Icon = apsJson.getString(PUSH_BTN_ICON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mBtnJSON = bundle.getString(PUSH_BUTTON2);
        if(null!=mBtnJSON){
            if (!TextUtils.isEmpty(mBtnJSON)) {
                try {
                    JSONObject apsJson = new JSONObject(mBtnJSON);
                    mB2Title = apsJson.getString(PUSH_BTN_TITLE);
                    mB2Icon = apsJson.getString(PUSH_BTN_ICON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mBtnJSON = bundle.getString(PUSH_BUTTON1);
        if(null!=mBtnJSON){
            if (!TextUtils.isEmpty(mBtnJSON)) {
                try {
                    JSONObject apsJson = new JSONObject(mBtnJSON);
                    mB3Title = apsJson.getString(PUSH_BTN_TITLE);
                    mB3Icon = apsJson.getString(PUSH_BTN_ICON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /*End custom button*/

        String NEWLINE = "\n";
        pushData.setMsgId(msgId);
        pushData.setCode(code);
        pushData.setTitle(title);
        pushData.setMsg(msg);
        pushData.setData(data);
        pushData.setPortion(portion);
        pushData.setOrientation(orientation);
        pushData.setSpeed(speed);
        pushData.setNoDialog(NoConfirm);
        pushData.setSound(sound);
        pushData.setBadge(badge);
        pushData.setContentAvailable(contentAvailable);
        pushData.setCategory(category);

        if(!mB1Title.isEmpty()){
            pushData.setBtn2Title(mB2Title);
        }
        if(!mB2Title.isEmpty()){
            pushData.setBtn2Title(mB2Title);
        }
        if(!mB3Title.isEmpty()){
            pushData.setBtn3Title(mB3Title);
        }

        if(!mB1Icon.isEmpty()){
            pushData.setBtn1Icon(getIcon(context,mB1Icon));
        }
        if(!mB2Icon.isEmpty()){
            pushData.setBtn2Icon(getIcon(context,mB2Icon));
        }
        if(!mB3Icon.isEmpty()){
            pushData.setBtn3Icon(getIcon(context,mB3Icon));
        }

        String logMsg = "code " + code + NEWLINE +
                "TitleLength" + lengthTitle + NEWLINE +
                "Title " + title + NEWLINE +
                "Msg " + msg + NEWLINE +
                "Data " + data + NEWLINE +
                "Portion " + portion + NEWLINE +
                "Orientation " + orientation + NEWLINE +
                "Speed " + speed + NEWLINE +
                "InstallId " + installID + NEWLINE +
                "N " + NoConfirm + NEWLINE +
                "b1Title "+ mB1Title + NEWLINE +
                "b1Icon " + mB1Icon + NEWLINE +
                "b2Title "+ mB2Title + NEWLINE +
                "b2Icon " + mB2Icon + NEWLINE +
                "b3Title "+ mB3Title + NEWLINE +
                "b3Icon " + mB3Icon + NEWLINE +
                "content-available "+contentAvailable + NEWLINE +
                "category" + category
                ;
    }

    /**
     * get icon resid from given icon name
     * @param context
     * @param iconName
     * @return
     */
    private int getIcon(Context context,String iconName){
        String packageName = context.getPackageName();
        return (context.getResources().getIdentifier(iconName, "drawable", packageName));
    }

    /**
     * Store push message in data base
     * @param context
     * @param bundle
     * @return
     */
    public boolean storePushMessageData(Context context, Bundle bundle) {
        PushNotificationDB storePushData = PushNotificationDB.getInstance(context);
        storePushData.open();
        PushNotificationData pushData = new PushNotificationData();
        getPushMessageData(context, pushData, bundle);
        storePushData.storeGcmMessageDatabase(pushData);
        storePushData.close();
        return true;
    }
}