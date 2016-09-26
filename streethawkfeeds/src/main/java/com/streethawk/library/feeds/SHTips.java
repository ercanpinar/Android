package com.streethawk.library.feeds;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class to display individual tips
 */
public class SHTips extends Utils implements Constants {


    private int mImageId = -1;  //TODO

    private static PopupWindow tipPopUpWindow;
    private static ArrayList<ITipClickEvents> mClickEventListeners = null;

    private static volatile Dialog mDialog = null;
    private final int GRAVITY_LEFT = 0;
    private final int GRAVITY_CENTER = 1;
    private final int GRAVITY_RIGHT = 2;

    private boolean mSendFeedResultForEachTip = true;


    public void unit_test_tooltip(Activity activity, String target) {
        TipObject widget = new TipObject();
        widget.setTitle("Enter key here");
        widget.setTitleColor("#FF0000");
        widget.setContent("Key is must for tagging");
        widget.setTitleColor("#FFFF00");
        widget.setBackGroundColor("#80FF00FF");
        widget.setAcceptedButtonTitle("NEXT");
        widget.setDeclinedButtonTitle("Back");
        widget.setPlacement(TOP);
        widget.setTarget(target);
        showTip(activity, widget, true);
    }

    public void showTips(){

    }


    private String[] getButtonTextFromButtonPairs(String pairName) {
        String[] btnPairs = new String[2];
        //TODO : Add code to implement fetching of button pairs

        //ANURAG REMOVE HARDCODED NAMES
        btnPairs[0] = "Next";
        btnPairs[1] = "Back";
        return btnPairs;
    }

    public void parseJSONToTipObject(TipObject tipObject, JSONObject object) {
        if (null == object)
            return;
        try {
            tipObject.setId(object.getString(TIP_ID));
        } catch (JSONException e) {
            tipObject.setId(null);
        }
        try {
            tipObject.setTitle(object.getString(TITLE));
        } catch (JSONException e) {
            tipObject.setTitle(null);
        }
        try {
            tipObject.setTitleColor(object.getString(TITLE_COLOR));
        } catch (JSONException e) {
            tipObject.setTitleColor("#000000");
        }
        try {
            tipObject.setContent(object.getString(CONTENT));
        } catch (JSONException e) {
            tipObject.setContent(null);
        }
        try {
            tipObject.setContentColor(object.getString(CONTENT_COLOR));
        } catch (JSONException e) {
            tipObject.setContentColor("#000000");
        }
        try {
            tipObject.setBackGroundColor(object.getString(BG_COLOR));
        } catch (JSONException e) {
            tipObject.setBackGroundColor("#FFFFFF");
        }
        try {
            tipObject.setTarget(object.getString(TARGET));
        } catch (JSONException e) {
            tipObject.setTarget(null);
        }
        try {
            JSONArray childArray = object.getJSONArray(CHILD);
            int size = childArray.length();
            String[] str = new String[size];
            for (int i = 0; i < size; i++) {
                str[i] = childArray.getString(i);
            }
            tipObject.setChild(str);
        } catch (JSONException e) {
            tipObject.setTarget(null);
        }
        try {
            tipObject.setPlacement(object.getString(PLACEMENT));
        } catch (JSONException e) {
            tipObject.setPlacement(BOTTOM);
        }
        try {
            tipObject.setDelay(object.getInt(DELAY));
        } catch (JSONException e) {
            tipObject.setDelay(0);
        }
        try {
            JSONObject customData = object.getJSONObject(CUSTOM_DATA);
            if (null != customData) {
                try {
                    tipObject.setAcceptedButtonTitle(customData.getString(NEXT_BUTTON));
                } catch (JSONException e) {
                    tipObject.setAcceptedButtonTitle(null);
                }
                try {
                    tipObject.setDeclinedButtonTitle(customData.getString(PREV_BUTTON));
                } catch (JSONException e) {
                    tipObject.setDeclinedButtonTitle(null);
                }
                try {
                    tipObject.setCloseButtonTitle(customData.getString(CLOSE_BUTTON));
                } catch (JSONException e) {
                    tipObject.setCloseButtonTitle(null);
                }
                try {
                    String buttonPair = customData.getString(PAIR);
                    if (null != buttonPair) {
                        String[] btnPairs = getButtonTextFromButtonPairs(buttonPair);
                        tipObject.setAcceptedButtonTitle(btnPairs[0]);
                        tipObject.setDeclinedButtonTitle(btnPairs[1]);
                    }
                } catch (JSONException e) {
                    //Dont do anything here
                }
                try {
                    tipObject.setImageUrl(customData.getString(IMG_URL));
                } catch (JSONException e) {
                    tipObject.setImageUrl(null);
                }
                JSONObject dndObject = customData.getJSONObject(DND);
                if (null != dndObject) {
                    tipObject.setHasDND(true);
                    try {
                        tipObject.setDNDTitle(dndObject.getString(DND_TITLE));
                    } catch (JSONException e) {
                        tipObject.setDNDTitle(null);
                    }
                    try {
                        tipObject.setDNDContent(dndObject.getString(DND_CONTENT));
                    } catch (JSONException e) {
                        tipObject.setDNDContent(null);
                    }
                    try {
                        tipObject.setDNDB1(dndObject.getString(DND_B1));
                    } catch (JSONException e) {
                        tipObject.setDNDB1(null);
                    }
                    try {
                        tipObject.setDNDB2(dndObject.getString(DND_B2));
                    } catch (JSONException e) {
                        tipObject.setDNDB2(null);
                    }
                }
            }

        } catch (JSONException e) {
            //Do nothing
        }
        try {
            tipObject.setParent(object.getString(PARENT));
        } catch (JSONException e) {
            tipObject.setParent(null);
        }
    }

