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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

public class SHFeedbackActivity extends Activity {

    private EditText mTitleEt;
    private EditText mFeedbackEt;
    private Button mSendButton;
    private Button mCancelButton;
    private String mTitleText=null;
    private String mContextText=null;
    private final int focus_title =0;
    private final int focus_content = focus_title+1;
    private Bundle mExtras;
    private String mMessageId = null;

    private int mCurrentFocus = 0;
    public SHFeedbackActivity(){};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtras = getIntent().getExtras();
        mTitleText = mExtras.getString("SHFeedbackActyTitle");
        mMessageId = mExtras.getString(Constants.MSGID);
        setContentView(getFeedbackView());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(Constants.PENDING_DIALOG,null);
        e.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            StreetHawk.INSTANCE.shActivityResumed(this);
        }
        if(mTitleText!=null)
            mTitleEt.setText(mTitleText);
        if(mContextText!=null)
            mFeedbackEt.setText(mContextText);

        switch(mCurrentFocus){
            case focus_title:
                mTitleEt.requestFocus();
                break;
            case focus_content:
                mFeedbackEt.requestFocus();
                break;
            default:
                mTitleEt.requestFocus();
                break;
        }
        PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
        obj.clearPendingDialogFlagAndDB(getApplicationContext(), this.mMessageId);
    }
    @Override
    public void onPause(){
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            StreetHawk.INSTANCE.shActivityPaused(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String title,content;
        title = mTitleEt.getText().toString();
        content = mFeedbackEt.getText().toString();
        savedInstanceState.putString("SHFeedbackActyTitle",title);
        savedInstanceState.putString("SHFeedbackActyContent",content);
        savedInstanceState.putString("SHMESSAGEID",mMessageId);

        if(mTitleEt.isFocused())
            savedInstanceState.putInt("SHFeedbackFocus",focus_title);
        if(mFeedbackEt.isFocused())
            savedInstanceState.putInt("SHFeedbackFocus",focus_content);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mTitleText = savedInstanceState.getString("SHFeedbackActyTitle");
        mContextText = savedInstanceState.getString("SHFeedbackActyContent");
        mCurrentFocus = savedInstanceState.getInt("SHFeedbackFocus");
        //mMessageId = savedInstanceState.getString("SHMESSAGEID",mMessageId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        sendPushResult(Constants.STREETHAWK_DECLINED);
        super.onBackPressed();
    }


    @SuppressLint("InlinedApi")
    private View getFeedbackView(){
        Context context = getApplicationContext();
        LinearLayout mMainLayout = new LinearLayout(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mMainLayout.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        mMainLayout.setOrientation(LinearLayout.VERTICAL);


		/*Add buttons for Feedback*/
        LinearLayout mButtonLinearLayout = new LinearLayout(getApplicationContext());
        mButtonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mButtonLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        mButtonLinearLayout.setBackgroundColor(0xffffffff);
        mButtonLinearLayout.setPadding(10,10,10,10);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        buttonLayoutParams.weight=1;
        mSendButton = new Button(context);
        mSendButton.setLayoutParams(buttonLayoutParams);
        mSendButton.setText(NotificationBase.getStringtoDisplay(context, NotificationBase.TYPE_FEEDBACK_SUBMIT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mSendButton.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,1.0f));
        mSendButton.setPadding(1, 1, 1, 1);
        mSendButton.setOnClickListener( saveButtonListener);

        mCancelButton = new Button(context);
        mCancelButton.setLayoutParams(buttonLayoutParams);
        mCancelButton.setText(NotificationBase.getStringtoDisplay(context, NotificationBase.TYPE_FEEDBACK_CANCEL));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mCancelButton.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,1.0f));
        mCancelButton.setPadding(1, 1, 1, 1);
        mCancelButton.setOnClickListener(cancelButtonListener);

        mButtonLinearLayout.addView(mCancelButton);
        mButtonLinearLayout.addView(mSendButton);

		/*Add title for feedback*/

        LinearLayout mTitleLinearLayout = new LinearLayout(getApplicationContext());
        mTitleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mTitleLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        mTitleLinearLayout.setBackgroundColor(0xffffffff);
        mTitleLinearLayout.setPadding(10,10,10,10);

        mTitleEt = new EditText(context);
        mTitleEt.setBackgroundColor(0xFFCCCCCC);
        mTitleEt.setTextColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mTitleEt.setLayoutParams(new LinearLayout.LayoutParams(0, ActionBar.LayoutParams.MATCH_PARENT,1.0F));
        mTitleEt.setHint(NotificationBase.getStringtoDisplay(context, NotificationBase.TYPE_FEEDBACK_HINT_TITLE));
        mTitleEt.setPadding(10, 20, 20, 20);
        mTitleLinearLayout.addView(mTitleEt);

		/*Add context to feedback */
        LinearLayout mContentLinearLayout = new LinearLayout(getApplicationContext());
        mContentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mContentLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        mContentLinearLayout.setBackgroundColor(0xffffffff);
        mContentLinearLayout.setPadding(10,10,10,10);
        mFeedbackEt = new EditText(context);
        mFeedbackEt.setBackgroundColor(0xFFCCCCCC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mFeedbackEt.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT,1.0F));
        mFeedbackEt.setTextColor(Color.BLACK);
        mFeedbackEt.setLines(100);
        mFeedbackEt.setGravity(Gravity.TOP);
        mFeedbackEt.setHint(NotificationBase.getStringtoDisplay(context, NotificationBase.TYPE_FEEDBACK_HINT_CONTENT));
        mFeedbackEt.setPadding(20, 20, 20, 20);
        mContentLinearLayout.addView(mFeedbackEt);
        mMainLayout.addView(mButtonLinearLayout);
        mMainLayout.addView(mTitleLinearLayout);
        mMainLayout.addView(mContentLinearLayout);
        return mMainLayout;
    }

    private void sendPushResult(int result){
        NotificationBase.sendResultBroadcast(getApplicationContext(), this.mMessageId, result);
    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title=null;
            String content=null;
            if(mTitleEt!=null){
                title = mTitleEt.getText().toString();
            }
            if(mTitleEt!=null){
                content = mFeedbackEt.getText().toString();
            }
            if(title.isEmpty() && content.isEmpty()){
                Toast.makeText(getApplicationContext(), NotificationBase.getStringtoDisplay(getApplicationContext(), NotificationBase.TYPE_FEEDBACK_TOAST_ERROR), Toast.LENGTH_LONG).show();
            }else{
                Logging.getLoggingInstance(getApplicationContext()).sendFeedbackToServer(title,content,0);
                sendPushResult(Constants.STREETHAWK_ACCEPTED);
                Toast.makeText(getApplicationContext(),NotificationBase.getStringtoDisplay(getApplicationContext(),NotificationBase.TYPE_FEEDBACK_TOAST_SUCCESS),Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendPushResult(Constants.STREETHAWK_DECLINED);
            finish();
        }
    };
}
