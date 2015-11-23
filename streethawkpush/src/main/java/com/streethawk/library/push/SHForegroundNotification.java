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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class SHForegroundNotification extends NotificationBase {


    private final String SUBTAG = "SHForegroundNotification ";
    private Context mContext;
    //private PushNotificationData mPushData;
    //private static Bundle mExtras;
    private static ISHObserver mSHObserver = null;
    private boolean isDialogHandled = false;
    private static volatile Dialog mForegroundDialog = null;
    private static SHForegroundNotification mInstance = null;
    private static Dialog MyDialog = null;

    private final String STREETHAWK_ERROR_NO_ISHOBSERVER = "Application has not registered ISHObserver. Please refer streethawk docs";

    /**
     * API to dismiss Streethawk dialog dialog
     */
    public void dismissForegroundDialog() {
        if (null != mForegroundDialog) {
            if (mForegroundDialog.isShowing()) {
                mForegroundDialog.dismiss();
            }
            mForegroundDialog = null;
        }
        if (MyDialog == null)
            return;
        if (MyDialog.isShowing())
            MyDialog.dismiss();
    }


    public static void dismissCurrentShowingDialog(){
        if(MyDialog==null)
            return;
        if(MyDialog.isShowing())
            MyDialog.dismiss();
    }

    /**
     * Pass receiver object which is required for cross platforms
     *
     * @param receiverObject
     */
    public void setAppPageReceiver(ISHObserver receiverObject) {
        mSHObserver = receiverObject;
    }

    /**
     * @param context
     * @return
     */
    public static SHForegroundNotification getDialogInstance(Context context) {
        if (null == mInstance) {
            SHForegroundNotification Instance = new SHForegroundNotification(context);
            return Instance;
        }
        return mInstance;
    }

    public SHForegroundNotification(Context context) {
        this.mContext = context;
    }

    private void hideSoftKeyboard() {
        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        if (activity == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow((IBinder) activity.getWindow().getDecorView().getWindowToken(), 0);
    }


    private void displayInAppSlide(PushNotificationData pushData) {
        StreetHawkDialog instance = StreetHawkDialog.getStreetHawkDialogInstance();
        instance.setParams(pushData);
        instance.show();
    }


    /**
     * Return positive button onClickListener
     *
     * @param pushData
     * @return
     */
    public View.OnClickListener getPositiveButtonListenerForApplication(final Dialog dialog, PushNotificationData pushData) {
        return getPositiveOnClickListenerForApp(dialog, pushData);

    }

    /**
     * Return negative button onClickListener
     *
     * @param pushData
     * @return
     */
    public View.OnClickListener getNegativeButtonListenerForApplication(final Dialog dialog, PushNotificationData pushData) {
        return getNegativeOnClickListenerForApp(dialog, pushData.getMsgId());

    }

    /**
     * Return neutral button onClickListener
     *
     * @param pushData
     * @return
     */
    public View.OnClickListener getNeutralButtonListenerForApplication(final Dialog dialog, PushNotificationData pushData) {
        return getNeutralOnClickListenerForApp(dialog, pushData.getMsgId());

    }


    /**
     * Call display to show alert dialog box
     */
    public void display(final PushNotificationData pushData) {
        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        if (null == activity) {
            Log.e(Util.TAG, SUBTAG + "activity is null in  display()");
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            return;
        }
        final Activity tmpActivity = activity;
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Context is null in display()");
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            return;
        }
        String titleStr1 = pushData.getTitle();
        titleStr1 = getUnicodeForEmoji(titleStr1, true);
        String msgStr1 = pushData.getMsg();
        msgStr1 = getUnicodeForEmoji(msgStr1, true);

        final String titleStr = titleStr1;
        final String msgStr = msgStr1;


        Spanned titleTmp = null;
        Spanned msgTmp = null;
        if (titleStr != null) {
            if (!titleStr.isEmpty())
                titleTmp = Html.fromHtml(titleStr/*+'\u00A9'+0x1F601*/);
        }
        if (msgStr != null) {
            if (!msgStr.isEmpty())
                msgTmp = Html.fromHtml(msgStr);
        }
        final Spanned title = titleTmp;
        final Spanned msg = msgTmp;
        final String data = pushData.getData();
        final int code = Integer.parseInt(pushData.getCode());
        final boolean noDialog = Boolean.parseBoolean(pushData.getNoDialog());
        if (null == title && null == msg) {
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            Log.w(Util.TAG, SUBTAG + "Ignoring message as title and message are null");
            return;
        }
        hideSoftKeyboard();
        if (code == CODE_OPEN_URL && noDialog) {
            Float portion;
            int orientation;
            int speed;
            if (null == data) {
                PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
                return;
            }
            if (data.isEmpty()) {
                PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
                return;
            }

            try {
                portion = Float.parseFloat(pushData.getPortion());
            } catch (Exception e) {
                portion = -1.0f;
            }
            try {
                orientation = Integer.parseInt(pushData.getOrientation());
            } catch (Exception e) {
                orientation = -1;
            }
            try {
                speed = (int) Float.parseFloat(pushData.getSpeed());
            } catch (Exception e) {
                speed = -1;
            }
            if ((0 < portion) || (0 < orientation) || (0 < speed)) {
                displayInAppSlide(pushData);
            } else {
                pushData.setOrientation("0");
                displayInAppSlide(pushData);
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(tmpActivity);
                    if (null != title) {
                        builder.setTitle(title);
                    }
                    if (null != msg) {
                        builder.setMessage(msg);
                    }
                    String positiveButtonTitle = getPositiveButtonTitle(mContext, code);
                    String NegativeButtonTitle = getNegativeButtonTitle(mContext, code);
                    // Keeping switch case instead of if to handle non supported codes
                    switch (code) {
                        case CODE_OPEN_URL:
                        case CODE_LAUNCH_ACTIVITY:
                        case CODE_CALL_TELEPHONE_NUMBER:
                        case CODE_IBEACON:
                        case CODE_ENABLE_LOCATION:
                        case CODE_UPDATE_APP:
                        case CODE_USER_REGISTRATION_SCREEN:
                        case CODE_USER_LOGIN_SCREEN:
                            builder.setPositiveButton(positiveButtonTitle, getPositiveOnClickListener(pushData));
                            builder.setNegativeButton(NegativeButtonTitle, getNegativeOnClickListener(pushData.getMsgId()));
                            break;
                        case CODE_SIMPLE_PROMPT:
                            builder.setPositiveButton(getStringtoDisplay(mContext, TYPE_SIMPLE_PUSH_POSITIVE), getPositiveOnClickListener(pushData));
                            break;
                        case CODE_RATE_APP:
                            builder.setPositiveButton(positiveButtonTitle, getPositiveOnClickListener(pushData));
                            builder.setNegativeButton(NegativeButtonTitle, getNeutralOnClickListener(pushData.getMsgId()));
                            break;
                        case CODE_FEEDBACK:
                            // Check for optionList. if present display list with one button else display dialog box to show custom feedback
                            JSONObject feedbackObject;
                            if (null == data || data.isEmpty()) {
                                builder.setPositiveButton(positiveButtonTitle, getPositiveOnClickListener(pushData));
                                builder.setNegativeButton(NegativeButtonTitle, getNegativeOnClickListener(pushData.getMsgId()));
                            } else {
                                try {
                                    feedbackObject = new JSONObject(data);
                                    JSONArray array = null;
                                    array = feedbackObject.getJSONArray(FEEDBACK_LIST_CONTENT);
                                    if (null == array || 0 == array.length()) {
                                        builder.setPositiveButton(positiveButtonTitle, getPositiveOnClickListener(pushData));
                                        builder.setNegativeButton(NegativeButtonTitle, getNegativeOnClickListener(pushData.getMsgId()));
                                    } else {
                                        isDialogHandled = true;
                                        feedback_preDefinedOptions(pushData.getMsgId(), titleStr, msgStr, data);
                                    }
                                } catch (JSONException je) {
                                    builder.setPositiveButton(positiveButtonTitle, getPositiveOnClickListener(pushData));
                                    builder.setNegativeButton(NegativeButtonTitle, getNegativeOnClickListener(pushData.getMsgId()));
                                }
                            }
                            break;
                        default:
                            Log.e(Util.TAG, SUBTAG + "Received Unhandled code " + code);
                            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
                            return;
                    }
                    builder.setCancelable(false); // Adding as dismiss is causing side effects
                    if (null == mForegroundDialog) {
                        mForegroundDialog = new Dialog(mContext);
                    }
                    mForegroundDialog = builder.create();
                    //storeShowDialogStatus(true);
                    if (!isDialogHandled) {
                        mForegroundDialog.show();
                    }
                    isDialogHandled = false;
                }
            });
        }
    }

    /**
     * negative button listener when actions are handled by custom dialog in application.
     *
     * @param dialog
     * @param msgId
     * @return
     */
    private View.OnClickListener getNegativeOnClickListenerForApp(final Dialog dialog, final String msgId) {
        View.OnClickListener negativeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendResultBroadcast(mContext, msgId, STREETHAWK_DECLINED);
            }
        };
        return negativeClickListener;
    }

    /**
     * neutral button onclick listener when actions are handled by custom dialog in application
     *
     * @param dialog
     * @param msgId
     * @return
     */
    private View.OnClickListener getNeutralOnClickListenerForApp(final Dialog dialog, final String msgId) {
        View.OnClickListener neutralClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendResultBroadcast(mContext, msgId, STREETHAWK_POSTPONED);
            }
        };
        return neutralClickListener;
    }

    /**
     * Postivive button listener when streethawk action is handled by custom dialog in application
     *
     * @param dialog
     * @param mPushData
     * @return
     */
    private View.OnClickListener getPositiveOnClickListenerForApp(final Dialog dialog, final PushNotificationData mPushData) {
        View.OnClickListener positiveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                int code = Integer.parseInt(mPushData.getCode());
                if (code != CODE_FEEDBACK) {
                    sendResultBroadcast(mContext, mPushData.getMsgId(), STREETHAWK_ACCEPTED);
                }
                switch (code) {
                    case CODE_OPEN_URL:
                        handleURL(mPushData);
                        break;
                    case CODE_LAUNCH_ACTIVITY:
                    case CODE_USER_REGISTRATION_SCREEN:
                    case CODE_USER_LOGIN_SCREEN:
                        String data = mPushData.getData();
                        if (data.isEmpty()) {
                            if (code == CODE_USER_REGISTRATION_SCREEN)
                                data = REGISTER_FRIENDLY_NAME;
                            if (code == CODE_USER_LOGIN_SCREEN)
                                data = LOGIN_FRIENDLY_NAME;
                        }
                        handleLaunchActivity(mPushData.getMsgId(), data);
                        break;
                    case CODE_CALL_TELEPHONE_NUMBER:
                        handleCall(mPushData.getData());
                        break;
                    case CODE_FEEDBACK:
                        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
                        if (null == activity)
                            break;
                        else {
                            showConversationalFeedback(mPushData.getMsgId());
                        }
                        break;
                    case CODE_SIMPLE_PROMPT:
                        PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                        obj.clearPendingDialogFlagAndDB(mContext, mPushData.getMsgId());
                        break;
                    case CODE_RATE_APP:
                    case CODE_UPDATE_APP:
                        handleRateUpdateApp();
                        break;
                    case CODE_IBEACON:
                        startBluetooth();
                        break;
                    case CODE_ENABLE_LOCATION:
                        Intent locintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        locintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(locintent);
                        break;
                    default:
                        // Wrong code so dismiss dialog
                        sendResultBroadcast(mContext, mPushData.getMsgId(), STREETHAWK_DECLINED);
                        break;
                }

            }
        };
        return positiveClickListener;
    }


    /**
     * Positive button listener when action is handled by default dialog by StreetHawk
     *
     * @param mPushData
     * @return
     */
    private DialogInterface.OnClickListener getPositiveOnClickListener(final PushNotificationData mPushData) {
        DialogInterface.OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int code = Integer.parseInt(mPushData.getCode());
                if (code != CODE_FEEDBACK) {
                    sendResultBroadcast(mContext, mPushData.getMsgId(), STREETHAWK_ACCEPTED);
                }
                switch (code) {
                    case CODE_OPEN_URL:
                        handleURL(mPushData);
                        break;
                    case CODE_LAUNCH_ACTIVITY:
                    case CODE_USER_REGISTRATION_SCREEN:
                    case CODE_USER_LOGIN_SCREEN:
                        String data = mPushData.getData();
                        if (data.isEmpty()) {
                            if (code == CODE_USER_REGISTRATION_SCREEN)
                                data = REGISTER_FRIENDLY_NAME;
                            if (code == CODE_USER_LOGIN_SCREEN)
                                data = LOGIN_FRIENDLY_NAME;
                        }
                        handleLaunchActivity(mPushData.getMsgId(), data);
                        break;
                    case CODE_CALL_TELEPHONE_NUMBER:
                        handleCall(mPushData.getData());
                        break;
                    case CODE_FEEDBACK:
                        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
                        if (null == activity)
                            break;
                        else {
                            showConversationalFeedback(mPushData.getMsgId());
                        }
                        break;
                    case CODE_SIMPLE_PROMPT:
                        PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                        break;
                    case CODE_RATE_APP:
                    case CODE_UPDATE_APP:
                        handleRateUpdateApp();
                        break;
                    case CODE_IBEACON:
                        startBluetooth();
                        break;
                    case CODE_ENABLE_LOCATION:
                        Intent locIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        locIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(locIntent);
                        break;
                    default:
                        // Wrong code so dismiss dialog
                        sendResultBroadcast(mContext, mPushData.getMsgId(), STREETHAWK_DECLINED);
                        break;
                }

            }
        };
        return positiveClickListener;
    }


    //TODO : move this module in beacons

    /**
     * Enables bluetooth when user preess yes
     */
    private void startBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            boolean isEnabled = bluetoothAdapter.isEnabled();
            if (!isEnabled) {
                bluetoothAdapter.enable();
                Toast.makeText(mContext, getStringtoDisplay(mContext, TYPE_BT_ENABLE_TOAST), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Shows conversational feedback dialog
     */
    public void showConversationalFeedback(String msgId) {
        Bundle extras = new Bundle();
        extras.putString(Util.SHMESSAGE_ID, msgId);
        Intent intent = new Intent(mContext, SHFeedbackActivity.class);
        extras.putString(MSGID, msgId);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * negative button listener for actions handled by StreetHawk default dialog
     *
     * @param msgId
     * @return
     */
    private DialogInterface.OnClickListener getNegativeOnClickListener(final String msgId) {
        DialogInterface.OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResultBroadcast(mContext, msgId, STREETHAWK_DECLINED);
            }
        };
        return negativeClickListener;
    }

    /**
     * neutral button listener for actions handled by StreetHawk default dialog.
     *
     * @param msgId
     * @return
     */
    private DialogInterface.OnClickListener getNeutralOnClickListener(final String msgId) {
        DialogInterface.OnClickListener neutralClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResultBroadcast(mContext, msgId, STREETHAWK_POSTPONED);
            }
        };
        return neutralClickListener;
    }

    /**
     * Opens google play to rate or update the app
     */

    private void handleRateUpdateApp() {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName()));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(marketIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(marketIntent);
        }
    }

    /**
     * Handle url for FG
     */
    private void handleURL(PushNotificationData pushData) {
        Float portion;
        int orientation;
        int speed;
        String data = pushData.getData();
        if (null == data) {
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            return;
        }
        if (data.isEmpty()) {
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            return;
        }
        if (!data.startsWith("http://") && !data.startsWith("https://")) {
            data = "http://" + data;
        }
        try {
            portion = Float.parseFloat(pushData.getPortion());
        } catch (Exception e) {
            portion = -1.0f;
        }
        try {
            orientation = Integer.parseInt(pushData.getOrientation());
        } catch (Exception e) {
            orientation = -1;
        }
        try {
            speed = (int) Float.parseFloat(pushData.getSpeed());
        } catch (Exception e) {
            speed = -1;
        }
        if ((0 < portion) || (0 <= orientation) || (0 <= speed)) {
            displayInAppSlide(pushData);
        } else {
            Intent nativeBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            nativeBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                mContext.startActivity(nativeBrowserIntent);
            } catch (Exception e) {
                Log.e(Util.TAG, "Exception in handleURL" + e);
            }
        }
    }

    /**
     * Handle launching of Activity
     * handling deep linking
     *
     * @param data
     */
    private void handleLaunchActivity(String msgId, String data) {
        if (null == data)
            data = mContext.getApplicationContext().getPackageName();
        final SharedPreferences activityPrefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_FRNDLST, Context.MODE_PRIVATE);
        String tempActivityName = activityPrefs.getString(data, null);
        if (null == tempActivityName) {
            // Check for deeplink url
            if (data.contains("://")) {
                try {
                    Intent deepLinkIntent = new Intent();
                    deepLinkIntent.setAction("android.intent.action.VIEW");
                    deepLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    deepLinkIntent.setData(Uri.parse(data));
                    mContext.startActivity(deepLinkIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e(Util.TAG, SUBTAG + "Incorrect link" + data);
                }
            }
            // Either we have received FQName or ""
            tempActivityName = data;
        }
        final String activityName = tempActivityName;
        switch (Util.getPlatformType()) {
            case PLATFORM_ANDROID_NATIVE:
            case PLATFORM_XAMARIN:
            default:
                try {
                    final Class<?> classname = Class.forName(activityName);
                    //check if requested activity and current activities are same. If so ignore launching of new activity
                    ActivityManager am = (ActivityManager) StreetHawk.INSTANCE.getCurrentActivity().getSystemService(Activity.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                    if (taskInfo.get(0).topActivity.getClassName().equals(activityName)) {
                        return;
                    }
                    /*Clearing pending dialog so as to prevent showing of dialog when again when activity is launched*/
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(PENDING_DIALOG, null);
                    e.commit();
                    Intent intent = new Intent(mContext, classname);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.e(Util.TAG, "Invalid activity called" + activityName);
                }
                break;

            case PLATFORM_PHONEGAP:
            case PLATFORM_TITANIUM:
            case PLATFORM_UNITY:
                if (null == mSHObserver) {
                    Log.e(Util.TAG, STREETHAWK_ERROR_NO_ISHOBSERVER);
                    return;
                } else {
                    mSHObserver.shNotifyAppPage(activityName);
                }
                break;
        }
    }

    /**
     * Send logs reporting errorous condition
     *
     * @param comment
     */
    private void SendErrorLog(String comment) {
        try {
            Bundle params = new Bundle();
            params.putString(Util.CODE, Integer.toString(CODE_ERROR));
            params.putString(Util.TYPE_STRING, comment);
            Logging manager = Logging.getLoggingInstance(mContext);
            manager.addLogsForSending(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * calls number
     *
     * @param PhoneNumber
     */
    private void handleCall(String PhoneNumber) {
        Intent callIntentDirect = new Intent(Intent.ACTION_CALL);
        callIntentDirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntentDirect.setData(Uri.parse("tel:" + PhoneNumber));
        mContext.startActivity(callIntentDirect);
    }

    private String FEEDBACK_LIST_CONTENT = "c";
    private String FEEDBACK_FREE_TEXT = "i";
    private final int FEEDBACK_WITH_INPUT_TEXT = 1;
    private final int FEEDBACK_WITHOUT_INPUT_TEXT = 0;


    private void feedback_preDefinedOptions(final String msgId, final String title, final String msg, String data) {
        final Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        if (null == activity) {
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, msgId);
            return;
        }
        try {
            JSONObject feedbackObject = new JSONObject(data);
            JSONArray array = null;
            String showFreeText = null;
            try {
                array = feedbackObject.getJSONArray(FEEDBACK_LIST_CONTENT);
            } catch (JSONException je) {
            }
            try {
                showFreeText = feedbackObject.getString(FEEDBACK_FREE_TEXT);
            } catch (JSONException je) {
            }
            if (null == array) {
                showConversationalFeedback(msgId);
            } else {
                final String tempShowFreeText = showFreeText;
                final ArrayList<SHFeedbackListModel> listoptions = new ArrayList<SHFeedbackListModel>();
                for (int i = 0; i < array.length(); i++) {
                    final SHFeedbackListModel model = new SHFeedbackListModel();
                    model.setOption(array.getString(i));
                    listoptions.add(model);
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (null != title)
                    builder.setTitle(title);
                if (null != msg)
                    builder.setMessage(msg);
                String cancel = getStringtoDisplay(mContext, TYPE_FEEDBACK_NEGATIVE);
                builder.setNegativeButton(Html.fromHtml("<b><i>" + cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResultBroadcast(mContext, msgId, STREETHAWK_DECLINED);
                    }
                });
                final ListView list = new ListView(activity);
                list.setAdapter(new ShFeedbackAdapter(activity, listoptions));
                builder.setCancelable(false);
                builder.setView(list);
                if (null == MyDialog) {
                    MyDialog = new Dialog(mContext);
                }
                // MyDialog.dismiss();
                MyDialog = builder.create();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> Parent, View view,
                                            final int position, long id) {
                        MyDialog.dismiss();
                        try {
                            if (null != tempShowFreeText) {
                                switch (Integer.parseInt(tempShowFreeText)) {
                                    case FEEDBACK_WITH_INPUT_TEXT:
                                        Intent intent = new Intent(mContext, SHFeedbackActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Bundle extras = new Bundle();
                                        extras.putString(MSGID, msgId);
                                        intent.putExtras(extras);
                                        intent.putExtra("SHFeedbackActyTitle", listoptions.get(position).getOption().toString());
                                        activity.startActivity(intent);
                                        break;
                                    case FEEDBACK_WITHOUT_INPUT_TEXT:
                                        String tempTitle = "";
                                        if (null == title) {
                                            if (null != msg) {
                                                tempTitle = msg;
                                            }
                                        } else {
                                            tempTitle = title;
                                        }
                                        Logging.getLoggingInstance(mContext).sendFeedbackToServer(tempTitle, listoptions.get(position).getOption().toString(), 0);
                                        sendResultBroadcast(mContext, msgId, STREETHAWK_ACCEPTED);
                                        Toast.makeText(mContext, "Feedback submitted", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } else {
                                showConversationalFeedback(msgId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //storeShowDialogStatus(true);
                if (MyDialog.isShowing())
                    MyDialog.dismiss();
                MyDialog.show();
                isDialogHandled = true;
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

}