    /**
     * @param widget
     * @param tip
     * @param length
     * @return
     */
    private int calculateGravity(float widget, float tip, float length) {

        float fourth = length / 4;

        float cp1 = tip + fourth;
        float cp2 = cp1 + fourth;
        float cp3 = cp2 + fourth;

        if (widget < cp1) {
            return GRAVITY_LEFT;
        }

        if (widget >= cp2 && widget <= cp3) {
            return GRAVITY_CENTER;
        }

        if (widget > cp3)
            return GRAVITY_RIGHT;

        return 0;
    }

    private View getViewByPosition(int pos, AbsListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showTip(Activity currentActivity, TipObject widget, boolean sendResult) {
        if (null == currentActivity)
            return;
        if (widget == null)
            return;
        mSendFeedResultForEachTip = sendResult;
        if (null == tipPopUpWindow) {
            tipPopUpWindow = new PopupWindow(currentActivity);
        }
        if (tipPopUpWindow.isShowing())
            tipPopUpWindow.dismiss();
        View tipView = getTipView(currentActivity, widget);
        if (tipView == null)
            return;
        tipPopUpWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View newPointerView = getPointerView(currentActivity, widget);
        if (null == newPointerView) {
            return;
        }

        newPointerView.setLayoutParams(new ViewGroup.LayoutParams(50, 50));

        LinearLayout rootView = new LinearLayout(currentActivity);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout wrapper = new LinearLayout(currentActivity);
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout pointerWrapper = new LinearLayout(currentActivity);
        pointerWrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        pointerWrapper.setBackgroundColor(Color.TRANSPARENT);
        int widgetID = getResIdFromWidgetName(currentActivity, widget.getTarget());
        View anchor = currentActivity.findViewById(widgetID);
        String [] child = widget.getChild();
        if(null!=child){
            int length = child.length;
            if(length>0){
                Object anchorObject = anchor;
                String childReferece = child[length-1];
                if ((anchorObject instanceof ListView)||(anchorObject instanceof GridView)
                        || (anchorObject instanceof ExpandableListView)) {
                    AbsListView list = (AbsListView)anchorObject;
                    int position;
                    try{
                        position = Integer.parseInt(childReferece);
                        list.smoothScrollToPosition(position);
                        anchor = getViewByPosition(position,list);
                    }catch (NumberFormatException e){
                        Log.e("StreetHawk","Android doesnt support setting tips on listview using text value");
                    }
                }
            }

        }
        String placement = widget.getPlacement();
        if (null != placement) {
            switch (placement) {
                case BOTTOM:
                default:
                    wrapper.setOrientation(LinearLayout.VERTICAL);
                    pointerWrapper.addView(newPointerView);
                    wrapper.addView(pointerWrapper);
                    wrapper.addView(tipView);
                    rootView.addView(wrapper);
                    rootView.measure(0, 0);
                    RelativeLayout.LayoutParams paramsB = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_TOP, widgetID);
                    rootView.setLayoutParams(paramsB);
                    int gravity = calculateGravity(anchor.getY(), rootView.getY(), anchor.getWidth());
                    if (GRAVITY_LEFT == gravity) {
                        pointerWrapper.setGravity(Gravity.LEFT);
                    }
                    if (GRAVITY_CENTER == gravity) {
                        pointerWrapper.setGravity(Gravity.CENTER);
                    }
                    if (GRAVITY_RIGHT == gravity) {
                        pointerWrapper.setGravity(Gravity.RIGHT);
                    }

                    tipPopUpWindow.setContentView(rootView);
                    tipPopUpWindow.showAsDropDown(anchor, 0, 0);
                    break;
                case RIGHT:
                    RelativeLayout.LayoutParams paramsR = new RelativeLayout.LayoutParams(RelativeLayout.RIGHT_OF, widgetID);
                    rootView.setLayoutParams(paramsR);
                    wrapper.setOrientation(LinearLayout.HORIZONTAL);
                    pointerWrapper.addView(newPointerView);
                    wrapper.addView(pointerWrapper);
                    pointerWrapper.setGravity(Gravity.RIGHT);
                    wrapper.addView(tipView);
                    rootView.addView(wrapper);
                    rootView.measure(0, 0);
                    int gravity_right = calculateGravity(anchor.getX(), rootView.getX(), anchor.getHeight());
                    if (GRAVITY_LEFT == gravity_right) {
                        pointerWrapper.setGravity(Gravity.LEFT);
                    }
                    if (GRAVITY_CENTER == gravity_right) {
                        pointerWrapper.setGravity(Gravity.CENTER);
                    }
                    if (GRAVITY_RIGHT == gravity_right) {
                        pointerWrapper.setGravity(Gravity.RIGHT);
                    }
                    tipPopUpWindow.setContentView(rootView);
                    tipPopUpWindow.showAsDropDown(anchor, anchor.getWidth(), -(anchor.getHeight() / 4));
                    break;
                case LEFT:
                    RelativeLayout.LayoutParams paramsL = new RelativeLayout.LayoutParams(RelativeLayout.LEFT_OF, widgetID);
                    rootView.setLayoutParams(paramsL);
                    wrapper.setOrientation(LinearLayout.HORIZONTAL);
                    pointerWrapper.addView(newPointerView);
                    wrapper.addView(tipView);
                    wrapper.addView(pointerWrapper);
                    rootView.addView(wrapper);
                    rootView.measure(0, 0);
                    int left_height = rootView.getMeasuredHeight();
                    int left_width = rootView.getMeasuredWidth();
                    int gravity_left = calculateGravity(anchor.getX(), rootView.getX(), anchor.getHeight());
                    if (GRAVITY_LEFT == gravity_left) {
                        pointerWrapper.setGravity(Gravity.LEFT);
                    }
                    if (GRAVITY_CENTER == gravity_left) {
                        pointerWrapper.setGravity(Gravity.CENTER);
                    }
                    if (GRAVITY_RIGHT == gravity_left) {
                        pointerWrapper.setGravity(Gravity.RIGHT);
                    }
                    tipPopUpWindow.setContentView(rootView);
                    tipPopUpWindow.showAsDropDown(anchor, -1 * left_width, -1 * (anchor.getHeight() + (left_height)));
                    break;
                case TOP:
                    LinearLayout.LayoutParams paramsT = new LinearLayout.LayoutParams(RelativeLayout.ALIGN_BOTTOM, widgetID);
                    rootView.setLayoutParams(paramsT);
                    wrapper.setOrientation(LinearLayout.VERTICAL);
                    pointerWrapper.addView(newPointerView);
                    wrapper.addView(tipView);

                    wrapper.addView(pointerWrapper);
                    rootView.addView(wrapper);
                    rootView.measure(0, 0);
                    int top_height = rootView.getMeasuredHeight();
                    int gravity_top = calculateGravity(anchor.getY(), rootView.getY(), anchor.getWidth());
                    if (GRAVITY_LEFT == gravity_top) {
                        pointerWrapper.setGravity(Gravity.LEFT);
                    }
                    if (GRAVITY_CENTER == gravity_top) {
                        pointerWrapper.setGravity(Gravity.CENTER);
                    }
                    if (GRAVITY_RIGHT == gravity_top) {
                        pointerWrapper.setGravity(Gravity.RIGHT);
                    }
                    tipPopUpWindow.setContentView(rootView);
                    tipPopUpWindow.showAsDropDown(anchor, 0, -(anchor.getHeight() + top_height));
                    break;
            }
        }
    }

