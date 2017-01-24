package com.streethawk.library.pointzi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player extends PointziBase implements WidgetClickListener, Constants {

    private static ArrayList<PointziBase> playList = null;
    private static int stepCounter = -1;
    private Activity mActivity;
    private static boolean FLAG_IN_TOUR = false;
    private FrameLayout baseFrame = null;

    public Player() {
    }

    private void parsePayloadToPointziBase(PointziBase object, JSONObject src) {
        if (null == object)
            return;
        if (src == null)
            return;

        JSONObject meta = null;
        try {
            meta = src.getJSONObject(META);
        } catch (JSONException e1) {
            Log.d(TAG, SUBTAG + "Exception in meta, returning");
            return;
        }
        try {
            object.setID(meta.getString(ID));
        } catch (JSONException e1) {
            Log.d(TAG, SUBTAG + "ID, returning");
            return;
        }
        try {
            object.setTarget(meta.getString(TARGET));
        } catch (JSONException e1) {
            object.setTarget(null);
        }
        try {
            object.setView(meta.getString(VIEW));
        } catch (JSONException e1) {
            object.setView(null);   //TODO set as Launcher function
        }
        try {
            object.setViewBackGroundColor(meta.getString(BACKGROUND_COLOR));
        } catch (JSONException e1) {
            object.setViewBackGroundColor(null);
        }
        try {
            JSONArray childArray = meta.getJSONArray(CHILD);
            int length = childArray.length();
            if (length > 0) {
                int child = childArray.getInt(0);
                object.setChildNumber(child);
            }
        } catch (JSONException e1) {
            object.setChildNumber(-1);
        }
        try {
            object.setPlacement(meta.getString(PLACEMENT));
        } catch (JSONException e1) {
            object.setPlacement(null);
        }
        try {
            object.setWidgetType(meta.getString(TOOL));
        } catch (JSONException e1) {
            object.setWidgetType(MODAL);
        }
        try {
            object.setURL(meta.getString(URL));
        } catch (JSONException e1) {
            object.setURL(null);
        }
        try {
            object.setURL(meta.getString(URL));
        } catch (JSONException e1) {
            object.setURL(null);
        }


        try {
            object.setViewElevation(meta.getInt(Z_INDEX));
        } catch (JSONException e1) {
            object.setViewElevation(DEFAULT_Z_INDEX);
        }
        try {
            object.setTemplateCode(meta.getInt(TEMPLATE));
        } catch (JSONException e1) {
            object.setTemplateCode(-1);
        }
        try {
            object.setDelay(meta.getInt(DELAY));
        } catch (JSONException e1) {
            object.setDelay(0);
        }

        int[] padding = new int[4];
        try {
            padding[0] = meta.getInt(PADDING_LEFT);
        } catch (JSONException e1) {
            padding[0] = DEFAULT_PADDING;
        }
        try {
            padding[1] = meta.getInt(PADDING_TOP);
        } catch (JSONException e1) {
            padding[1] = DEFAULT_PADDING;
        }
        try {
            padding[2] = meta.getInt(PADDING_RIGHT);
        } catch (JSONException e1) {
            padding[1] = DEFAULT_PADDING;
        }
        try {
            padding[3] = meta.getInt(PADDING_BOTTOM);
        } catch (JSONException e1) {
            padding[3] = DEFAULT_PADDING;
        }
        object.setViewPadding(padding);

        JSONObject displayParams = null;
        try {
            displayParams = meta.getJSONObject(DISPLAY_PARAMS);
        } catch (JSONException e1) {
            displayParams = null;
        }
        if (null != displayParams) {
            try {
                object.setViewBorderColor(displayParams.getString(BORDER_COLOR));
            } catch (JSONException e) {
                object.setViewBorderColor(object.getViewBorderColor());
            }
            try {
                object.setViewBorderWidth(displayParams.getInt(BORDER_WIDTH));
            } catch (JSONException e) {
                object.setViewBorderWidth(0);
            }
            try {
                object.setViewCornerRadius(displayParams.getInt(CORNER_RADIUS));
            } catch (JSONException e) {
                object.setViewCornerRadius(0);
            }
            try {
                object.setViewPercentage(displayParams.getInt(PERCENTAGE));
            } catch (JSONException e) {
                object.setViewPercentage(0);
            }
            try {
                object.setViewWidth(displayParams.getString(WIDTH));
            } catch (JSONException e) {
                object.setViewWidth(null);
            }
            try {
                object.setViewHeight(displayParams.getString(HEIGHT));
            } catch (JSONException e) {
                object.setViewHeight(null);
            }
            try {
                object.setDim(displayParams.getInt(DIM));
            } catch (JSONException e) {
                object.setDim(0);
            }
            try {
                object.setDim_color(displayParams.getString(COLOR));
            } catch (JSONException e) {
                object.setDim_color(null);
            }
        }
        JSONObject dismissParams = null;
        try {
            dismissParams = meta.getJSONObject(DISMISS_PARAMS);
        } catch (JSONException e1) {
            dismissParams = null;
        }
        if (null != dismissParams) {
            String touch_in = FALSE;
            try {
                touch_in = dismissParams.getString(TOUCH_IN);
            } catch (JSONException e) {
                touch_in = FALSE;
            }
            object.setTouch_in(touch_in);

            String touch_out = FALSE;
            try {
                touch_out = dismissParams.getString(TOUCH_OUT);
            } catch (JSONException e) {
                touch_out = FALSE;
            }
            object.setTouch_out(touch_out);
        }

        //TODO Add animation in release 2

        JSONObject title = null;
        try {
            title = src.getJSONObject(TITLE);
        } catch (JSONException e) {
            title = null;
        }
        if (null != title) {
            try {
                object.setTitle(title.getString(TEXT));
            } catch (JSONException e) {
                title = null;
            }
            try {
                object.setTitleColor(title.getString(COLOR));
            } catch (JSONException e) {
                object.setTitleColor(null);
            }
            try {
                object.setTitleBackgroundColor(title.getString(BACKGROUND_COLOR));
            } catch (JSONException e) {
                object.setTitleBackgroundColor(object.getViewBackGroundColor());
            }
            try {
                object.setTitleGravity(title.getString(TEXT_ALIGH));
            } catch (JSONException e) {
                object.setTitleGravity(DEFAULT_GRAVITY);
            }

            int[] titlePadding = new int[4];
            try {
                titlePadding[0] = title.getInt(PADDING_LEFT);
            } catch (JSONException e1) {
                titlePadding[0] = DEFAULT_PADDING;
            }
            try {
                titlePadding[1] = title.getInt(PADDING_TOP);
            } catch (JSONException e1) {
                titlePadding[1] = DEFAULT_PADDING;
            }
            try {
                titlePadding[2] = title.getInt(PADDING_RIGHT);
            } catch (JSONException e1) {
                titlePadding[1] = DEFAULT_PADDING;
            }
            try {
                titlePadding[3] = title.getInt(PADDING_BOTTOM);
            } catch (JSONException e1) {
                titlePadding[3] = DEFAULT_PADDING;
            }
            object.setTitlePadding(titlePadding);
            try {
                object.setTitleFontFamily(title.getString(FONT_FAMILY));
            } catch (JSONException e) {
                object.setTitleFontFamily(null);
            }
            try {
                object.setTitleFontSize(title.getInt(FONT_SIZE));
            } catch (JSONException e) {
                object.setTitleFontSize(DEFAULT_FONT_SIZE);
            }
            try {
                object.setTitleElevation(title.getInt(Z_INDEX));
            } catch (JSONException e) {
                object.setTitleElevation(DEFAULT_Z_INDEX);
            }
        }

        JSONObject content = null;
        try {
            content = src.getJSONObject(CONTENT);
        } catch (JSONException e) {
            content = null;
        }
        if (null != content) {
            try {
                object.setContent(content.getString(TEXT));
            } catch (JSONException e) {
                content = null;
            }
            try {
                object.setContentColor(content.getString(COLOR));
            } catch (JSONException e) {
                object.setContentColor(null);
            }
            try {
                object.setContentBackgroundColor(content.getString(BACKGROUND_COLOR));
            } catch (JSONException e) {
                object.setContentBackgroundColor(object.getViewBackGroundColor());
            }
            try {
                object.setContentGravity(content.getString(TEXT_ALIGH));
            } catch (JSONException e) {
                object.setContentGravity(DEFAULT_GRAVITY);
            }

            int[] contentPadding = new int[4];
            try {
                contentPadding[0] = content.getInt(PADDING_LEFT);
            } catch (JSONException e1) {
                contentPadding[0] = DEFAULT_PADDING;
            }
            try {
                contentPadding[1] = content.getInt(PADDING_TOP);
            } catch (JSONException e1) {
                contentPadding[1] = DEFAULT_PADDING;
            }
            try {
                contentPadding[2] = content.getInt(PADDING_RIGHT);
            } catch (JSONException e1) {
                contentPadding[1] = DEFAULT_PADDING;
            }
            try {
                contentPadding[3] = content.getInt(PADDING_BOTTOM);
            } catch (JSONException e1) {
                contentPadding[3] = DEFAULT_PADDING;
            }
            object.setContentPadding(contentPadding);
            try {
                object.setContentFontFamily(content.getString(FONT_FAMILY));
            } catch (JSONException e) {
                object.setContentFontFamily(null);
            }
            try {
                object.setContentFontSize(content.getInt(FONT_SIZE));
            } catch (JSONException e) {
                object.setContentFontSize(DEFAULT_FONT_SIZE);
            }
            try {
                object.setContentElevation(content.getInt(Z_INDEX));
            } catch (JSONException e) {
                object.setContentElevation(DEFAULT_Z_INDEX);
            }
        }

        JSONObject button = null;
        try {
            button = src.getJSONObject(BUTTONS);
        } catch (JSONException e) {
            button = null;
        }
        if (null != button) {
            try {
                object.setButtonPair(button.getString(PAIR));
            } catch (JSONException e) {
                object.setButtonPair(null);
            }
            JSONObject next = null;
            try {
                next = button.getJSONObject(NEXT);
            } catch (JSONException e) {
                next = null;
            }
            if (null != next) {
                try {
                    object.setNextButtonTitle(next.getString(TEXT));
                } catch (JSONException e) {
                    object.setNextButtonTitle(null);
                }
                try {
                    object.setNextButtonTitleColor(next.getString(COLOR));
                } catch (JSONException e) {
                    object.setNextButtonTitleColor(null);
                }
                try {
                    object.setNextButtonBackgroundColor(next.getString(BACKGROUND_COLOR));
                } catch (JSONException e) {
                    object.setNextButtonBackgroundColor(object.getViewBackGroundColor());
                }
                int[] nextPadding = new int[4];
                try {
                    nextPadding[0] = next.getInt(PADDING_LEFT);
                } catch (JSONException e1) {
                    nextPadding[0] = DEFAULT_PADDING;
                }
                try {
                    nextPadding[1] = next.getInt(PADDING_TOP);
                } catch (JSONException e1) {
                    nextPadding[1] = DEFAULT_PADDING;
                }
                try {
                    nextPadding[2] = next.getInt(PADDING_RIGHT);
                } catch (JSONException e1) {
                    nextPadding[1] = DEFAULT_PADDING;
                }
                try {
                    nextPadding[3] = next.getInt(PADDING_BOTTOM);
                } catch (JSONException e1) {
                    nextPadding[3] = DEFAULT_PADDING;
                }
                object.setNextButtonPadding(nextPadding);
                try {
                    object.setNextButtonFontFamily(next.getString(FONT_FAMILY));
                } catch (JSONException e) {
                    object.setNextButtonFontFamily(null);
                }
                try {
                    object.setNextButtonFontSize(next.getInt(FONT_SIZE));
                } catch (JSONException e) {
                    object.setNextButtonFontSize(DEFAULT_FONT_SIZE);
                }
                try {
                    object.setNextButtonGravity(next.getString(TEXT_ALIGH));
                } catch (JSONException e) {
                    object.setNextButtonGravity(DEFAULT_GRAVITY);
                }
                try {
                    object.setNextButtonElevation(next.getInt(Z_INDEX));
                } catch (JSONException e) {
                    object.setNextButtonElevation(DEFAULT_Z_INDEX);
                }
                try {
                    object.setNextButtonCTA(next.getString(CTA));
                } catch (JSONException e) {
                    object.setNextButtonCTA(null);
                }

            }
            JSONObject prev = null;
            try {
                prev = button.getJSONObject(PREV);
            } catch (JSONException e) {
                prev = null;
            }
            if (null != prev) {
                try {
                    object.setPrevButtonTitle(prev.getString(TEXT));
                } catch (JSONException e) {
                    object.setPrevButtonTitle(null);
                }
                try {
                    object.setPrevButtonTitleColor(prev.getString(COLOR));
                } catch (JSONException e) {
                    object.setPrevButtonTitleColor(null);
                }
                try {
                    object.setPrevButtonBackgroundColor(prev.getString(BACKGROUND_COLOR));
                } catch (JSONException e) {
                    object.setPrevButtonBackgroundColor(object.getViewBackGroundColor());
                }
                int[] prevPadding = new int[4];
                try {
                    prevPadding[0] = prev.getInt(PADDING_LEFT);
                } catch (JSONException e1) {
                    prevPadding[0] = DEFAULT_PADDING;
                }
                try {
                    prevPadding[1] = prev.getInt(PADDING_TOP);
                } catch (JSONException e1) {
                    prevPadding[1] = DEFAULT_PADDING;
                }
                try {
                    prevPadding[2] = prev.getInt(PADDING_RIGHT);
                } catch (JSONException e1) {
                    prevPadding[1] = DEFAULT_PADDING;
                }
                try {
                    prevPadding[3] = prev.getInt(PADDING_BOTTOM);
                } catch (JSONException e1) {
                    prevPadding[3] = DEFAULT_PADDING;
                }
                object.setPrevButtonPadding(prevPadding);
                try {
                    object.setPrevButtonFontFamily(prev.getString(FONT_FAMILY));
                } catch (JSONException e) {
                    object.setPrevButtonFontFamily(null);
                }
                try {
                    object.setPrevButtonFontSize(prev.getInt(FONT_SIZE));
                } catch (JSONException e) {
                    object.setPrevButtonFontSize(DEFAULT_FONT_SIZE);
                }
                try {
                    object.setPrevButtonGravity(prev.getString(TEXT_ALIGH));
                } catch (JSONException e) {
                    object.setPrevButtonGravity(DEFAULT_GRAVITY);
                }
                try {
                    object.setPrevButtonElevation(prev.getInt(Z_INDEX));
                } catch (JSONException e) {
                    object.setPrevButtonElevation(DEFAULT_Z_INDEX);
                }
                try {
                    object.setPrevButtonCTA(prev.getString(CTA));
                } catch (JSONException e) {
                    object.setPrevButtonCTA(null);
                }

            }
            JSONObject dismiss = null;
            try {
                dismiss = button.getJSONObject(DISMISS);
            } catch (JSONException e) {
                dismiss = null;
            }
            if (null != dismiss) {
                object.setHasDND(true);
                try {
                    object.setDND_Title(dismiss.getString(TITLE));
                } catch (JSONException e) {
                    object.setDND_Title(null);
                }
                try {
                    object.setDND_Content(dismiss.getString(CONTENT));
                } catch (JSONException e) {
                    object.setDND_Content(null);
                }
                try {
                    object.setDND_Next_Button_title(dismiss.getString(B1));
                } catch (JSONException e) {
                    object.setDND_Next_Button_title(null);
                }
                try {
                    object.setDND_Prev_Button_title(dismiss.getString(B2));
                } catch (JSONException e) {
                    object.setDND_Prev_Button_title(null);
                }
                try {
                    object.setDND_Button_ID(dismiss.getString(BUTTONS));
                } catch (JSONException e) {
                    object.setDND_Button_ID(null);
                }
                try {
                    object.setDND_Height(dismiss.getInt(HEIGHT));
                } catch (JSONException e) {
                    object.setDND_Height(0);
                }
                try {
                    object.setDND_Widht(dismiss.getInt(WIDTH));
                } catch (JSONException e) {
                    object.setDND_Widht(0);
                }
                int[] dndPadding = new int[4];
                try {
                    dndPadding[0] = dismiss.getInt(PADDING_LEFT);
                } catch (JSONException e1) {
                    dndPadding[0] = DEFAULT_PADDING;
                }
                try {
                    dndPadding[1] = dismiss.getInt(PADDING_TOP);
                } catch (JSONException e1) {
                    dndPadding[1] = DEFAULT_PADDING;
                }
                try {
                    dndPadding[2] = dismiss.getInt(PADDING_RIGHT);
                } catch (JSONException e1) {
                    dndPadding[1] = DEFAULT_PADDING;
                }
                try {
                    dndPadding[3] = dismiss.getInt(PADDING_BOTTOM);
                } catch (JSONException e1) {
                    dndPadding[3] = DEFAULT_PADDING;
                }
                object.setDND_Padding(dndPadding);
                try {
                    object.setDND_Button_color(dismiss.getString(COLOR));
                } catch (JSONException e) {
                    object.setDND_Button_color(null);
                }
                try {
                    object.setDND_Button_Background_Color(dismiss.getString(BACKGROUND_COLOR));
                } catch (JSONException e) {
                    object.setDND_Button_Background_Color(null);
                }
            }
        }
    }


    private void populatePlaylist(JSONArray raw) {
        if (null == raw)
            return;
        if (raw.length() > 0) {
            if (null == playList) {
                playList = new ArrayList<PointziBase>();
            } else {
                playList.clear();
            }
            for (int i = 0; i < raw.length(); i++) {
                try {
                    JSONObject obj = raw.getJSONObject(i);
                    PointziBase pointziBase = new PointziBase();
                    parsePayloadToPointziBase(pointziBase, obj);
                    playList.add(pointziBase);
                } catch (JSONException e) {

                }
            }

        }

    }

    private void drawTriggerAndPlay(final Trigger trigger) {
        if (null == trigger) {
            return;
        }
        String tool = trigger.getTool();
        if (null == tool) {
            return;
        }
        JSONObject launcherJSON = null;
        String fileName = null;
        int width;
        int height;
        String bgColor = null;
        try {
            launcherJSON = new JSONObject(trigger.getLauncherJSON());
        } catch (JSONException e) {
            e.printStackTrace();
            launcherJSON = null;
        }
        if (null == launcherJSON)
            return;
        try {
            fileName = launcherJSON.getString(FILE_NAME);
        } catch (JSONException e) {
            fileName = null;
        }
        try {
            width = launcherJSON.getInt(WIDTH);
        } catch (JSONException e) {
            width = 0;
        }
        try {
            height = launcherJSON.getInt(HEIGHT);
        } catch (JSONException e) {
            height = 0;
        }
        try {
            bgColor = launcherJSON.getString(BACKGROUND_COLOR);
        } catch (JSONException e) {
            bgColor = null;
        }
        if (null != fileName) {
            final PopupWindow triggerDisplayView = new PopupWindow(mActivity);
            int anchor_resId = getResIdFromWidgetName(mActivity, trigger.getTarget());
            final View anchor = mActivity.findViewById(anchor_resId);
            final ImageButton crossButton = new ImageButton(mActivity);
            crossButton.setBackgroundColor(Color.parseColor(bgColor));
            if (0 == width || 0 == height) {
                crossButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                crossButton.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            }
            int drawableResourceId = mActivity.getResources().getIdentifier(fileName, "drawable", mActivity.getPackageName());
            Drawable drawable = null;
            if (-1 == anchor_resId) {
                //TODO create a floating button
            } else {
                if (-1 == drawableResourceId) {
                    drawable = VectorDrawableCompat.create(mActivity.getResources(),
                            R.drawable.radio, mActivity.getTheme());
                } else {
                    drawable = VectorDrawableCompat.create(mActivity.getResources(),
                            drawableResourceId, mActivity.getTheme());
                }
                crossButton.setImageDrawable(drawable);
                crossButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        triggerDisplayView.dismiss();
                        play(trigger);
                        //TODO : direct cta
                    }
                });

                triggerDisplayView.setContentView(crossButton);
                String placement = LEFT;
                try {
                    JSONObject launcher = new JSONObject(trigger.getLauncherJSON());
                    placement = launcher.getString(PLACEMENT);
                } catch (JSONException e) {
                    placement = LEFT;
                }
                int placement_width = 0;
                int placement_height = 0;
                int ht = 0;
                switch (placement) {
                    case LEFT:
                        placement_width = -1 * crossButton.getWidth();
                        ht = anchor.getHeight();
                        placement_height = -1 * (ht + (ht / 2));
                        break;
                    case RIGHT:
                        ht = anchor.getHeight();
                        placement_height = -1 * (ht + ht / 2);
                        placement_width = anchor.getMeasuredWidth();
                        break;
                    case TOP:
                        placement_width = crossButton.getHeight();
                        placement_height = anchor.getMeasuredWidth();
                        break;
                    default:
                    case BOTTOM:
                        placement_width = 0;
                        placement_height = 0;
                        break;

                }
                final int final_placement_width = placement_width;
                final int final_placement_height = placement_height;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        triggerDisplayView.showAsDropDown(anchor, final_placement_width, final_placement_height);
                    }
                });
            }


        } else {
            //TODO draw a floating button here
            // Create a common class for floating button as this will be used by authoring as well.

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void play(Trigger trigger) {
        stepCounter = -1;
        if (null == trigger) {
            return;
        }
        String tool = trigger.getTool();
        if (null == tool) {
            return;
        }

        try {
            JSONArray array = new JSONArray(trigger.getJSON());
            if (array.length() > 0) {
                switch (tool) {
                    case MODAL:
                    case COUCH_MARKS:
                    case TIP: {
                        final PointziBase widget = new PointziBase();
                        parsePayloadToPointziBase(widget, array.getJSONObject(0));
                        widget.setFeedID(trigger.getFeedID());
                        play(widget);
                        break;
                    }
                    case TOUR: {
                        populatePlaylist(array);
                        if (playList != null) {
                            if (playList.size() > 0) {
                                stepCounter = 0;
                                FLAG_IN_TOUR = true;
                                final PointziBase widget = playList.get(0);
                                widget.setFeedID(trigger.getFeedID());
                                play(widget);
                            }
                        }
                    }
                    break;
                    default:
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void play(final PointziBase widget) {
        if (null == widget) {
            return;
        }
        PointziBase.setWidgetClickListener(this);
        String tool = widget.getWidgetType();
        switch (tool) {
            case MODAL:
                final RelativeLayout baseLayout = new RelativeLayout(mActivity);
                RelativeLayout.LayoutParams baseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                baseLayout.setLayoutParams(baseParams);
                baseLayout.setBackgroundColor(Color.TRANSPARENT);
                baseFrame = new FrameLayout(mActivity);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup parent = (ViewGroup) mActivity.findViewById(android.R.id.content);
                        View view = new Modal().getModalView(mActivity, widget);
                        if (null != view) {
                            RelativeLayout.LayoutParams baseFrameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            baseFrameParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            baseFrame.setLayoutParams(baseFrameParams);
                            baseFrame.addView(view);
                            baseLayout.addView(baseFrame);
                            parent.addView(baseLayout);
                            parent.invalidate();
                            View container = mActivity.findViewById(android.R.id.content);
                            //dimBackground(mActivity, container, widget.getDim(), widget.getDim_color());
                        }
                    }
                });
                break;
            case TIP:
                Tip tip = new Tip();
                tip.showTip(mActivity, widget, true);  //TODO remove hardcoded true
                break;
            case COUCH_MARKS:
                break;
            default:
                break;
        }
    }

    /**
     * Function displays a trigger on the basis of click on target element
     *
     * @param trigger
     */
    private void runTriggerClick(final Trigger trigger) {
        String target = trigger.getTarget();
        if (null != target) {
            int resId = getResIdFromButtonName(target);
            View view = mActivity.findViewById(resId);

            if (null != view) {
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            play(trigger);
                        }
                    }
                });
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void displayTrigger(Trigger trigger) {
        if (null == trigger)
            return;
        Log.e("Anurag", "displayTrigger" + trigger.getTriggerType());
        switch (trigger.getTriggerType()) {
            case TRIGGER_ON_PAGE_OPEN:
                play(trigger);
                break;
            case TRIGGER_CLICK:
                runTriggerClick(trigger);
                break;
            case TRIGGER_BUTTON:
                drawTriggerAndPlay(trigger);
                break;
            default:
                break;
        }
        if (trigger.getTrigger().equals(TRIGGER_ON_PAGE_OPEN)) {
            play(trigger);
        } else {
            //TODO : add trigger next to icon
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onEnteringNewActivity(Activity activity) {
        if (null == activity)
            return;
        mActivity = activity;
        FLAG_IN_TOUR = false;
        ArrayList<Trigger> triggers = new ArrayList<>();
        Context context = activity.getApplicationContext();
        PointziDB db = new PointziDB(context);
        db.open();
        String viewName = getViewName(activity.getClass().getName());
        db.getTriggerForView(viewName, TRIGGER_ON_PAGE_OPEN, ACTION_PENDING, triggers);
        db.close();
        if (triggers.size() == 0) {
            //No triggers on page open so get all other triggers
            db.getTriggerForView(viewName, null, ACTION_PENDING, triggers);
            for (Trigger trigger : triggers) {
                displayTrigger(trigger);
            }
        } else {
            displayTrigger(triggers.get(0));
        }
    }

    public void onLeavingActivity(Activity activity) {
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onOrientationChange(Activity activity) {
        onEnteringNewActivity(activity);
    }


    /**
     * Send acknowledgement for the received feed item
     *
     * @param feedId
     */
    private void sendFeedAck(int feedId) {
        if (null == mActivity) {
            Log.e(Util.TAG, "sendFeedAck: context==null returning..");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(CODE, CODE_FEED_ACK);
        params.putInt(SHFEEDID, feedId);
        Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
        manager.addLogsForSending(params);
    }

    @Override
    public void onButtonClicked(PointziBase widget, int buttonCode, boolean[] result) {
        final String feed_id = widget.getFeedID();
        if (null != baseFrame)
            baseFrame.removeAllViews();
        if (playList != null) {
            if (stepCounter >= 0) {
                if (buttonCode == ACCEPTED_BUTTON) {
                    stepCounter++;
                }
                if (buttonCode == DECLINE_BUTTON) {
                    stepCounter--;
                }
                if (buttonCode == DISMISS_BUTTON) {
                    stepCounter = -1;
                    FLAG_IN_TOUR = false;
                    PointziDB db = new PointziDB(mActivity);
                    db.open();
                    db.updateActionedFlag(feed_id, ACTION_ACTIONED);
                    db.close();
                    return;
                }
                if (stepCounter < playList.size()) {
                    PointziBase obj = playList.get(stepCounter);
                    obj.setFeedID(feed_id);
                    play(obj);
                } else {
                    stepCounter = -1;
                    FLAG_IN_TOUR = false;
                    /*
                    PointziDB db = new PointziDB(mActivity);
                    db.open();
                    db.updateActionedFlag(feed_id, ACTION_ACTIONED);
                    db.close();
                    */
                    return;
                }
            }
        } else {
            stepCounter = -1;
            FLAG_IN_TOUR = false;
            /*
            PointziDB db = new PointziDB(mActivity);
            db.open();
            db.updateActionedFlag(feed_id, ACTION_ACTIONED);
            db.close();
            */
            try {
                int feedId = Integer.parseInt(feed_id);
                String resultCode = getResultString(buttonCode);
                notifyFeedResult(mActivity, feedId, resultCode, result[0], true);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Number format exception, Feed result not sent");
            }

        }
    }
}
