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
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streethawk.library.core.Util;

class SHFeedbackListModelView extends View {
    private Context context;
    private TextView textView;
    private LinearLayout linearLayout;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    // For listview, the below function setText is the actual function which fill in the data
    public void setText(String value){
        linearLayout.removeView(textView);
        textView = new TextView(context);
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int maxDimen=0;
        int padding_value=0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            maxDimen = display.getWidth() > display.getHeight()?display.getWidth():display.getHeight();
        } else {
            try {
                Point size = new Point();
                display.getRealSize(size);
                maxDimen = size.x >size.y?size.x:size.y;
            } catch (Exception e) {
                    Log.e(Util.TAG, "Exception in SHFeedbackListModelView" + e);
                maxDimen = 0;
            }
        }
        if(maxDimen<320){
            padding_value = 0;
        }else if(maxDimen>=320 && maxDimen<480){
            padding_value = 5;
        }else{
            padding_value = 10;
        }
        float density = context.getResources().getDisplayMetrics().density;
        int padding = (int)(padding_value*density);
        textView.setPadding(0,padding,0,padding);
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) || (Util.getPlatformType()==Constants.PLATFORM_PHONEGAP)){
            textView.setBackgroundColor(Color.WHITE);
        }
        textView.setTextAppearance(context,android.R.style.TextAppearance_DeviceDefault_Medium);
        textView.setTextColor(Color.parseColor("#33b5e5"));
        textView.setGravity(Gravity.CENTER);
        textView.setText(value);
        linearLayout.addView(textView);
    }

    public SHFeedbackListModelView(Context context) {
        super(context);
        this.context = context;
    }
    @SuppressLint("NewApi")
    public View getFeedbackView(){
        linearLayout = new LinearLayout(context);
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(textView);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }
}