    /**
     * Function returns resID based on button name. Returns -1 if not button pair found
     *
     * @param buttonName
     * @return
     */
    private int getResIdFromButtonName(String buttonName) {
        return -1;
    }



    public void registerClickListener(ITipClickEvents eventListener) {
        if (null == mClickEventListeners) {
            mClickEventListeners = new ArrayList<ITipClickEvents>();
        }
        mClickEventListeners.add(eventListener);
    }

    class Pointer extends View {

        private Paint mPaint;
        private String mFillColor;
        private String mBorderColor;

         /*
          Triangle will be drawn as
                    B
                  A   C
         */

        private float[] mPointA;
        private float[] mPointB;
        private float[] mPointC;

        public Pointer(Context context) {
            super(context);
            mPaint = new Paint();
        }

        public void setPointA(float[] pointA) {
            mPointA = pointA;
        }

        public void setPointB(float[] pointB) {
            mPointB = pointB;
        }

        public void setPointC(float[] pointC) {
            mPointC = pointC;
        }

        public void setFillColor(String color) {
            mFillColor = color;
        }

        public void setBorderColor(String color) {
            mBorderColor = color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float strokeWidth = 10f;
            float aX = mPointA[0];
            float aY = mPointA[1];

            float bX = mPointB[0];
            float bY = mPointB[1];

            float cX = mPointC[0];
            float cY = mPointC[1];

            mPaint.setColor(Color.parseColor(mBorderColor));
            mPaint.setStrokeWidth(strokeWidth);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(aX, aY);
            path.lineTo(bX, bY);
            path.lineTo(cX, cY);
            path.lineTo(aX, aY);
            path.close();
            canvas.drawPath(path, mPaint);
        }
    }

