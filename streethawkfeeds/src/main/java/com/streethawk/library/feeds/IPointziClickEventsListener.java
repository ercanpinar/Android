package com.streethawk.library.feeds;

/**
 * Observer for button pressed on tips
 */
public interface IPointziClickEventsListener {
    public void onButtonClickedOnTip(TipObject object,int feedResults[]);
    public void onButtonClickedOnTour(TipObject object, int[] feedResults);
    public void onButtonClickedOnModal(TipObject object, int[] feedResults);
}
