package com.streethawk.library.pointzi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class displays modals
 */
public class Modal extends PointziBase {
    public Modal() {
    }

    public View getModalView(final Activity activity, final PointziBase widget) {
        if (null == activity)
            return null;
        if (widget == null)
            return null;
        String title = widget.getTitle();
        String content = widget.getContent();
        String viewBackGroundColor = widget.getViewBackGroundColor();
        int elevation = widget.getViewElevation();
        int viewWidth = getWidth(activity, widget.getViewWidth());
        int viewHeight = getWidth(activity, widget.getViewHeight());

        if (0 == viewWidth) {
            viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (0 == viewHeight) {
            viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        FrameLayout baseFrame = new FrameLayout(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) viewWidth, (int) viewHeight);
        baseFrame.setLayoutParams(params);
        baseFrame.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable drawable = (GradientDrawable) activity.getResources().getDrawable(R.drawable.layout_bg);
            drawable.setColor(Color.parseColor(viewBackGroundColor));
            drawable.setCornerRadius(widget.getViewCornerRadius());
            drawable.setStroke(widget.getViewBorderWidth(), Color.parseColor(widget.getViewBorderColor()));
            baseFrame.setBackground(drawable);
            baseFrame.setElevation(elevation);
        }
        LinearLayout linearBase = new LinearLayout(activity);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linearBase.setOrientation(LinearLayout.VERTICAL);
        linearBase.setLayoutParams(llParams);

        LinearLayout DNDToolBar = new LinearLayout(activity);
        LinearLayout.LayoutParams dndParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        DNDToolBar.setLayoutParams(dndParams);
        DNDToolBar.setOrientation(LinearLayout.HORIZONTAL);
        if (null != title) {
            TextView titleTv = new TextView(activity);
            LinearLayout.LayoutParams titleTVParams = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            String fontFamily = widget.getTitleFontFamily();
            if (null != fontFamily) {
                Typeface typeface = getTypeface(activity, fontFamily);
                if (null != typeface)
                    titleTv.setTypeface(typeface);
            }
            titleTVParams.weight = 1;
            titleTv.setLayoutParams(titleTVParams);
            Spanned spannable = null;
            if (title != null) {
                if (!title.isEmpty())
                    spannable = Html.fromHtml(title/*+'\u00A9'+0x1F601*/);
            }
            titleTv.setText(spannable);
            titleTv.setGravity(getGravity(widget.getTitleGravity()));
            titleTv.setTextSize(widget.getTitleFontSize());
            int[] padding = widget.getTitlePadding();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                titleTv.setElevation(widget.getTitleElevation());
            }
            titleTv.setPadding(padding[0], padding[1], padding[2], padding[3]);
            titleTv.setTextColor(Color.parseColor(widget.getTitleColor()));
            titleTv.setBackgroundColor(Color.parseColor(widget.getTitleBackgroundColor()));
            DNDToolBar.addView(titleTv);
        }
        if (widget.isHasDND()) {
            DNDToolBar.addView(getDNDButtonView(activity, widget, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }));
        }
        linearBase.addView(DNDToolBar);
        LinearLayout contentLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams contentLinearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLinearLayout.setLayoutParams(contentLinearLayoutParams);
        contentLinearLayout.setWeightSum(1);

        if (content != null) {
            TextView contentTv = new TextView(activity);
            LinearLayout.LayoutParams contentTvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            contentTv.setLayoutParams(contentTvParams);
            Spanned spannable = null;
            if (content != null) {
                if (!content.isEmpty())
                    spannable = Html.fromHtml(content/*+'\u00A9'+0x1F601*/);
            }
            contentTv.setText(spannable);
            contentTv.setGravity(getGravity(widget.getContentGravity()));
            contentTv.setTextSize(widget.getContentFontSize());
            int[] padding = widget.getContentPadding();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contentTv.setElevation(widget.getContentElevation());
            }
            String fontFamily = widget.getContentFontFamily();
            if (null != fontFamily) {
                Typeface typeface = getTypeface(activity, fontFamily);
                if (null != typeface)
                    contentTv.setTypeface(typeface);
            }
            contentTv.setPadding(padding[0], padding[1], padding[2], padding[3]);
            contentTv.setTextColor(Color.parseColor(widget.getContentColor()));
            contentTv.setBackgroundColor(Color.parseColor(widget.getContentBackgroundColor()));
            contentLinearLayout.addView(contentTv);
        }
        linearBase.addView(contentLinearLayout);
        String url = widget.getURL();
        if (null != url) {
            LinearLayout webViewPane = new LinearLayout(activity);
            LinearLayout.LayoutParams webViewPaneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            webViewPaneParams.weight = 1;
            webViewPane.setLayoutParams(webViewPaneParams);
            webViewPane.addView(getWebView(activity, url)); //TODO add view after page load
            linearBase.addView(webViewPane);
        }
        String url_content = widget.getURL_Content();
        if (null != url_content) {
            LinearLayout webViewPane = new LinearLayout(activity);
            LinearLayout.LayoutParams webViewPaneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            webViewPaneParams.weight = 1;
            webViewPane.setLayoutParams(webViewPaneParams);
            webViewPane.addView(getUrlContentView(activity, url_content));
            linearBase.addView(webViewPane);
        }
        String nextButtonTitle = widget.getNextButtonTitle();
        String prevButtonTitle = widget.getPrevButtonTitle();
        Button nextButton = null;
        Button prevButton = null;
        if (null != prevButtonTitle) {
            prevButton = new Button(activity);
            prevButton.setText(prevButtonTitle);
            prevButton.setTextColor(Color.parseColor(widget.getPrevButtonTitleColor()));
            int[] padding = widget.getPrevButtonPadding();
            prevButton.setPadding(padding[0], padding[1], padding[2], padding[3]);
            prevButton.setBackgroundColor(Color.parseColor(widget.getPrevButtonBackgroundColor()));
            LinearLayout.LayoutParams prevButtonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            prevButtonParams.weight = 0.5f;
            prevButton.setLayoutParams(prevButtonParams);
            prevButton.setTextSize(widget.getPrevButtonFontSize());
            String fontFamily = widget.getPrevButtonFontFamily();
            if (null != fontFamily) {
                Typeface typeface = getTypeface(activity, fontFamily);
                if (null != typeface)
                    prevButton.setTypeface(typeface);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                prevButton.setElevation(widget.getPrevButtonElevation());
            }
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mClickEventListeners) {
                        boolean result[] = {false, false};
                        mClickEventListeners.onButtonClicked(widget, DISMISS_BUTTON, result);
                    }
                    if (null != widget.getPrevButtonCTA()) {
                        String deeplink_url = widget.getNextButtonCTA();
                        if (null != deeplink_url) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.setData(Uri.parse(deeplink_url));
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
        if (null != nextButtonTitle) {
            nextButton = new Button(activity);
            nextButton.setText(nextButtonTitle);
            LinearLayout.LayoutParams nextButtonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            nextButtonParams.weight = 0.5f;
            nextButton.setLayoutParams(nextButtonParams);
            int[] padding = widget.getNextButtonPadding();
            nextButton.setPadding(padding[0], padding[1], padding[2], padding[3]);
            nextButton.setTextColor(Color.parseColor(widget.getNextButtonTitleColor()));
            nextButton.setBackgroundColor(Color.parseColor(widget.getNextButtonBackgroundColor()));
            nextButton.setTextSize(widget.getNextButtonFontSize());
            String fontFamily = widget.getNextButtonFontFamily();
            if (null != fontFamily) {
                Typeface typeface = getTypeface(activity, fontFamily);
                if (null != typeface)
                    nextButton.setTypeface(typeface);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nextButton.setElevation(widget.getNextButtonElevation());
            }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mClickEventListeners) {
                        boolean result[] = {false, false};
                        mClickEventListeners.onButtonClicked(widget, ACCEPTED_BUTTON, result);
                    }
                    if (null != widget.getNextButtonCTA()) {
                        String deeplink_url = widget.getNextButtonCTA();
                        if (null != deeplink_url) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.setData(Uri.parse(deeplink_url));
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
        LinearLayout buttonBar = new LinearLayout(activity);
        buttonBar.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonBar.setLayoutParams(buttonBarParams);
        buttonBar.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        if (null != prevButton) {
            buttonBar.addView(prevButton);
        }
        if (null != nextButton) {
            buttonBar.addView(nextButton);
        }
        linearBase.addView(buttonBar);
        baseFrame.addView(linearBase);
        return baseFrame;
    }
}