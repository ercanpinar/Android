package com.streethawk.library.feeds;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streethawk.library.core.WidgetDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class renders simple modal
 */
public class Modal implements Constants,IPointziClickEventsListener {

    public void unit_test_tooltip(Activity activity) {
        TipObject widget = new TipObject();
        widget.setTitle("<b>Enter key here</b>");
        widget.setTitleColor("#FF0000");
        widget.setContent("Key is must for tagging <a href=\"slack://open\">slack</a>");
        widget.setTitleColor("#FFFF00");
        widget.setBackGroundColor("#80FF00FF");
        widget.setPlacement(TOP);
        widget.setAcceptedButtonTitle("GetStarted");
        showModal(activity, widget);
    }

    private Activity mActivity;
    private static Dialog mDialog;
    private static ArrayList<IPointziClickEventsListener> mClickEventListeners = null;

    public Modal() {
    }

    public void registerClickListener(IPointziClickEventsListener eventListener) {
        if (null == mClickEventListeners) {
            mClickEventListeners = new ArrayList<IPointziClickEventsListener>();
        }
        mClickEventListeners.add(eventListener);
    }


    private void parsePayloadToGetObject(SHTriger source, TipObject dest) {
        if (null == source)
            return;
        if (null == dest)
            return;

        JSONArray modalArray = null;
        try {
            modalArray = new JSONArray(source.getJSON());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JSONObject modal = null;
        try {
            modal = modalArray.getJSONObject(0);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (null != modal) {
            try {
                dest.setId(modal.getString(TIP_ID));
            } catch (JSONException e1) {
            }
            try {
                dest.setTitle(modal.getString(TITLE));
            } catch (JSONException e1) {
            }
            try {
                dest.setContent(modal.getString(CONTENT));
            } catch (JSONException e1) {
            }
            try {
                dest.setPlacement(modal.getString(PLACEMENT));
            } catch (JSONException e1) {
            }
            try {
                dest.setTarget(modal.getString(TARGET));
            } catch (JSONException e1) {
            }
            try {
                dest.setBackGroundColor(modal.getString(BG_COLOR));
            } catch (JSONException e1) {
            }

            try {
                dest.setTitleColor(modal.getString(TITLE_COLOR));
            } catch (JSONException e1) {
            }

            try {
                dest.setContentColor(modal.getString(CONTENT_COLOR));
            } catch (JSONException e1) {
            }

            try {
                dest.setDelay(modal.getInt(DELAY));
            } catch (JSONException e1) {
            }
            try {
                dest.setImageUrl(modal.getString(IMG_URL));
            } catch (JSONException e1) {
            }
            try {
                dest.setParent(modal.getString(PARENT));
            } catch (JSONException e1) {
            }

            JSONObject customData = null;
            try {
                customData = modal.getJSONObject(CUSTOM_DATA);
            } catch (JSONException e) {
                customData = null;
            }
            if (null != customData) {
                try {
                    dest.setAcceptedButtonTitle(customData.getString(NEXT_BUTTON));
                } catch (JSONException e1) {
                }
                try {
                    dest.setDeclinedButtonTitle(customData.getString(PREV_BUTTON));
                } catch (JSONException e1) {
                }
                try {
                    dest.setCloseButtonTitle(customData.getString(CLOSE_BUTTON));
                } catch (JSONException e1) {
                }
                JSONObject DNDObject = null;
                try {
                    DNDObject = customData.getJSONObject(DND);
                } catch (JSONException e) {
                    DNDObject = null;
                }
                if(null!=DNDObject) {
                    try {
                        dest.setDNDTitle(DNDObject.getString(DND_TITLE));
                    } catch (JSONException e1) {
                    }

                    try {
                        dest.setDNDContent(DNDObject.getString(DND_CONTENT));
                    } catch (JSONException e1) {
                    }
                    String DNDB1 = null;
                    String DNDB2 = null;

                    try {
                        DNDB1 = DNDObject.getString(DND_B1);
                        dest.setDNDB1(DNDB1);
                    } catch (JSONException e1) {
                    }
                    try {
                        DNDB2 = DNDObject.getString(DND_B2);
                        dest.setDNDB2(DNDB2);
                    } catch (JSONException e1) {
                    }

                    if (null == DNDB1 && null == DNDB2) {
                        dest.setHasDND(false);
                    } else {
                        dest.setHasDND(true);
                    }
                }
            }
        }
    }

    public void showModal(Activity activity, SHTriger trigger) {
        if (null == activity)
            return;
        mActivity = activity;
        TipObject widget = new TipObject();
        parsePayloadToGetObject(trigger, widget);
        showModal(activity,widget);
    }

    public void showModal(Activity activity, TipObject widget) {
        mActivity = activity;
        if (null == activity)
            return;
        Context context = activity.getApplicationContext();
        LinearLayout baseLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(params);


        baseLayout.addView(getContentView(activity, widget));
        if (null == mDialog)
            mDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        mDialog = new Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(baseLayout);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mDialog.isShowing())
            mDialog.dismiss();
        mDialog.show();
    }

    private View.OnClickListener declineButtonListener(final Activity activity, final TipObject widget) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    mDialog.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    int[] results = new int[2];
                    results[0] = -1;  //FeedResult
                    results[1] = -1;  //Expires
                    if(null!=mClickEventListeners) {
                        for (IPointziClickEventsListener listener : mClickEventListeners) {
                            listener.onButtonClickedOnTip(widget, results);
                        }
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
                    mDialog.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    int[] results = new int[2];
                    results[0] = 1;  //FeedResult
                    results[1] = -1; //Expires
                    if(null!=mClickEventListeners) {
                        for (IPointziClickEventsListener listener : mClickEventListeners) {
                            listener.onButtonClickedOnTip(widget, results);
                        }
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
                    mDialog.dismiss();
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
                                        int[] results = new int[2];
                                        results[0] = -2;  //FeedResult
                                        results[1] = 1; //Dismiss for all
                                        if(null!=mClickEventListeners) {
                                            for (IPointziClickEventsListener listener : mClickEventListeners) {
                                                listener.onButtonClickedOnTip(widget, results);
                                            }
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
                                        int[] results = new int[2];
                                        results[0] = -2;  //FeedResult
                                        results[1] = 0;
                                        if(null!=mClickEventListeners) {
                                            for (IPointziClickEventsListener listener : mClickEventListeners) {
                                                listener.onButtonClickedOnTip(widget, results);
                                            }
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
                    mDialog.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
                    int[] results = new int[2];
                    results[0] = 0;  //FeedResult
                    results[1] = -1; //Expires
                    if (null != mClickEventListeners) {
                        if(null!=mClickEventListeners) {
                            for (IPointziClickEventsListener listener : mClickEventListeners) {
                                listener.onButtonClickedOnTip(widget, results);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private String getViewName(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }

    /**
     * Function returns widgetID from the widgetName provided.
     * TODO: Write a function to store widget name
     *
     * @param widgetName
     * @return
     */
    private int getResIdFromWidgetName(Activity activity, String widgetName) {
        if (null == widgetName)
            return -1;
        WidgetDBHelper helper = new WidgetDBHelper(activity.getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();

        String parent = getViewName(activity.getClass().getName());
        String WHERE = " where ";
        String EQUALS = " = ";
        String AND = " and ";
        String DOUBLE_QUOTE = "\"";

        String query = "select * from " + WidgetDBHelper.TOOLTIP_TABLE_NAME +
                WHERE + WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widgetName.trim() + DOUBLE_QUOTE +
                AND + WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS + DOUBLE_QUOTE + parent.trim() + DOUBLE_QUOTE;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_RES_ID));
            } else {
                cursor.close();
                database.close();
                helper.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            database.close();
            helper.close();
            return -1;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  //Anurag change min SDK to 11
    protected View getContentView(Activity activity, TipObject widget) {
        if (null == activity)
            return null;
        String backGroundColor = widget.getBackGroundColor();
        String titleColor = widget.getTitleColor();
        String contentColor = widget.getContentColor();

        LinearLayout rootView = new LinearLayout(mActivity);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(rootParams);
        rootView.setBackgroundColor(Color.parseColor(backGroundColor));

        //Dismiss button
        LinearLayout LL_dismiss = new LinearLayout(mActivity);
        LinearLayout.LayoutParams dismissLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_dismiss.setOrientation(LinearLayout.VERTICAL);
        LL_dismiss.setPadding(1, 1, 1, 1);
        LL_dismiss.setLayoutParams(dismissLLParams);
        LL_dismiss.setGravity(Gravity.RIGHT);
        ImageButton dismissButton = new ImageButton(mActivity);
        dismissButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dismissButton.setBackgroundColor(Color.TRANSPARENT);
        Bitmap cancel = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.shclose);
        dismissButton.setImageBitmap(cancel);
        dismissButton.setOnClickListener(customCrossButtonListener(activity, widget));
        LL_dismiss.addView(dismissButton);

        //Title Bar
        LinearLayout LL_titleBar = new LinearLayout(mActivity);
        LinearLayout.LayoutParams LL_titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_titleBar.setOrientation(LinearLayout.HORIZONTAL);
        LL_titleBar.setLayoutParams(LL_titleParams);

        LinearLayout LL_title_horizontal = new LinearLayout(mActivity);
        LinearLayout.LayoutParams LL_title_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_title_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_title_horizontal.setLayoutParams(LL_title_horizontalParams);

        String strTitle = widget.getTitle();

        if (null != strTitle) {
            //Add title here
            if (!strTitle.isEmpty()) {
                Spanned title = Html.fromHtml(strTitle/*+'\u00A9'+0x1F601*/);
                TextView titletv = new TextView(mActivity);
                titletv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                titletv.setTextAppearance(mActivity, android.R.style.TextAppearance_Large);
                titletv.setText(title);
                titletv.setPadding(1, 1, 1, 1);

                titletv.setTextColor(Color.parseColor(titleColor));
                LL_title_horizontal.addView(titletv);
            }
        }
        LL_titleBar.addView(LL_title_horizontal);

        //MessageBar
        LinearLayout LL_MessageBar = new LinearLayout(mActivity);
        LinearLayout.LayoutParams LL_MessageBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        LL_MessageBar.setOrientation(LinearLayout.VERTICAL);
        LL_MessageBar.setLayoutParams(LL_MessageBarParams);
        LL_MessageBar.setPadding(1, 1, 1, 1);

        String strContent = widget.getContent();
        if (null != strContent) {
            if (!strContent.isEmpty()) {
                Spanned content = Html.fromHtml(strContent/*+'\u00A9'+0x1F601*/);
                TextView messageTv = new TextView(mActivity);
                messageTv.setMovementMethod(LinkMovementMethod.getInstance());
                messageTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                messageTv.setText(content);
                messageTv.setPadding(1, 1, 1, 1);
                messageTv.setLinksClickable(true);
                messageTv.setTextColor(Color.parseColor(contentColor));
                LL_MessageBar.addView(messageTv);
            }
        }

        //Buttons
        LinearLayout LL_ButtonBar = new LinearLayout(mActivity);
        LinearLayout.LayoutParams LL_ButtonBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_ButtonBar.setOrientation(LinearLayout.VERTICAL);
        LL_ButtonBar.setLayoutParams(LL_ButtonBarParams);
        LL_ButtonBar.setPadding(1, 1, 1, 1);
        LL_ButtonBar.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        LinearLayout LL_Button_horizontal = new LinearLayout(mActivity);
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
                    ImageButton imgBtn = new ImageButton(mActivity);
                    imgBtn.setPadding(1, 1, 1, 1);
                    imgBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imgBtn.setBackgroundColor(Color.TRANSPARENT);
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(declineButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(mActivity);
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
                    ImageButton imgBtn = new ImageButton(mActivity);
                    imgBtn.setPadding(1, 1, 1, 1);
                    imgBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imgBtn.setBackgroundColor(Color.TRANSPARENT);
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(acceptedButtonListener(activity, widget));
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(mActivity);
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

    @Override
    public void onButtonClickedOnTip(TipObject object, int[] feedResults) {

    }

    @Override
    public void onButtonClickedOnTour(TipObject object, int[] feedResults) {

    }

    @Override
    public void onButtonClickedOnModal(TipObject object, int[] feedResults) {

    }
}