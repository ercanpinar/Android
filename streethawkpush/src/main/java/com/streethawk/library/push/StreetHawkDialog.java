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
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

class StreetHawkDialog extends NotificationBase {
    private boolean mflagBG;
    private Context mContext = StreetHawk.INSTANCE.getCurrentActivity();
    private ProgressBar mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLargeInverse);
    RelativeLayout relativeLayout = new RelativeLayout(mContext);
    private static Dialog mDialog = null;
    private static StreetHawkDialog mInstance = null;
    private float mPortion = -1.0f;
    private int mOrientation = -1;
    private int mSpeed = -1;
    private PushNotificationData mPushData;

    public static StreetHawkDialog getStreetHawkDialogInstance() {
        if (null == mInstance)
            mInstance = new StreetHawkDialog();
        return mInstance;
    }

    public void setParams(PushNotificationData pushData) {
        this.mPushData = pushData;
        try {
            Float portion = Float.parseFloat(pushData.getPortion());
            this.mPortion = (portion>0&&portion<1)?portion:1.0f;
        } catch (Exception e) {
            this.mPortion = 1.0f;
        }
        try {
            int orientation = Integer.parseInt(pushData.getOrientation());
            this.mOrientation = (orientation>=0&&orientation<=3)?orientation:0;
        } catch (Exception e) {
            this.mOrientation = 0;
        }
        try {
            float fSpeed = Float.parseFloat(pushData.getSpeed());
            int speed;
            if(fSpeed>0 && fSpeed<1)
                speed = 1;
            else {
                speed = (int) Float.parseFloat(pushData.getSpeed());
            }
            this.mSpeed = speed>0?speed:0;
        } catch (Exception e) {
            this.mSpeed = 0;
        }
    }

    /**
     * API to check if given URL is valid
     *
     * @param url
     * @return true for valid url else false
     */
    private boolean isValidUrl(String url) {
        if (!(Patterns.WEB_URL.matcher(url).matches())) {
                Log.e(Util.TAG, "received invalid URL" + url);
            return false;
        }
        return true;
    }

    public void show() {
        final Builder builder = new Builder(true);
        builder.setSlide(mOrientation, mSpeed, mPortion);
        String url = mPushData.getData();
        if (!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://"+url;
        }
        //TODO: Add check for valid url here.
        builder.setUrl(url);
        String close = getStringtoDisplay(mContext, TYPE_SHOW_URL_CLOSE);
        builder.setNegativeButton(close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(Constants.PENDING_DIALOG, null);
                e.commit();
                sendResultBroadcast(mContext, mPushData.getMsgId(), Constants.STREETHAWK_ACCEPTED);
                builder.stopWebview();
                builder.clearCache();
            }
        });
        PushNotificationDB dbObject = PushNotificationDB.getInstance(mContext);
        dbObject.open();
        dbObject.forceStoreNoDialog(mPushData.getMsgId());
        dbObject.close();
        builder.show();
    }

    private class StreetHawkWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.GONE);
                relativeLayout.removeView(mProgressBar);
                view.removeView(relativeLayout);
                if (!mflagBG) {
                    if (mDialog == null) {
                        return;
                    }
                    mDialog.dismiss();
                    try {
                        mDialog.show();
                    } catch (Exception e) {
                        return;
                    }
                    mflagBG = true;
                }
            }
        }
    }

    public static void forceDismissDialog() {
        if (null != mDialog) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            mDialog = null;
        }
    }

    public static final int DIRECTION_FROM_BOTTOM_TO_TOP = 0;
    public static final int DIRECTION_FROM_TOP_TO_BOTTOM = 1;
    public static final int DIRECTION_FROM_RIGHT_TO_LEFT = 2;
    public static final int DIRECTION_FROM_LEFT_TO_RIGHT = 3;

    private class Builder {

        private Dialog dialog;
        private View mDialogView;
        private Builder mBuilderInstance = null;

        private Builder(boolean flagBG) {
            try {
                mflagBG = flagBG;                    // faced exception_in_init error here. check
            } catch (Throwable e) {
                Log.e(Util.TAG, "exception@StreethawkDialog93" + e.getCause());
            }
        }

        private String url;
        private Integer dialogSpacing;
        private String negativeButtonText;
        private View.OnClickListener negativeButtonListener;
        private DialogInterface.OnKeyListener onKeyListener;
        private DialogInterface.OnShowListener onShowListener;

        private boolean slide = false;
        private Integer direction;
        private Integer speed;
        private Float pixel;

        private WebView webView;

        public void setUrl(String url) {
            this.url = url;
        }

        public void clearCache() {
            if (null != webView) {
                webView.clearCache(true);
            }
        }

        public void setNegativeButton(String text, View.OnClickListener listener) {
            this.negativeButtonText = text;
            this.negativeButtonListener = listener;
        }

        public void setSlide(Integer direction, int speed, Float pixel) {
            this.slide = true;
            this.direction = direction;
            this.speed = speed;
            this.pixel = pixel;
        }

        private Dialog build() {
            final Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
            final Context context = activity;
            dialog = new Dialog(context);
            Rect outRect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
            int height = outRect.height();
            int width = outRect.width();
            boolean orientation = false;
            if (direction != null)
                orientation = ((this.direction == 0 || this.direction == 1) ? true : false);
            LinearLayout.LayoutParams pageparams = new LinearLayout.LayoutParams(width, height);
            if (pixel != null)
                pageparams.weight = this.pixel;
            else
                pageparams.weight = 1;
            mDialogView = getDialogView(activity);
            FrameLayout blankframe = new FrameLayout(context);
            blankframe.setLayoutParams(pageparams);
            blankframe.setBackgroundColor(Color.TRANSPARENT);
            blankframe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doing nothing here as we need to prevent dialog from getting dismiss
                }
            });

            LinearLayout dialogViewWrapper = new LinearLayout(context);
            if (orientation) {
                dialogViewWrapper.setOrientation(LinearLayout.VERTICAL);
            }
            dialogViewWrapper.setLayoutParams(pageparams);
            if (direction != null) {
                if (this.direction == 0 || this.direction == 2) {
                    dialogViewWrapper.addView(blankframe);
                    dialogViewWrapper.addView(mDialogView);
                } else {
                    dialogViewWrapper.addView(mDialogView);
                    dialogViewWrapper.addView(blankframe);
                }
            } else {
                dialogViewWrapper.addView(mDialogView);
            }
            dialogViewWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Util.clearStroredMessageParams(context);
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogViewWrapper);
            dialog.setOnShowListener(onShowListener);
            if (webView != null) {
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                        if (onKeyListener != null) {
                            return onKeyListener.onKey(dialog, keyCode, event);
                        }

                        return false;
                    }
                });
            } else {
                dialog.setOnKeyListener(onKeyListener);
            }
            setupSlideAnimation(dialog, mDialogView);
            return dialog;
        }

        private void setupSlideAnimation(Dialog dialog, final View dialogView) {
            if (slide && dialog != null && dialogView != null) {
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        float fromXValue = 0.0f;
                        float fromYValue = 0.0f;

                        if (direction != null) {
                            switch (direction) {
                                case DIRECTION_FROM_BOTTOM_TO_TOP:
                                    fromYValue = 1.0f;
                                    break;
                                case DIRECTION_FROM_TOP_TO_BOTTOM:
                                    fromYValue = -1.0f;
                                    break;
                                case DIRECTION_FROM_RIGHT_TO_LEFT:
                                    fromXValue = 1.0f;
                                    break;
                                case DIRECTION_FROM_LEFT_TO_RIGHT:
                                    fromXValue = -1.0f;
                                    break;
                                default:
                                    fromYValue = 5.0f;
                                    break;
                            }
                        } else {
                            fromYValue = 5.0f;
                        }
                        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromXValue, Animation.RELATIVE_TO_PARENT, 0.0f,
                                Animation.RELATIVE_TO_PARENT, fromYValue, Animation.RELATIVE_TO_PARENT, 0.0f);
                        if (speed != null) {
                            animation.setDuration((long) (1000 * speed));
                        } else {
                            animation.setDuration(500);
                        }
                        dialogView.startAnimation(animation);
                    }
                });
            }
        }

        /*
        private void setupCloseSlideAnimation() {
            if (slide && dialog != null && mDialogView != null) {
                dialog.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        float fromXValue = 0.0f;
                        float fromYValue = 0.0f;
                        if (direction != null) {
                            switch (direction) {
                                case DIRECTION_FROM_BOTTOM_TO_TOP:
                                    fromYValue = 1.0f;
                                    break;
                                case DIRECTION_FROM_TOP_TO_BOTTOM:
                                    fromYValue = -1.0f;
                                    break;
                                case DIRECTION_FROM_RIGHT_TO_LEFT:
                                    fromXValue = 1.0f;
                                    break;
                                case DIRECTION_FROM_LEFT_TO_RIGHT:
                                    fromXValue = -1.0f;
                                    break;
                                default:
                                    fromYValue = 5.0f;
                                    break;
                            }
                        } else {
                            fromYValue = 5.0f;
                        }
                        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0f , Animation.RELATIVE_TO_PARENT, fromXValue,
                                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,fromYValue);
                        animation.setDuration(1000);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if(null!=mDialog){
                                    if(mDialog.isShowing())
                                        mDialog.dismiss();
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mDialogView.startAnimation(animation);
                    }
                });
            }
        }
*/

        @SuppressWarnings("deprecation")
        @SuppressLint("NewApi")
        private View getDialogView(Activity activity) {
            Context context = activity;
            Display display = activity.getWindowManager().getDefaultDisplay();
            int windowHeight;
            int windowWidth;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowHeight = display.getHeight();
                windowWidth = display.getWidth();
            } else {
                Point size = new Point();
                display.getSize(size);
                windowHeight = size.y;
                windowWidth = size.x;
            }
            int height = windowHeight;
            int width = windowWidth;
            if (pixel == null) {
                pixel = 1f;
            }
            if (pixel != null) {
                if (pixel > 1f) {
                    pixel = 1f;
                }
                if (pixel < 0.1f) {
                    pixel = 0.1f;
                }
                if (direction != null) {
                    if (direction.equals(DIRECTION_FROM_BOTTOM_TO_TOP) || direction.equals(DIRECTION_FROM_TOP_TO_BOTTOM)) {
                        height = (int) ((windowHeight - dpToPx(getDialogSpacing())) * pixel);
                        width = (int) (windowWidth);
                    } else if (direction.equals(DIRECTION_FROM_RIGHT_TO_LEFT) || direction.equals(DIRECTION_FROM_LEFT_TO_RIGHT)) {
                        height = (int) (windowHeight - dpToPx(getDialogSpacing()));
                        width = (int) ((windowWidth - dpToPx(getDialogSpacing())) * pixel);
                    } else {
                        // use default
                    }
                } else {
                    // use default
                }
            }
            LinearLayout dialogView = new LinearLayout(context);
            dialogView.setOrientation(LinearLayout.VERTICAL);
            dialogView.setBackgroundDrawable(getDialogBackground());
            dialogView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            dialogView.setClickable(true);
            dialogView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            View webView = getWebView(context);
            //View buttonsView = getButtonsView(context);


            LinearLayout topBarLayout = new LinearLayout(mContext);
            topBarLayout.setOrientation(LinearLayout.HORIZONTAL);

            final String titleStr = mPushData.getTitle();
            final String msgStr = mPushData.getMsg();
            Spanned titleTmp = null;
            Spanned msgTmp = null;
            if(null!=titleStr) {
                if (!titleStr.isEmpty())
                    titleTmp = Html.fromHtml(titleStr);
            }
            if(null!=msgStr) {
                if (!msgStr.isEmpty())
                    msgTmp = Html.fromHtml(msgStr);
            }

            final Spanned title = titleTmp;
            final Spanned msg = msgTmp;

            String scrollMsg = "";
            if (null != title) {
                scrollMsg = title + " : ";
            }
            if (null != msg) {
                scrollMsg = scrollMsg + msg;
            }

            TextView scrollTV = new TextView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            scrollTV.setLayoutParams(params);
            scrollTV.setText(scrollMsg);
            scrollTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            scrollTV.setSingleLine(true);
            scrollTV.setPadding(dpToPx(1), dpToPx(12), dpToPx(1), dpToPx(12));
            scrollTV.setMinHeight(dpToPx(48));
            scrollTV.setMinWidth(dpToPx(64));
            scrollTV.setTextSize(15);
            scrollTV.setHorizontallyScrolling(true);
            scrollTV.setTypeface(null, Typeface.BOLD);
            scrollTV.setMarqueeRepeatLimit(-1);       // MARQUEE_FOREVER
            scrollTV.setSelected(true);

            Button closeButton = new Button(context);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            closeButton.setLayoutParams(params2);
            closeButton.setText(negativeButtonText);
            closeButton.setTextSize(15);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                closeButton.setElevation(1);
                closeButton.setTranslationZ(1);
            }
            closeButton.setTextColor(Color.BLACK);
            closeButton.setGravity(Gravity.CENTER);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeButtonListener != null) {
                        negativeButtonListener.onClick(v);
                    }
                }
            });


            topBarLayout.addView(scrollTV);
            topBarLayout.addView(closeButton);

            dialogView.addView(topBarLayout);
            if (webView != null) {
                dialogView.addView(webView);
            }
            return dialogView;
        }

        @SuppressLint("NewApi")
        private View getWebView(Context context) {
            if (url == null || TextUtils.isEmpty(url.trim())) {
                return null;
            }
            webView = new WebView(context);
            if (null != relativeLayout.getParent())
                ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout);
            webView.setWebViewClient(new StreetHawkWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.loadUrl(url);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            return webView;
        }

        /**
         * Stop loading of webview
         */
        public void stopWebview() {
            if (null != webView)
                webView.stopLoading();
        }

        @SuppressLint("NewApi")
        private void show() {
            final Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog == null)
                            mDialog = new Dialog(activity);
                        mDialog = build();
                        if (mflagBG) {
                            if (mProgressBar.getParent() == relativeLayout)
                                relativeLayout.removeView(mProgressBar);

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                            relativeLayout.setGravity(Gravity.CENTER);
                            relativeLayout.addView(mProgressBar);
                            mProgressBar.setVisibility(View.VISIBLE);
                            if (null == relativeLayout.getParent()) {
                                mDialog.addContentView(relativeLayout, params);
                            }
                            if (mDialog.isShowing())
                                mDialog.dismiss();
                            try {
                                mDialog.show();
                            } catch (Exception e) {
                                Log.e(Util.TAG, "User existed app when dialog was being shown. Dialog will be loaded when app is launched again");
                            }
                        }
                    }
                });
            }
        }

        @SuppressLint("InlinedApi")
        private Drawable getDialogBackground() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return new ColorDrawable(Color.WHITE);
            } else {
                return Resources.getSystem().getDrawable(android.R.drawable.dialog_holo_light_frame);
            }
        }

        private int getDialogSpacing() {
            int defaultDialogSpacing = 20;
            if (dialogSpacing == null) {
                return defaultDialogSpacing;
            } else {
                return dialogSpacing;
            }
        }
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}