    //TODO add proper feed result for all the three function

    private View.OnClickListener declineButtonListener(final Activity activity, final TipObject widget) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    tipPopUpWindow.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    if (mSendFeedResultForEachTip)
                        SHFeedItem.getInstance(activity).notifyFeedResult(int_id, -1);
                    int[] results = new int[2];
                    results[0] = -1;  //FeedResult
                    results[1] = -1;  //Expires
                    for (ITipClickEvents listener : mClickEventListeners) {
                        listener.onButtonClickedOnTip(widget, results);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener acceptedButtonListener(final Activity activity, final TipObject widget) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    tipPopUpWindow.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    if (mSendFeedResultForEachTip)
                        SHFeedItem.getInstance(activity).notifyFeedResult(int_id, 1);
                    int[] results = new int[2];
                    results[0] = 1;  //FeedResult
                    results[1] = -1; //Expires
                    for (ITipClickEvents listener : mClickEventListeners) {
                        listener.onButtonClickedOnTip(widget, results);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private View.OnClickListener customCrossButtonListener(final Activity activity, final TipObject widget) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (widget.getHasDND()) {
                    tipPopUpWindow.dismiss();
                    final String title = widget.getDNDTitle();
                    final String content = widget.getDNDContent();
                    final String positiveButtonTitle = widget.getDNDB1();
                    final String negativeButtonTitle = widget.getDNDB2();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            if (null == title && null == content) {
                                laterButtonListener(activity, widget).onClick(view);
                            }
                            if (null != title) {
                                builder.setTitle(title);
                            }
                            if (null != content) {
                                builder.setMessage(content);
                            }
                            if (null != positiveButtonTitle) {
                                builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String id = widget.getId();
                                        int int_id = Integer.parseInt(id);
                                        if (mSendFeedResultForEachTip)
                                            SHFeedItem.getInstance(activity).notifyFeedResult(int_id, 0);
                                        int[] results = new int[2];
                                        results[0] = -2;  //FeedResult
                                        results[1] = 1; //Dismiss for all
                                        for (ITipClickEvents listener : mClickEventListeners) {
                                            listener.onButtonClickedOnTip(widget, results);
                                        }
                                    }
                                });
                            }
                            if (null != negativeButtonTitle) {
                                builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String id = widget.getId();
                                        int int_id = Integer.parseInt(id);
                                        if (mSendFeedResultForEachTip)
                                            SHFeedItem.getInstance(activity).notifyFeedResult(int_id, 0);
                                        int[] results = new int[2];
                                        results[0] = -2;  //FeedResult
                                        results[1] = 0;
                                        for (ITipClickEvents listener : mClickEventListeners) {
                                            listener.onButtonClickedOnTip(widget, results);
                                        }
                                    }
                                });
                            }
                            builder.setCancelable(false); // Adding as dismiss is causing side effects
                            Dialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                } else {
                    laterButtonListener(activity, widget).onClick(view);
                }

            }
        };
    }

    private View.OnClickListener laterButtonListener(final Activity activity, final TipObject widget) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tipPopUpWindow.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    if (mSendFeedResultForEachTip)
                        SHFeedItem.getInstance(activity).notifyFeedResult(int_id, 0);
                    int[] results = new int[2];
                    results[0] = 0;  //FeedResult
                    results[1] = -1; //Expires
                    if (null != mClickEventListeners) {
                        for (ITipClickEvents listener : mClickEventListeners) {
                            listener.onButtonClickedOnTip(widget, results);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private View getPointerView(Activity activity, TipObject widget) {
        String placement = widget.getPlacement();
        Context context = activity.getApplicationContext();
        String widgetName = widget.getTarget();

        if (null == widgetName) {
            return null;
        } else {
            if (widgetName.isEmpty()) {
                return null;
            }

            int widgetID = getResIdFromWidgetName(activity, widgetName);
            if (-1 == widgetID) {
                return null;
            }
            final float POINTERHEIGHT = 50;
            float[] A = new float[2];
            float[] B = new float[2];
            float[] C = new float[2];

            switch (placement) {
                case BOTTOM:
                default:
                    A[0] = 0;
                    A[1] = POINTERHEIGHT;

                    B[0] = POINTERHEIGHT / 2;
                    B[1] = 0;

                    C[0] = POINTERHEIGHT;
                    C[1] = POINTERHEIGHT;

                    break;
                case TOP:
                    A[0] = 0;
                    A[1] = 0;

                    B[0] = POINTERHEIGHT / 2;
                    B[1] = POINTERHEIGHT;

                    C[0] = POINTERHEIGHT;
                    C[1] = 0;
                    break;
                case LEFT:  //Display on left of the widget
                    A[0] = 0;
                    A[1] = 0;

                    B[0] = POINTERHEIGHT;
                    B[1] = POINTERHEIGHT / 2;

                    C[0] = 0;
                    C[1] = POINTERHEIGHT;
                    break;
                case RIGHT:  //Displays on the right of the widget
                    A[0] = POINTERHEIGHT;
                    A[1] = 0;

                    B[0] = 0;
                    B[1] = POINTERHEIGHT / 2;

                    C[0] = POINTERHEIGHT;
                    C[1] = POINTERHEIGHT;
                    break;
            }
            Pointer pointer = new Pointer(context);
            pointer.setBackgroundColor(Color.TRANSPARENT);
            pointer.setPointA(A);
            pointer.setPointB(B);
            pointer.setPointC(C);
            String backGroundColor = widget.getBackGroundColor();
            pointer.setFillColor(backGroundColor);
            pointer.setBorderColor(backGroundColor);
            return pointer;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  //Anurag change min SDK to 11
    private View getTipView(Activity activity, TipObject widget) {
        if (null == activity)
            return null;

        String backGroundColor = widget.getBackGroundColor();
        String titleColor = widget.getTitleColor();
        String contentColor = widget.getContentColor();
        Context context = activity.getApplicationContext();
        LinearLayout rootView = new LinearLayout(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(rootParams);
        rootView.setBackgroundColor(Color.parseColor(backGroundColor));

        //Dismiss button
        LinearLayout LL_dismiss = new LinearLayout(context);
        LinearLayout.LayoutParams dismissLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_dismiss.setOrientation(LinearLayout.VERTICAL);
        LL_dismiss.setPadding(1, 1, 1, 1);
        LL_dismiss.setLayoutParams(dismissLLParams);
        LL_dismiss.setGravity(Gravity.RIGHT);
        ImageButton dismissButton = new ImageButton(context);
        dismissButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dismissButton.setBackgroundColor(Color.TRANSPARENT);
        Bitmap cancel = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.shclose);
        dismissButton.setImageBitmap(cancel);
        dismissButton.setOnClickListener(customCrossButtonListener(activity, widget));
        LL_dismiss.addView(dismissButton);

        //Title Bar
        LinearLayout LL_titleBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_titleBar.setOrientation(LinearLayout.VERTICAL);
        LL_titleBar.setLayoutParams(LL_titleParams);

        LinearLayout LL_title_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_title_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_title_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_title_horizontal.setLayoutParams(LL_title_horizontalParams);

        if (-1 != mImageId) {
            //Add image view here
            ImageButton profilePic = new ImageButton(context);
            profilePic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            profilePic.setPadding(1, 1, 1, 1);
            profilePic.setBackgroundColor(Color.TRANSPARENT);
            profilePic.setBackgroundResource(mImageId);
            LL_title_horizontal.addView(profilePic);
        }

        String title = widget.getTitle();
        if (null != title) {
            //Add title here
            if (!title.isEmpty()) {
                TextView titletv = new TextView(context);
                titletv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                titletv.setTextAppearance(context, android.R.style.TextAppearance_Large);
                titletv.setText(title);
                titletv.setPadding(1, 1, 1, 1);
                titletv.setTextColor(Color.parseColor(titleColor));
                LL_title_horizontal.addView(titletv);
            }
        }
        LL_titleBar.addView(LL_title_horizontal);
        //MessageBar
        LinearLayout LL_MessageBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_MessageBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_MessageBar.setOrientation(LinearLayout.VERTICAL);
        LL_MessageBar.setLayoutParams(LL_MessageBarParams);
        LL_MessageBar.setPadding(1, 1, 1, 1);
        String content = widget.getContent();
        if (null != content) {
            if (!content.isEmpty()) {
                TextView messageTv = new TextView(context);
                messageTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                messageTv.setText(content);
                messageTv.setPadding(1, 1, 1, 1);
                messageTv.setTextColor(Color.parseColor(contentColor));
                LL_MessageBar.addView(messageTv);
            }
        }
        //Buttons
        LinearLayout LL_ButtonBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_ButtonBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_ButtonBar.setOrientation(LinearLayout.VERTICAL);
        LL_ButtonBar.setLayoutParams(LL_ButtonBarParams);
        LL_ButtonBar.setPadding(1, 1, 1, 1);

        LinearLayout LL_Button_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_Button_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_Button_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_Button_horizontal.setLayoutParams(LL_Button_horizontalParams);
        LL_Button_horizontal.setGravity(Gravity.RIGHT);

        String declineButtonTitle = widget.getDelineButtonTitle();
        if (null != declineButtonTitle) {
            if (!declineButtonTitle.isEmpty()) {
                int resId = getResIdFromButtonName(declineButtonTitle);
                if (-1 != resId) {
                    //Create image button here
                    ImageButton imgBtn = new ImageButton(context);
                    imgBtn.setPadding(1, 1, 1, 1);
                    imgBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imgBtn.setBackgroundColor(Color.TRANSPARENT);
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(declineButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(context);
                    btn.setBackgroundColor(Color.parseColor(backGroundColor));
                    btn.setTextColor(Color.parseColor(titleColor));
                    btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    btn.setText(declineButtonTitle);
                    btn.setBackgroundColor(Color.TRANSPARENT);
                    btn.setOnClickListener(declineButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(btn);
                }
            }
        }

        String accpetedButtonTitle = widget.getAcceptedButtonTitle();
        if (null != accpetedButtonTitle) {
            if (!accpetedButtonTitle.isEmpty()) {
                int resId = getResIdFromButtonName(accpetedButtonTitle);
                if (-1 != resId) {
                    //Create image button here
                    ImageButton imgBtn = new ImageButton(context);
                    imgBtn.setPadding(1, 1, 1, 1);
                    imgBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imgBtn.setBackgroundColor(Color.TRANSPARENT);
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(acceptedButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(context);
                    btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    btn.setBackgroundColor(Color.parseColor(backGroundColor));
                    btn.setTextColor(Color.parseColor(titleColor));
                    btn.setText(accpetedButtonTitle);
                    btn.setBackgroundColor(Color.TRANSPARENT);
                    btn.setOnClickListener(acceptedButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(btn);
                }
            }
        }
        LL_ButtonBar.addView(LL_Button_horizontal);
        rootView.addView(LL_dismiss);
        rootView.addView(LL_titleBar);
        rootView.addView(LL_MessageBar);
        rootView.addView(LL_ButtonBar);
        return rootView;
    }
}