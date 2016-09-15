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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streethawk.library.core.WidgetDB;

import java.util.ArrayList;

/**
 * Class renders simple modal
 */
public class Modal implements Constants {

    public void unit_test_tooltip(Activity activity) {
        TipObject widget = new TipObject();
        widget.setTitle("<b>Enter key here</b>");
        widget.setTitleColor("#FF0000");
        widget.setContent("Key is must for tagging");
        widget.setTitleColor("#FFFF00");
        widget.setBackGroundColor("#80FF00FF");
        widget.setPlacement(TOP);
        widget.setAcceptedButtonTitle("GetStarted");
        showModal(activity, widget);
    }

    private Activity mActivity;
    private static Dialog mDialog;
    private static ArrayList<ITipClickEvents> mClickEventListeners = null;

    public Modal() {
    }

    public void registerClickListener(ITipClickEvents eventListener) {
        if (null == mClickEventListeners) {
            mClickEventListeners = new ArrayList<ITipClickEvents>();
        }
        mClickEventListeners.add(eventListener);
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
                    mDialog.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
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
                    mDialog.dismiss();
                    String id = widget.getId();
                    int int_id = Integer.parseInt(id);
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
        WidgetDB.WidgetDBHelper helper = new WidgetDB(activity).new WidgetDBHelper(activity.getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();

        String parent = getViewName(activity.getClass().getName());
        String WHERE = " where ";
        String EQUALS = " = ";
        String AND = " and ";
        String DOUBLE_QUOTE = "\"";

        String query = "select * from " + WidgetDB.WidgetDBHelper.TOOLTIP_TABLE_NAME +
                WHERE + WidgetDB.WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widgetName.trim() + DOUBLE_QUOTE +
                AND + WidgetDB.WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS + DOUBLE_QUOTE + parent.trim() + DOUBLE_QUOTE;
        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(WidgetDB.WidgetDBHelper.COLUMN_RES_ID));
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
        Context context = activity.getApplicationContext();
        LinearLayout rootView = new LinearLayout(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
        LL_titleBar.setOrientation(LinearLayout.HORIZONTAL);
        LL_titleBar.setLayoutParams(LL_titleParams);

        LinearLayout LL_title_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_title_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_title_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_title_horizontal.setLayoutParams(LL_title_horizontalParams);

        String strTitle = widget.getTitle();

        if (null != strTitle) {
            //Add title here
            if (!strTitle.isEmpty()) {
                Spanned title = Html.fromHtml(strTitle/*+'\u00A9'+0x1F601*/);
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
        LinearLayout.LayoutParams LL_MessageBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        LL_MessageBar.setOrientation(LinearLayout.VERTICAL);
        LL_MessageBar.setLayoutParams(LL_MessageBarParams);
        LL_MessageBar.setPadding(1, 1, 1, 1);

        String strContent = widget.getContent();
        if (null != strContent) {
            if (!strContent.isEmpty()) {
                Spanned content = Html.fromHtml(strContent/*+'\u00A9'+0x1F601*/);
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
        LL_ButtonBar.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

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
