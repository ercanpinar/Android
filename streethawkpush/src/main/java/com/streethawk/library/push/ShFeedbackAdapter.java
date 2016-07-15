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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.streethawk.library.core.Util;

import java.util.ArrayList;

class ShFeedbackAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList mOptions;
    private SHFeedbackListModel temp;

    public ShFeedbackAdapter(Context context, ArrayList options){
        this.mContext = context;
        this.mOptions = options;
    }
    @Override
    public int getCount() {
        if(mOptions.size()<=0)
            return 1;
        return mOptions.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        SHFeedbackListModelView holder = new SHFeedbackListModelView(mContext);
        if(convertView==null){
            view = holder.getFeedbackView();
            view.setTag(holder);
        }else{
            holder = (SHFeedbackListModelView) view.getTag();
        }
        if(mOptions.size()<=0){
                Log.i(Util.TAG, "Feedback list is empty");
        }else{
            temp=null;
            temp = (SHFeedbackListModel)mOptions.get(position);
            holder.setText(temp.getOption());
        }
        return view;
    }
}