package com.streethawk.library.pointzi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class displays tips
 */
public class Tip extends PointziBase {

    private boolean mSendFeedResultForEachTip = true;

    //TODO impement button pairs
    private String[] getButtonTextFromButtonPairs(String pairName) {
        String[] btnPairs = new String[2];
        //TODO : Add code to implement fetching of button pairs
        //ANURAG REMOVE HARDCODED NAMES
        btnPairs[0] = "Next";
        btnPairs[1] = "Back";
        return btnPairs;
    }

    private View getViewByPosition(int pos, AbsListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private EditText.OnFocusChangeListener etFocusChangeListener(final PopupWindow tipPopUpWindow) {
        return new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (tipPopUpWindow.isShowing()) {
                        tipPopUpWindow.dismiss();
                    }
                }
            }
        };
    }

    private TextWatcher etTextWatcher(final PopupWindow tipPopUpWindow) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tipPopUpWindow.isShowing()) {
                    tipPopUpWindow.dismiss();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }


    public View.OnClickListener DNDButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    public View.OnClickListener prevButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        };
    }

    public View.OnClickListener nextButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        };
    }


    public void showTip_new(final Activity currentActivity, final PointziBase widget, final boolean sendResult) {
        if (null == currentActivity)
            return;
        if (widget == null)
            return;

        View DNDButtonView = getDNDButtonView(currentActivity, widget, DNDButtonOnClickListener());
        View titleView = getTitleView(currentActivity, widget);
        View contentView = getContentView(currentActivity, widget);
        View URLContentView = getUrlContentView(currentActivity, widget.getURL_Content());
        View ButtonContentView = getCTAButtonView(currentActivity, widget, nextButtonOnClickListener(), prevButtonOnClickListener());


    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void showTip(final Activity currentActivity, final PointziBase widget, final boolean sendResult) {
        if (null == currentActivity)
            return;
        if (widget == null)
            return;
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSendFeedResultForEachTip = sendResult;
                final PopupWindow tipPopUpWindow = new PopupWindow(currentActivity);
                if (tipPopUpWindow.isShowing())
                    tipPopUpWindow.dismiss();
                tipPopUpWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                final View tipView = getTipView(currentActivity, widget, tipPopUpWindow);
                if (tipView == null) {
                    return;
                }
                GradientDrawable drawable = (GradientDrawable) currentActivity.getResources().getDrawable(R.drawable.layout_bg);
                String backgroundColor = widget.getViewBackGroundColor();
                drawable.setColor(Color.parseColor(backgroundColor));
                drawable.setCornerRadius(widget.getViewCornerRadius());
                drawable.setStroke(widget.getViewBorderWidth(), Color.parseColor(widget.getViewBorderColor()));
                if (Build.VERSION.SDK_INT > 16) {
                    tipView.setBackground(drawable);
                } else {
                    tipView.setBackgroundDrawable(drawable);
                }
                final View newPointerView = getPointerView(currentActivity, widget);
                if (null == newPointerView) {
                    return;
                }
                newPointerView.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
                final LinearLayout rootView = new LinearLayout(currentActivity);
                LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                rootView.setLayoutParams(rootParams);
                final LinearLayout wrapper = new LinearLayout(currentActivity);
                wrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                int anchorID = getResIdFromWidgetName(currentActivity, widget.getTarget());
                View anchor = currentActivity.findViewById(anchorID);
                int child = widget.getChildNumber();
                Object anchorObject = anchor;
                if ((anchorObject instanceof ListView) || (anchorObject instanceof GridView)
                        || (anchorObject instanceof ExpandableListView)) {
                    AbsListView list = (AbsListView) anchorObject;
                    try {
                        list.smoothScrollToPosition(child);
                        anchor = getViewByPosition(child, list);
                    } catch (NumberFormatException e) {
                        //TODO support listview with text
                    }
                }
                if (anchorObject instanceof EditText) {
                    ((EditText) anchorObject).setOnFocusChangeListener(etFocusChangeListener(tipPopUpWindow));
                    ((EditText) anchorObject).addTextChangedListener(etTextWatcher(tipPopUpWindow));
                }
                final View finalAchor = anchor;
                String placement = widget.getPlacement();
                if (null != placement) {
                    switch (placement) {
                        case BOTTOM:
                        default: {
                            final RelativeLayout pointerWrapper = new RelativeLayout(currentActivity);
                            RelativeLayout.LayoutParams pointerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            pointerWrapper.setLayoutParams(pointerParams);
                            pointerWrapper.setBackgroundColor(Color.TRANSPARENT);
                            wrapper.setOrientation(LinearLayout.VERTICAL);
                            final RelativeLayout pointerWrapperRil = new RelativeLayout(currentActivity);
                            final RelativeLayout.LayoutParams params;
                            params = new RelativeLayout.LayoutParams(50, 50);
                            params.leftMargin = 0;
                            pointerWrapperRil.addView(newPointerView, params);
                            wrapper.addView(pointerWrapper);
                            rootView.addView(wrapper);
                            wrapper.addView(tipView);
                            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            RelativeLayout.LayoutParams paramsB = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_TOP, anchorID);
                            rootView.setLayoutParams(paramsB);
                            tipPopUpWindow.setContentView(rootView);
                            tipPopUpWindow.showAsDropDown(finalAchor, 0, 0);
                            View container;
                            if (Build.VERSION.SDK_INT > 22) {
                                container = (View) tipPopUpWindow.getContentView().getParent().getParent();
                            } else {
                                container = (View) tipPopUpWindow.getContentView().getParent();
                            }
                            final View tempAnchor = finalAchor;
                            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onGlobalLayout() {
                                    int anchorLocation[] = new int[2];
                                    tempAnchor.getLocationOnScreen(anchorLocation);
                                    int popLocation[] = new int[2];
                                    rootView.getLocationOnScreen(popLocation);
                                    tipPopUpWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    float diff = (anchorLocation[0] + (tempAnchor.getWidth() / 2)) - popLocation[0];
                                    if (diff < 0)
                                        diff *= -1;

                                    params.leftMargin = (int) diff;
                                    pointerWrapperRil.removeView(newPointerView);
                                    pointerWrapper.removeView(pointerWrapperRil);
                                    wrapper.removeView(tipView);
                                    wrapper.removeView(pointerWrapper);
                                    rootView.removeView(wrapper);

                                    pointerWrapperRil.addView(newPointerView, params);
                                    pointerWrapper.addView(pointerWrapperRil);
                                    wrapper.addView(pointerWrapper);
                                    rootView.addView(wrapper);
                                    wrapper.addView(tipView);
                                    ViewCompat.setElevation(tipPopUpWindow.getContentView(), widget.getViewElevation());
                                    tipPopUpWindow.update();

                                }
                            });
                        }
                        break;
                        case RIGHT: {
                            final RelativeLayout pointerWrapperRil = new RelativeLayout(currentActivity);
                            final RelativeLayout.LayoutParams params;
                            params = new RelativeLayout.LayoutParams(50, 50);
                            params.topMargin = 0;
                            pointerWrapperRil.addView(newPointerView, params);
                            wrapper.addView(pointerWrapperRil);
                            wrapper.addView(tipView);
                            rootView.addView(wrapper);
                            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            tipPopUpWindow.setContentView(rootView);
                            final int height = finalAchor.getHeight();
                            tipPopUpWindow.showAsDropDown(finalAchor, finalAchor.getWidth(), -(height + height / 2));

                            View container;
                            if (Build.VERSION.SDK_INT > 22) {
                                container = (View) tipPopUpWindow.getContentView().getParent().getParent();
                            } else {
                                container = (View) tipPopUpWindow.getContentView().getParent();
                            }
                            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onGlobalLayout() {
                                    int anchorLocation[] = new int[2];
                                    finalAchor.getLocationOnScreen(anchorLocation);
                                    int popLocation[] = new int[2];
                                    rootView.getLocationOnScreen(popLocation);
                                    tipPopUpWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    float diff = (anchorLocation[1] + (finalAchor.getHeight() / 2)) - popLocation[1];
                                    if (diff < 0)
                                        diff *= -1;

                                    params.topMargin = (int) diff;
                                    pointerWrapperRil.removeView(newPointerView);
                                    wrapper.removeView(pointerWrapperRil);
                                    wrapper.removeView(tipView);
                                    rootView.removeView(wrapper);

                                    pointerWrapperRil.addView(newPointerView, params);
                                    wrapper.addView(pointerWrapperRil);
                                    wrapper.addView(tipView);
                                    rootView.addView(wrapper);
                                    ViewCompat.setElevation(tipPopUpWindow.getContentView(), widget.getViewElevation());
                                    tipPopUpWindow.update();

                                }
                            });
                            break;
                        }
                        case LEFT: {
                            final RelativeLayout pointerWrapperRil = new RelativeLayout(currentActivity);
                            final RelativeLayout.LayoutParams params;
                            params = new RelativeLayout.LayoutParams(50, 50);
                            params.topMargin = 0;
                            pointerWrapperRil.addView(newPointerView, params);
                            wrapper.addView(tipView);
                            wrapper.addView(pointerWrapperRil);
                            rootView.addView(wrapper);
                            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            final int left_height = rootView.getMeasuredHeight();
                            final int left_width = rootView.getMeasuredWidth();
                            tipPopUpWindow.setContentView(rootView);
                            final int height = anchor.getHeight();
                            tipPopUpWindow.showAsDropDown(finalAchor, -1 * left_width, -1 * (height + (height / 2)));
                            View container;
                            if (Build.VERSION.SDK_INT > 22) {
                                container = (View) tipPopUpWindow.getContentView().getParent().getParent();
                            } else {
                                container = (View) tipPopUpWindow.getContentView().getParent();
                            }
                            dimBackground(currentActivity, container, widget.getDim(), widget.getDim_color());

                            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onGlobalLayout() {
                                    int anchorLocation[] = new int[2];
                                    finalAchor.getLocationOnScreen(anchorLocation);
                                    int popLocation[] = new int[2];
                                    rootView.getLocationOnScreen(popLocation);
                                    tipPopUpWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    float diff = (anchorLocation[1] + (finalAchor.getHeight() / 2)) - popLocation[1];
                                    if (diff < 0)
                                        diff *= -1;

                                    params.topMargin = (int) diff;
                                    pointerWrapperRil.removeView(newPointerView);
                                    wrapper.removeView(tipView);
                                    wrapper.removeView(pointerWrapperRil);
                                    rootView.removeView(wrapper);

                                    pointerWrapperRil.addView(newPointerView, params);
                                    wrapper.addView(tipView);
                                    wrapper.addView(pointerWrapperRil);
                                    rootView.addView(wrapper);
                                    ViewCompat.setElevation(tipPopUpWindow.getContentView(), widget.getViewElevation());
                                    tipPopUpWindow.update();

                                }
                            });
                            break;
                        }
                        case TOP: {
                            final RelativeLayout pointerWrapper = new RelativeLayout(currentActivity);
                            RelativeLayout.LayoutParams pointerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            pointerWrapper.setLayoutParams(pointerParams);
                            pointerWrapper.setBackgroundColor(Color.TRANSPARENT);
                            RelativeLayout.LayoutParams paramsT = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_BOTTOM, anchorID);
                            rootView.setLayoutParams(paramsT);
                            wrapper.setOrientation(LinearLayout.VERTICAL);
                            wrapper.setBackgroundColor(Color.TRANSPARENT);
                            final RelativeLayout pointerWrapperRil = new RelativeLayout(currentActivity);
                            final RelativeLayout.LayoutParams params;
                            params = new RelativeLayout.LayoutParams(50, 50);
                            params.leftMargin = 0;
                            pointerWrapperRil.addView(newPointerView, params);
                            pointerWrapper.setBackgroundColor(Color.TRANSPARENT);
                            pointerWrapper.addView(pointerWrapperRil);
                            wrapper.addView(tipView);
                            wrapper.addView(pointerWrapper);
                            rootView.addView(wrapper);
                            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            final int top_height = rootView.getMeasuredHeight();
                            rootView.setBackgroundColor(Color.TRANSPARENT);
                            tipPopUpWindow.setContentView(rootView);
                            tipPopUpWindow.showAsDropDown(finalAchor, 0, -(finalAchor.getHeight() + top_height));
                            View container;
                            if (Build.VERSION.SDK_INT > 22) {
                                container = (View) tipPopUpWindow.getContentView().getParent().getParent();
                            } else {
                                container = (View) tipPopUpWindow.getContentView().getParent();
                            }
                            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onGlobalLayout() {
                                    int anchorLocation[] = new int[2];
                                    finalAchor.getLocationOnScreen(anchorLocation);
                                    int popLocation[] = new int[2];
                                    rootView.getLocationOnScreen(popLocation);
                                    tipPopUpWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    float diff = (anchorLocation[0] + (finalAchor.getWidth() / 2)) - popLocation[0];
                                    if (diff < 0)
                                        diff *= -1;

                                    params.leftMargin = (int) diff;
                                    pointerWrapperRil.removeView(newPointerView);
                                    pointerWrapper.removeView(pointerWrapperRil);
                                    wrapper.removeView(tipView);
                                    wrapper.removeView(pointerWrapper);
                                    rootView.removeView(wrapper);

                                    pointerWrapperRil.addView(newPointerView, params);
                                    pointerWrapper.addView(pointerWrapperRil);
                                    wrapper.addView(tipView);
                                    wrapper.addView(pointerWrapper);
                                    rootView.addView(wrapper);
                                    ViewCompat.setElevation(tipPopUpWindow.getContentView(), widget.getViewElevation());
                                    tipPopUpWindow.update();
                                }
                            });
                            break;
                        }
                    }
                    if (null != widget.isTouch_out()) {
                        if (widget.isTouch_out().equals(TRUE)) {
                            View container = currentActivity.findViewById(android.R.id.content);
                            container.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (tipPopUpWindow.isShowing())
                                        tipPopUpWindow.dismiss();
                                }
                            });
                        }
                    }
                    if (null != widget.isTouch_in()) {
                        if (widget.isTouch_in().equals(TRUE)) {
                            tipPopUpWindow.getContentView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (tipPopUpWindow.isShowing())
                                        tipPopUpWindow.dismiss();
                                }
                            });

                        }
                    }
                }
            }
        });
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private View getPointerView(Activity activity, PointziBase widget) {
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
            String backGroundColor = widget.getViewBorderColor();
            pointer.setFillColor(backGroundColor);
            pointer.setBorderColor(backGroundColor);
            return pointer;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  //Anurag change min SDK to 11
    private View getTipView(final Activity activity, final PointziBase widget, final PopupWindow tipPopUpWindow) {
        if (null == activity)
            return null;

        String backGroundColor = widget.getViewBackGroundColor();
        String titleColor = widget.getTitleColor();
        String contentColor = widget.getContentColor();
        Context context = activity.getApplicationContext();
        final LinearLayout rootView = new LinearLayout(context);

        int viewWidth = getWidth(activity, widget.getViewWidth());
        int viewHeight = getWidth(activity, widget.getViewHeight());

        if (0 == viewWidth) {
            viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (0 == viewHeight) {
            viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(viewWidth, viewHeight);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(rootParams);
        rootView.setBackgroundColor(Color.parseColor(backGroundColor));

        //Title Bar
        final LinearLayout LL_titleBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_titleBar.setOrientation(LinearLayout.VERTICAL);
        LL_titleBar.setLayoutParams(LL_titleParams);

        LinearLayout LL_title_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_title_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = widget.getViewBorderWidth();
        LL_title_horizontalParams.setMargins(width, width, width, width);
        LL_title_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_title_horizontal.setLayoutParams(LL_title_horizontalParams);
        LL_title_horizontal.setBackgroundColor(Color.parseColor(widget.getTitleBackgroundColor()));
        String title = widget.getTitle();
        if (null != title) {
            //Add title here
            if (!title.isEmpty()) {
                TextView titletv = new TextView(context);
                String fontFamily = widget.getTitleFontFamily();
                if (null != fontFamily) {
                    Typeface typeface = getTypeface(activity, fontFamily);
                    if (null != typeface) {
                        titletv.setTypeface(typeface);
                    }
                }
                int[] padding = widget.getTitlePadding();
                titletv.setPadding(padding[0], padding[1], padding[2], padding[3]);
                titletv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Spanned spannable = null;
                if (title != null) {
                    if (!title.isEmpty())
                        spannable = Html.fromHtml(title/*+'\u00A9'+0x1F601*/);
                }
                titletv.setText(spannable);
                titletv.setTextColor(Color.parseColor(titleColor));
                LL_title_horizontal.addView(titletv);
            }
        }
        LL_titleBar.addView(LL_title_horizontal);
        //MessageBar
        final LinearLayout LL_MessageBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_MessageBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_MessageBar.setOrientation(LinearLayout.VERTICAL);
        LL_MessageBarParams.setMargins(width, width, width, width);
        LL_MessageBar.setLayoutParams(LL_MessageBarParams);
        LL_MessageBar.setPadding(1, 1, 1, 1);
        LL_MessageBar.setBackgroundColor(Color.parseColor(widget.getContentBackgroundColor()));
        String content = widget.getContent();
        if (null != content) {
            if (!content.isEmpty()) {
                TextView messageTv = new TextView(context);
                int[] padding = widget.getTitlePadding();
                String fontFamily = widget.getTitleFontFamily();
                if (null != fontFamily) {
                    Typeface typeface = getTypeface(activity, fontFamily);
                    if (null != typeface)
                        messageTv.setTypeface(typeface);
                }
                messageTv.setPadding(padding[0], padding[1], padding[2], padding[3]);
                messageTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                messageTv.setGravity(getGravity(widget.getContentGravity()));
                Spanned spannable = null;
                if (content != null) {
                    if (!content.isEmpty())
                        spannable = Html.fromHtml(content/*+'\u00A9'+0x1F601*/);
                }
                messageTv.setText(spannable);
                messageTv.setTextColor(Color.parseColor(contentColor));
                LL_MessageBar.addView(messageTv);
            }
        }
        LinearLayout webViewPane = new LinearLayout(activity);

        String url_content = widget.getURL_Content();
        if (null != url_content) {
            LinearLayout.LayoutParams webViewPaneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            webViewPaneParams.weight = 1;
            webViewPane.setLayoutParams(webViewPaneParams);
            webViewPane.addView(getUrlContentView(activity, url_content));
        }

        //Buttons
        final LinearLayout LL_ButtonBar = new LinearLayout(context);
        LinearLayout.LayoutParams LL_ButtonBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_ButtonBar.setOrientation(LinearLayout.VERTICAL);
        LL_ButtonBar.setLayoutParams(LL_ButtonBarParams);
        LL_ButtonBar.setPadding(1, 1, 1, 1);

        LinearLayout LL_Button_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_Button_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_Button_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_Button_horizontal.setLayoutParams(LL_Button_horizontalParams);
        LL_Button_horizontal.setGravity(Gravity.RIGHT);

        String declineButtonTitle = widget.getPrevButtonTitle();
        if (null != declineButtonTitle) {
            if (!declineButtonTitle.isEmpty()) {

                int resId = getResIdFromButtonName(declineButtonTitle);
                if (-1 != resId) {
                    //Create image button here
                    ImageButton imgBtn = new ImageButton(context);
                    int[] padding = widget.getPrevButtonPadding();
                    imgBtn.setPadding(padding[0], padding[1], padding[2], padding[3]);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(width, width, width, width);
                    imgBtn.setLayoutParams(params);
                    imgBtn.setBackgroundColor(Color.parseColor(widget.getPrevButtonBackgroundColor()));
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tipPopUpWindow.dismiss();
                            if (null != mClickEventListeners) {
                                boolean[] results = {false, false};
                                mClickEventListeners.onButtonClicked(widget, DISMISS_BUTTON, results);
                            }
                        }
                    });
                    LL_ButtonBar.setPadding(1, 1, 1, 1);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(context);
                    int[] padding = widget.getPrevButtonPadding();
                    btn.setPadding(padding[0], padding[1], padding[2], padding[3]);
                    btn.setBackgroundColor(Color.parseColor(backGroundColor));
                    btn.setTextColor(Color.parseColor(widget.getPrevButtonTitleColor()));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(width, width, width, width);
                    params.weight = 1;
                    btn.setLayoutParams(params);
                    btn.setTextSize(widget.getPrevButtonFontSize());
                    btn.setText(declineButtonTitle);
                    String fontFamily = widget.getPrevButtonFontFamily();
                    if (null != fontFamily) {
                        Typeface typeface = getTypeface(activity, fontFamily);
                        if (null != typeface)
                            btn.setTypeface(typeface);
                    }
                    btn.setBackgroundColor(Color.parseColor(widget.getPrevButtonBackgroundColor()));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tipPopUpWindow.dismiss();
                            if (null != mClickEventListeners) {
                                boolean[] results = {false, false};
                                mClickEventListeners.onButtonClicked(widget, DISMISS_BUTTON, results);
                            }
                        }
                    });
                    LL_Button_horizontal.addView(btn);
                }
            }
        }
        String accpetedButtonTitle = widget.getNextButtonTitle();
        if (null != accpetedButtonTitle) {
            if (!accpetedButtonTitle.isEmpty()) {
                int resId = getResIdFromButtonName(accpetedButtonTitle);
                if (-1 != resId) {
                    //Create image button here
                    ImageButton imgBtn = new ImageButton(context);
                    int[] padding = widget.getNextButtonPadding();
                    imgBtn.setPadding(padding[0], padding[1], padding[2], padding[3]);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(width, width, width, width);
                    imgBtn.setLayoutParams(params);
                    imgBtn.setBackgroundColor(Color.parseColor(widget.getNextButtonBackgroundColor()));
                    imgBtn.setBackgroundResource(resId);
                    imgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tipPopUpWindow.dismiss();
                            if (null != mClickEventListeners) {
                                final boolean[] result = {false, false};
                                mClickEventListeners.onButtonClicked(widget, ACCEPTED_BUTTON, result);
                            }
                        }
                    });
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(context);
                    int[] padding = widget.getNextButtonPadding();
                    btn.setPadding(padding[0], padding[1], padding[2], padding[3]);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(width, width, width, width);
                    params.weight = 1;
                    btn.setTextSize(widget.getNextButtonFontSize());
                    btn.setLayoutParams(params);
                    btn.setBackgroundColor(Color.parseColor(backGroundColor));
                    btn.setTextColor(Color.parseColor(widget.getNextButtonTitleColor()));
                    btn.setText(accpetedButtonTitle);
                    btn.setBackgroundColor(Color.parseColor(widget.getNextButtonBackgroundColor()));
                    String fontFamily = widget.getNextButtonFontFamily();
                    if (null != fontFamily) {
                        Typeface typeface = getTypeface(activity, fontFamily);
                        if (null != typeface)
                            btn.setTypeface(typeface);
                    }
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tipPopUpWindow.dismiss();
                            if (null != mClickEventListeners) {
                                final boolean[] result = {false, false};
                                mClickEventListeners.onButtonClicked(widget, ACCEPTED_BUTTON, result);
                            }
                        }
                    });


                    LL_Button_horizontal.addView(btn);
                }
            }
        }

        LL_ButtonBar.addView(LL_Button_horizontal);
        final LinearLayout DNDBar = new LinearLayout(activity);
        LinearLayout.LayoutParams DNDBarParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        DNDBar.setLayoutParams(DNDBarParams);
        DNDBar.setGravity(Gravity.RIGHT);
        DNDBar.addView(getDNDButtonView(activity, widget, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tipPopUpWindow.dismiss();
                        boolean[] result = {false, false};
                        if (mSendFeedResultForEachTip) {
                            dismissButtonOnClickListener(activity, widget, result);
                        } else {
                            doNothing();
                        }
                    }
                }

        ));
        rootView.addView(DNDBar);
        rootView.addView(LL_titleBar);
        rootView.addView(LL_MessageBar);
        rootView.addView(webViewPane);
        String url = widget.getURL();
        if (null != url) {
            LinearLayout webViewPaneLL = new LinearLayout(activity);
            LinearLayout.LayoutParams webViewPaneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            webViewPaneParams.weight = 1;
            webViewPane.setLayoutParams(webViewPaneParams);
            webViewPaneLL.addView(getWebView(activity, url));
            rootView.addView(webViewPaneLL);
        }
        rootView.addView(LL_ButtonBar);
        return rootView;
    }
}