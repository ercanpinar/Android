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

/**
 * Class for defining button pairs for interactive push
 */
public class InteractivePush implements Constants{

    private final String TAG = "StreetHawk_IntPush";

    /**
     * Title for button 1
     */
    private String mB1Title;
    /**
     * Icon identifier for button1.
     * Put -1 if you dont wish to associate icons with this button
     */
    private int mB1Icon;

    /**
     * Title for button 2
     */
    private String mB2Title;
    /**
     * Icon identifier for button1.
     * Put -1 if you dont wish to associate icons with this button
     */
    private int mB2Icon;

    /**
     * Unique Identifier for title
     */
    private String mPairTitle;

    /**
     * Title for button 3 (Not supported)
     */
    private String mB3Title = null;

    /**
     * Icon for icon 3 (Not suppoted)
     */
    private int mB3Icon = -1;


    /**
     * Default constructor
     */
    public InteractivePush(){}

    /**
     * Constructor to define button pairs for interactive push
     * @param B1Title  Title for button 1
     * @param B1Icon Icon identifier for button 1
     * @param B2Title Title for button 2
     * @param B2Icon Icon identifier for button 2
     * @param pairTitle Title for this button pair to be displayed in StreetHawk dashboard (optional pass null)
     */
    public InteractivePush(String B1Title,int B1Icon,String B2Title,int B2Icon,String pairTitle){

        if(null==B1Title){
            Log.e(TAG,"B1Title is null returning");
            return;
        }
        if(null==B2Title){
            Log.e(TAG,"B2Title is null returning");
            return;
        }
        this.mB1Title   = B1Title;
        this.mB1Icon    = B1Icon;

        this.mB2Title   = B2Title;
        this.mB2Icon    = B2Icon;

        if(null==pairTitle) {
            this.mPairTitle = B1Title.concat(B2Title);
        }else if(pairTitle.isEmpty()){
            this.mPairTitle = B1Title.concat(B2Title);
        }
        else{
            this.mPairTitle = pairTitle;
        }
    }

    /**
     * Constructor to define button pairs without icons for interactive push
     * @param B1Title Title for button 1
     * @param B2Title Title for button 2
     * @param pairTitle Title for this button pair to be displayed in StreetHawk dashboard (optional pass null)
     */
    public InteractivePush(String B1Title,String B2Title,String pairTitle){
        new InteractivePush(B1Title,-1,B2Title,-1,pairTitle);
    }


    /**
     * set title for button pair
     * @param title
     */
    public void setPairTitle(String title){this.mPairTitle = title;}

    /**
     * Set title for button 1
     * @param B1Title
     */
    public void setB1Title(String B1Title){this.mB1Title = B1Title;}

    /**
     * Set icon identifier for button 1
     * @param B1Icon
     */
    public void setB1Icon(int B1Icon){this.mB1Icon = B1Icon;}

    /**
     * Set title for button 1
     * @param B2Title
     */
    public void setB2Title(String B2Title){this.mB2Title = B2Title;}
    /**
     * Set icon identifier for button 1
     * @param B2Icon
     */
    public void setB2Icon(int B2Icon){this.mB2Icon = B2Icon;}

    /**
     * Set title for button 3 (Not supported)
     * @param B3Title
     */
    public void setB3Title(String B3Title){this.mB3Title = B3Title;}
    /**
     * Set icon identifier for button 3 (Not supported)
     * @param B3Icon
     */
    public void setB3Icon(int B3Icon){this.mB3Icon = B3Icon;}


    /**
     * Returns tile for given pair (Set internally by SDK)
     * @return
     */
    public String getPairTitle(){return mPairTitle;}

    /**
     * Returns title for button 1
     * @return title
     */
    public String getB1Title(){return mB1Title;}

    /**
     * Returns title for button 2
     * @return title
     */
    public String getB2Title(){return mB2Title;}

    /**
     * Returns title for button 3 (Not supported)
     * @return title
     */
    public String getB3Title(){return mB3Title;}


    /**
     * Returns icon identifier for button 1
     * @return identifier
     */
    public int getB1Icon(){return mB1Icon;}
    /**
     * Returns icon identifier for button 2
     * @return identifier
     */
    public int getB2Icon(){return mB2Icon;}
    /**
     * Returns icon identifier for button 3 (Not supported)
     * @return identifier
     */
    public int getB3Icon(){return mB3Icon;}

    /**
     * Display data in logcat for debugging
     * @param TAG
     */
    public void displayMyDaya(String TAG){

        String NEWLINE = "\n";
        if(null==TAG){
            TAG = "StreetHawk";
        }
        String myData = "InteractivePushData" + NEWLINE +
                "PairTitle " + this.mPairTitle + NEWLINE +
                "B1Title "   + this.mB1Title + NEWLINE +
                "B2Title "   + this.mB2Title + NEWLINE +
                "B3Title "   + this.mB3Title + NEWLINE +
                "B1Icon "    + this.mB1Icon + NEWLINE +
                "B2Icon "    + this.mB2Icon + NEWLINE +
                "B3Icon "    + this.mB3Icon
                ;

        Log.i(TAG,myData);
    }

}
