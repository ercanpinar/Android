package com.streethawk.library.feeds;

/**
 * Observer for button pressed on tips
 */
public interface ITipClickEvents {
    public void onButtonClickedOnTip(TipObject object,int feedResults[]);
}
