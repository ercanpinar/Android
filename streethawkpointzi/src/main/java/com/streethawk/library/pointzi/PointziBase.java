package com.streethawk.library.pointzi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;
import com.streethawk.library.core.WidgetDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Base class to
 */
public class PointziBase implements Constants, Parcelable {

    /**
     * Placement
     */
    final String BOTTOM = "bottom";
    final String TOP = "top";
    final String LEFT = "left";
    final String RIGHT = "right";


    /*meta*/
    private String feedID = null;
    private String ID = null;
    private String target = null;              // UI element to which widget will be attached to
    private String view = null;
    private String viewBackGroundColor;
    private int childNumber = -1;       // sub view (example listview)
    private String placement = BOTTOM;           // Direction
    private String widgetType = MODAL;          // Select type of the widget (Tip,Model,couchmark etc)
    private String URL = null;
    private String URL_Content = null;
    private int viewElevation = 0;              // z elevation for shadows
    private int templateCode = -1;
    private int delay = 0;                  // Wait before dismissing the widget
    private int[] viewPadding = {2, 2, 2, 2};
    private String viewBorderColor;
    private int viewBorderWidth = 1;
    private int viewCornerRadius = 0;
    private int viewPercentage = 100;
    private String viewWidth;
    private String viewHeight;
    private int animation = 0;         //TODO next release

    /*title*/
    private String title = null;
    private String titleColor;
    private String titleBackgroundColor;
    private String titleGravity = DEFAULT_GRAVITY;
    private int[] titlePadding = {2, 2, 2, 2};
    ;
    private int titleElevation = 0;
    private String titleFontFamily = null;
    private int titleFontSize = -1;


    /*content*/
    private String content = null;
    private String contentColor;
    private String contentBackgroundColor;
    private String contentGravity = DEFAULT_GRAVITY;
    private int[] contentPadding = {2, 2, 2, 2};
    private int contentElevation = 0;
    private String contentFontFamily = null;
    private int contentFontSize = -1;

    /*buttons*/

    private String buttonPair = null;
    private String nextButtonTitle = null;
    private String nextButtonTitleColor;
    private String nextButtonBackgroundColor;
    private int[] nextButtonPadding = {2, 2, 2, 2};
    private int nextButtonElevation = 0;
    private String nextButtonFontFamily = null;
    private int nextButtonFontSize = -1;
    private String nextButtonGravity = DEFAULT_GRAVITY;
    private String nextButtonCTA = null;

    private String prevButtonTitle = null;
    private String prevButtonTitleColor;
    private String prevButtonBackgroundColor;
    private int[] prevButtonPadding = {2, 2, 2, 2};
    private int prevButtonElevation = 0;
    private String prevButtonFontFamily = null;
    private int prevButtonFontSize = -1;
    private String prevButtonGravity = DEFAULT_GRAVITY;
    private String prevButtonCTA = null;

    // Release 1 DND will be alert Dialog only
    private boolean hasDND = false;
    private String DND_Button_ID = null;
    private String DND_Button_color;
    private String DND_Button_Background_Color;
    private int[] DND_Padding = {2, 2, 2, 2};
    private int DND_Widht = 0;
    private int DND_Height = 0;
    private String DND_Next_Button_title = null;
    private String DND_Prev_Button_title = null;
    private String DND_Title = null;
    private String DND_Content = null;

    private int dim = 0;
    private String dim_color;

    private String touch_in;
    private String touch_out;


    public PointziBase() {
    }


    //TODO Anurag remove vars defined in constants

    protected PointziBase(Parcel in) {
        feedID = in.readString();
        ID = in.readString();
        target = in.readString();
        view = in.readString();
        viewBackGroundColor = in.readString();
        childNumber = in.readInt();
        placement = in.readString();
        widgetType = in.readString();
        URL = in.readString();
        viewElevation = in.readInt();
        templateCode = in.readInt();
        delay = in.readInt();
        viewPadding = in.createIntArray();
        viewBorderColor = in.readString();
        viewBorderWidth = in.readInt();
        viewCornerRadius = in.readInt();
        viewPercentage = in.readInt();
        viewWidth = in.readString();
        viewHeight = in.readString();
        //dismissRule = in.readInt();
        //dismssParams = in.createIntArray();
        //displayRule = in.readInt();
        //displayParams = in.createIntArray();
        animation = in.readInt();
        title = in.readString();
        titleColor = in.readString();
        titleBackgroundColor = in.readString();
        titleGravity = in.readString();
        titlePadding = in.createIntArray();
        titleElevation = in.readInt();
        titleFontFamily = in.readString();
        titleFontSize = in.readInt();
        content = in.readString();
        contentColor = in.readString();
        contentBackgroundColor = in.readString();
        contentGravity = in.readString();
        contentPadding = in.createIntArray();
        contentElevation = in.readInt();
        contentFontFamily = in.readString();
        contentFontSize = in.readInt();
        buttonPair = in.readString();
        nextButtonTitle = in.readString();
        nextButtonTitleColor = in.readString();
        nextButtonBackgroundColor = in.readString();
        nextButtonPadding = in.createIntArray();
        nextButtonElevation = in.readInt();
        nextButtonFontFamily = in.readString();
        nextButtonFontSize = in.readInt();
        nextButtonGravity = in.readString();
        nextButtonCTA = in.readString();
        prevButtonTitle = in.readString();
        prevButtonTitleColor = in.readString();
        prevButtonBackgroundColor = in.readString();
        prevButtonPadding = in.createIntArray();
        prevButtonElevation = in.readInt();
        prevButtonFontFamily = in.readString();
        prevButtonFontSize = in.readInt();
        prevButtonGravity = in.readString();
        prevButtonCTA = in.readString();
        hasDND = in.readByte() != 0;
        DND_Next_Button_title = in.readString();
        DND_Prev_Button_title = in.readString();
        DND_Title = in.readString();
        DND_Content = in.readString();
        dim = in.readInt();
        dim_color = in.readString();
        touch_in = in.readString();
        touch_out = in.readString();
        touch_out = in.readString();
        URL_Content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(feedID);
        dest.writeString(ID);
        dest.writeString(target);
        dest.writeString(view);
        dest.writeString(viewBackGroundColor);
        dest.writeInt(childNumber);
        dest.writeString(placement);
        dest.writeString(widgetType);
        dest.writeString(URL);
        dest.writeInt(viewElevation);
        dest.writeInt(templateCode);
        dest.writeInt(delay);
        dest.writeIntArray(viewPadding);
        dest.writeString(viewBorderColor);
        dest.writeInt(viewBorderWidth);
        dest.writeInt(viewCornerRadius);
        dest.writeInt(viewPercentage);
        dest.writeString(viewWidth);
        dest.writeString(viewHeight);
        // dest.writeInt(dismissRule);
        //dest.writeIntArray(dismissParams);
        //dest.writeInt(displayRule);
        //dest.writeIntArray(displayParams);
        dest.writeInt(animation);
        dest.writeString(title);
        dest.writeString(titleColor);
        dest.writeString(titleBackgroundColor);
        dest.writeString(titleGravity);
        dest.writeIntArray(titlePadding);
        dest.writeInt(titleElevation);
        dest.writeString(titleFontFamily);
        dest.writeInt(titleFontSize);
        dest.writeString(content);
        dest.writeString(contentColor);
        dest.writeString(contentBackgroundColor);
        dest.writeString(contentGravity);
        dest.writeIntArray(contentPadding);
        dest.writeInt(contentElevation);
        dest.writeString(contentFontFamily);
        dest.writeInt(contentFontSize);
        dest.writeString(buttonPair);
        dest.writeString(nextButtonTitle);
        dest.writeString(nextButtonTitleColor);
        dest.writeString(nextButtonBackgroundColor);
        dest.writeIntArray(nextButtonPadding);
        dest.writeInt(nextButtonElevation);
        dest.writeString(nextButtonFontFamily);
        dest.writeInt(nextButtonFontSize);
        dest.writeString(nextButtonGravity);
        dest.writeString(nextButtonCTA);
        dest.writeString(prevButtonTitle);
        dest.writeString(prevButtonTitleColor);
        dest.writeString(prevButtonBackgroundColor);
        dest.writeIntArray(prevButtonPadding);
        dest.writeInt(prevButtonElevation);
        dest.writeString(prevButtonFontFamily);
        dest.writeInt(prevButtonFontSize);
        dest.writeString(prevButtonGravity);
        dest.writeString(prevButtonCTA);
        dest.writeByte((byte) (hasDND ? 1 : 0));
        dest.writeString(DND_Next_Button_title);
        dest.writeString(DND_Prev_Button_title);
        dest.writeString(DND_Title);
        dest.writeString(DND_Content);
        dest.writeString(BOTTOM);
        dest.writeString(TOP);
        dest.writeString(LEFT);
        dest.writeString(RIGHT);
        dest.writeInt(dim);
        dest.writeString(dim_color);
        dest.writeString(touch_in);
        dest.writeString(touch_out);
        dest.writeString(URL_Content);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PointziBase> CREATOR = new Creator<PointziBase>() {
        @Override
        public PointziBase createFromParcel(Parcel in) {
            return new PointziBase(in);
        }

        @Override
        public PointziBase[] newArray(int size) {
            return new PointziBase[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getViewBackGroundColor() {
        if (null != viewBackGroundColor)
            return viewBackGroundColor;
        else
            return null;
    }

    public void setViewBackGroundColor(String viewBackGroundColor) {
        this.viewBackGroundColor = viewBackGroundColor;
    }

    public int getChildNumber() {
        return childNumber;
    }

    public void setChildNumber(int childNumber) {
        this.childNumber = childNumber;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getViewElevation() {
        return viewElevation;
    }

    public void setViewElevation(int viewElevation) {
        this.viewElevation = viewElevation;
    }

    public int getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(int templateCode) {
        this.templateCode = templateCode;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int[] getViewPadding() {
        return viewPadding;
    }

    public void setViewPadding(int[] viewPadding) {
        this.viewPadding = viewPadding;
    }

    public String getViewBorderColor() {
        if (null == viewBorderColor)
            return null;
        return viewBorderColor;
    }

    public void setViewBorderColor(String viewBorderColor) {
        this.viewBorderColor = viewBorderColor;
    }

    public int getViewBorderWidth() {
        return viewBorderWidth;
    }

    public void setViewBorderWidth(int viewBorderWidth) {
        this.viewBorderWidth = viewBorderWidth;
    }

    public int getViewCornerRadius() {
        return viewCornerRadius;
    }

    public void setViewCornerRadius(int viewCornerRadius) {
        this.viewCornerRadius = viewCornerRadius;
    }

    public int getViewPercentage() {
        return viewPercentage;
    }

    public void setViewPercentage(int viewPercentage) {
        this.viewPercentage = viewPercentage;
    }

    public String getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(String viewWidth) {
        this.viewWidth = viewWidth;
    }

    public String getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(String viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public String getDim_color() {
        return dim_color;
    }

    public void setDim_color(String dim_color) {
        this.dim_color = dim_color;
    }

    public String isTouch_in() {
        return touch_in;
    }

    public void setTouch_in(String touch_in) {
        this.touch_in = touch_in;
    }

    public String isTouch_out() {
        return touch_out;
    }

    public void setTouch_out(String touch_out) {
        this.touch_out = touch_out;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitleColor() {
        if (null == titleColor)
            return null;
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitleBackgroundColor() {
        if (null == titleBackgroundColor)
            return null;
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(String titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }

    public String getTitleGravity() {
        return titleGravity;
    }

    public void setTitleGravity(String titleGravity) {
        this.titleGravity = titleGravity;
    }

    public int[] getTitlePadding() {
        return titlePadding;
    }

    public void setTitlePadding(int[] titlePadding) {
        this.titlePadding = titlePadding;
    }

    public int getTitleElevation() {
        return titleElevation;
    }

    public void setTitleElevation(int titleElevation) {
        this.titleElevation = titleElevation;
    }

    public String getTitleFontFamily() {
        return titleFontFamily;
    }

    public void setTitleFontFamily(String titleFontFamily) {
        this.titleFontFamily = titleFontFamily;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentColor() {
        if (null == contentColor)
            return null;
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public String getContentBackgroundColor() {
        if (null == contentBackgroundColor)
            return null;
        return contentBackgroundColor;
    }

    public void setContentBackgroundColor(String contentBackgroundColor) {
        this.contentBackgroundColor = contentBackgroundColor;
    }

    public String getContentGravity() {
        return contentGravity;
    }

    public void setContentGravity(String contentGravity) {
        this.contentGravity = contentGravity;
    }

    public int[] getContentPadding() {
        return contentPadding;
    }

    public void setContentPadding(int[] contentPadding) {
        this.contentPadding = contentPadding;
    }

    public int getContentElevation() {
        return contentElevation;
    }

    public void setContentElevation(int contentElevation) {
        this.contentElevation = contentElevation;
    }

    public String getContentFontFamily() {
        return contentFontFamily;
    }

    public void setContentFontFamily(String contentFontFamily) {
        this.contentFontFamily = contentFontFamily;
    }

    public int getContentFontSize() {
        return contentFontSize;
    }

    public void setContentFontSize(int contentFontSize) {
        this.contentFontSize = contentFontSize;
    }

    public String getNextButtonTitle() {
        return nextButtonTitle;
    }

    public void setNextButtonTitle(String nextButtonTitle) {
        this.nextButtonTitle = nextButtonTitle;
    }

    public String getNextButtonTitleColor() {
        return nextButtonTitleColor;
    }

    public void setNextButtonTitleColor(String nextButtonTitleColor) {
        this.nextButtonTitleColor = nextButtonTitleColor;
    }

    public String getNextButtonBackgroundColor() {
        return nextButtonBackgroundColor;
    }

    public void setNextButtonBackgroundColor(String nextButtonBackgroundColor) {
        this.nextButtonBackgroundColor = nextButtonBackgroundColor;
    }

    public int[] getNextButtonPadding() {
        return nextButtonPadding;
    }

    public void setNextButtonPadding(int[] nextButtonPadding) {
        this.nextButtonPadding = nextButtonPadding;
    }

    public int getNextButtonElevation() {
        return nextButtonElevation;
    }

    public void setNextButtonElevation(int nextButtonElevation) {
        this.nextButtonElevation = nextButtonElevation;
    }

    public String getNextButtonFontFamily() {
        return nextButtonFontFamily;
    }

    public void setNextButtonFontFamily(String nextButtonFontFamily) {
        this.nextButtonFontFamily = nextButtonFontFamily;
    }

    public int getNextButtonFontSize() {
        return nextButtonFontSize;
    }

    public void setNextButtonFontSize(int nextButtonFontSize) {
        this.nextButtonFontSize = nextButtonFontSize;
    }

    public String getNextButtonCTA() {
        return nextButtonCTA;
    }

    public void setNextButtonCTA(String nextButtonCTA) {
        this.nextButtonCTA = nextButtonCTA;
    }

    public String getPrevButtonTitle() {
        return prevButtonTitle;
    }

    public void setPrevButtonTitle(String prevButtonTitle) {
        this.prevButtonTitle = prevButtonTitle;
    }

    public String getPrevButtonTitleColor() {
        return prevButtonTitleColor;
    }

    public void setPrevButtonTitleColor(String prevButtonTitleColor) {
        this.prevButtonTitleColor = prevButtonTitleColor;
    }

    public String getPrevButtonBackgroundColor() {
        return prevButtonBackgroundColor;
    }

    public void setPrevButtonBackgroundColor(String prevButtonBackgroundColor) {
        this.prevButtonBackgroundColor = prevButtonBackgroundColor;
    }

    public int[] getPrevButtonPadding() {
        return prevButtonPadding;
    }

    public void setPrevButtonPadding(int[] prevButtonPadding) {
        this.prevButtonPadding = prevButtonPadding;
    }

    public int getPrevButtonElevation() {
        return prevButtonElevation;
    }

    public void setPrevButtonElevation(int prevButtonElevation) {
        this.prevButtonElevation = prevButtonElevation;
    }

    public String getPrevButtonFontFamily() {
        return prevButtonFontFamily;
    }

    public void setPrevButtonFontFamily(String prevButtonFontFamily) {
        this.prevButtonFontFamily = prevButtonFontFamily;
    }

    public int getPrevButtonFontSize() {
        return prevButtonFontSize;
    }

    public void setPrevButtonFontSize(int prevButtonFontSize) {
        this.prevButtonFontSize = prevButtonFontSize;
    }

    public String getPrevButtonCTA() {
        return prevButtonCTA;
    }

    public void setPrevButtonCTA(String prevButtonCTA) {
        this.prevButtonCTA = prevButtonCTA;
    }

    public boolean isHasDND() {
        return hasDND;
    }

    public void setHasDND(boolean hasDND) {
        this.hasDND = hasDND;
    }

    public String getDND_Next_Button_title() {
        return DND_Next_Button_title;
    }

    public void setDND_Next_Button_title(String DND_Next_Button_title) {
        this.DND_Next_Button_title = DND_Next_Button_title;
    }

    public String getDND_Prev_Button_title() {
        return DND_Prev_Button_title;
    }

    public void setDND_Prev_Button_title(String DND_Prev_Button_title) {
        this.DND_Prev_Button_title = DND_Prev_Button_title;
    }

    public String getDND_Title() {
        return DND_Title;
    }

    public void setDND_Title(String DND_Title) {
        this.DND_Title = DND_Title;
    }

    public String getDND_Content() {
        return DND_Content;
    }

    public void setDND_Content(String DND_Content) {
        this.DND_Content = DND_Content;
    }

    public String getButtonPair() {
        return buttonPair;
    }

    public void setButtonPair(String buttonPair) {
        this.buttonPair = buttonPair;
    }

    public String getNextButtonGravity() {
        return nextButtonGravity;
    }

    public void setNextButtonGravity(String nextButtonGravity) {
        this.nextButtonGravity = nextButtonGravity;
    }

    public String getPrevButtonGravity() {
        return prevButtonGravity;
    }

    public void setPrevButtonGravity(String prevButtonGravity) {
        this.prevButtonGravity = prevButtonGravity;
    }

    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    public String getDND_Button_ID() {
        return DND_Button_ID;
    }

    public void setDND_Button_ID(String DND_Button_ID) {
        this.DND_Button_ID = DND_Button_ID;
    }

    public String getDND_Button_color() {
        return DND_Button_color;
    }

    public void setDND_Button_color(String DND_Button_color) {
        this.DND_Button_color = DND_Button_color;
    }

    public String getDND_Button_Background_Color() {
        return DND_Button_Background_Color;
    }

    public void setDND_Button_Background_Color(String DND_Button_Background_Color) {
        this.DND_Button_Background_Color = DND_Button_Background_Color;
    }

    public int[] getDND_Padding() {
        return DND_Padding;
    }

    public void setDND_Padding(int[] DND_Padding) {
        this.DND_Padding = DND_Padding;
    }

    public int getDND_Widht() {
        return DND_Widht;
    }

    public void setDND_Widht(int DND_Widht) {
        this.DND_Widht = DND_Widht;
    }

    public int getDND_Height() {
        return DND_Height;
    }

    public void setDND_Height(int DND_Height) {
        this.DND_Height = DND_Height;
    }


    protected static WidgetClickListener mClickEventListeners = null;


    public static void setWidgetClickListener(WidgetClickListener listener) {
        if (null == listener)
            return;
        mClickEventListeners = listener;
    }

    protected String getViewName(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }

    protected int getResIdFromWidgetName(Activity activity, String widgetName) {
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

    protected Typeface getTypeface(Activity activity, String fontName) {
        if (null == fontName)
            return null;
        try {
            AssetManager assetManager = activity.getApplicationContext().getAssets();
            Typeface typeface = Typeface.createFromAsset(assetManager, String.format(Locale.US, "fonts/%s", fontName));
            return typeface;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getResultString(int buttonCode) {
        switch (buttonCode) {
            case ACCEPTED_BUTTON:
                return "accepted";
            case DECLINE_BUTTON:
                return "postponed";
            case DISMISS_BUTTON:
                return "rejected";
            default:
                return "invalid";
        }
    }


    protected int getResIdFromButtonName(String buttonName) {
        return -1;
    }

    private View.OnClickListener customCrossButtonListener(final Activity activity, final PointziBase widget) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String title = widget.getDND_Title();
                final String content = widget.getDND_Content();
                final String positiveButtonTitle = widget.getDND_Next_Button_title();
                final String negativeButtonTitle = widget.getDND_Prev_Button_title();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                                    boolean[] results = {false, true};
                                    mClickEventListeners.onButtonClicked(widget, DECLINE_BUTTON, results);
                                }
                            });
                        }
                        if (null != negativeButtonTitle) {
                            builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String id = widget.getFeedID();
                                    boolean[] results = {false, false};
                                    mClickEventListeners.onButtonClicked(widget, DECLINE_BUTTON, results);
                                }
                            });
                        }
                        builder.setCancelable(false); // Adding as dismiss is causing side effects
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                });

            }
        };
    }

    /**
     * Return DND button view
     *
     * @param activity
     * @param listener
     * @return
     */
    protected View getDNDButtonView(final Activity activity, final PointziBase widget,
                                    final View.OnClickListener listener) {
        ImageButton crossButton = new ImageButton(activity);
        Drawable drawable;
        if (null == widget.getDND_Button_ID()) {
            drawable = VectorDrawableCompat.create(activity.getResources(),
                    R.drawable.ic_close, activity.getTheme());
        } else {
            int resID = R.drawable.ic_close;
            drawable = VectorDrawableCompat.create(activity.getResources(),
                    resID, activity.getTheme());
        }
        crossButton.setImageDrawable(drawable);
        crossButton.setBackgroundColor(Color.parseColor((widget.getDND_Button_Background_Color())));
        int[] padding = widget.getDND_Padding();
        crossButton.setPadding(padding[0], padding[1], padding[2], padding[3]);

        int width = widget.getDND_Height();
        int height = widget.getDND_Widht();
        LinearLayout.LayoutParams params;
        if (0 == width || 0 == height) {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params = new LinearLayout.LayoutParams(width, height);
        }
        crossButton.setLayoutParams(params);
        if (null != listener) {
            crossButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customCrossButtonListener(activity, widget).onClick(view);
                    listener.onClick(view);
                }
            });
        }
        return crossButton;
    }

    protected int getGravity(String gravityText) {
        if (null == gravityText)
            return Gravity.LEFT;
        if (gravityText.isEmpty())
            return Gravity.LEFT;

        if (gravityText.equals(GRAVITY_CENTER))
            return Gravity.CENTER;

        if (gravityText.equals(GRAVITY_RIGHT))
            return Gravity.RIGHT;

        if (gravityText.equals(GRAVITY_LEFT))
            return Gravity.LEFT;
        return Gravity.LEFT;

    }

    /**
     * Parse and returns width
     *
     * @param viewWidth
     * @return
     */
    protected int getWidth(Activity activity, String viewWidth) {
        int w = 0;
        try {
            w = Integer.parseInt(viewWidth);
        } catch (NumberFormatException e) {
            int index = viewWidth.indexOf("%");
            if (0 != index) {
                viewWidth = viewWidth.substring(0, index);
                try {
                    int percentage = Integer.parseInt(viewWidth);
                    DisplayMetrics metrics;
                    metrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    if (-1 != percentage) {
                        float pc = (float) (percentage / 100.0);
                        w = (int) (metrics.widthPixels * pc);
                    }
                } catch (NumberFormatException x) {
                    w = 0;
                }
            }

        }
        return w;
    }

    /**
     * Parse and returns height
     *
     * @param viewHeight
     * @return
     */
    protected int getHeight(Activity activity, String viewHeight) {
        int height = 0;
        try {
            height = Integer.parseInt(viewHeight);
        } catch (NumberFormatException e) {
            int index = viewHeight.indexOf("%");
            if (0 != index) {
                viewHeight = viewHeight.substring(0, index);
                try {
                    int percentage = Integer.parseInt(viewHeight);
                    DisplayMetrics metrics;
                    metrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    if (-1 != percentage) {
                        float pc = (float) (percentage / 100.0);
                        height = (int) (metrics.heightPixels * pc);
                    }
                } catch (NumberFormatException x) {
                    height = 0;
                }
            }

        }
        return height;
    }


    protected View getTitleView(Activity activity, PointziBase widget) {
        if (null == widget)
            return null;
        String title = widget.getTitle();
        if (null != title) {
            //Add title here
            if (!title.isEmpty()) {
                TextView titletv = new TextView(activity.getApplicationContext());
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

                spannable = Html.fromHtml(title/*+'\u00A9'+0x1F601*/);
                titletv.setText(spannable);
                titletv.setTextColor(Color.parseColor(titleColor));
                return titletv;

            }
        }
        return null;
    }

    protected View getContentView(Activity activity, PointziBase widget) {
        if (null == widget)
            return null;
        String title = widget.getContent();
        if (null != title) {
            //Add title here
            if (!title.isEmpty()) {
                TextView titletv = new TextView(activity.getApplicationContext());
                String fontFamily = widget.getContentFontFamily();
                if (null != fontFamily) {
                    Typeface typeface = getTypeface(activity, fontFamily);
                    if (null != typeface) {
                        titletv.setTypeface(typeface);
                    }
                }
                int[] padding = widget.getContentPadding();
                titletv.setPadding(padding[0], padding[1], padding[2], padding[3]);
                titletv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Spanned spannable = null;

                spannable = Html.fromHtml(title/*+'\u00A9'+0x1F601*/);
                titletv.setText(spannable);
                titletv.setTextColor(Color.parseColor(titleColor));
                return titletv;

            }
        }
        return null;
    }


    protected View getCTAButtonView(Activity activity, PointziBase widget,
                                    View.OnClickListener nextButtonClickListener, View.OnClickListener prevButtonClickListener) {
        if (null == activity)
            return null;
        if (null == widget)
            return null;

        Context context = activity.getApplicationContext();

        LinearLayout LL_Button_horizontal = new LinearLayout(context);
        LinearLayout.LayoutParams LL_Button_horizontalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LL_Button_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LL_Button_horizontal.setLayoutParams(LL_Button_horizontalParams);
        LL_Button_horizontal.setGravity(Gravity.RIGHT);
        int width = widget.getViewBorderWidth();
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
                    if (null != nextButtonClickListener)
                        imgBtn.setOnClickListener(prevButtonClickListener);
                    LL_Button_horizontal.addView(imgBtn);
                } else {
                    //create normal button here
                    Button btn = new Button(context);
                    int[] padding = widget.getPrevButtonPadding();
                    btn.setPadding(padding[0], padding[1], padding[2], padding[3]);

                    btn.setBackgroundColor(Color.parseColor(widget.getPrevButtonBackgroundColor()));
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
                    btn.setOnClickListener(prevButtonClickListener);
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
                    imgBtn.setOnClickListener(nextButtonClickListener);
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
                    btn.setBackgroundColor(Color.parseColor(widget.getNextButtonBackgroundColor()));
                    btn.setTextColor(Color.parseColor(widget.getNextButtonTitleColor()));
                    btn.setText(accpetedButtonTitle);
                    btn.setBackgroundColor(Color.parseColor(widget.getNextButtonBackgroundColor()));
                    String fontFamily = widget.getNextButtonFontFamily();
                    if (null != fontFamily) {
                        Typeface typeface = getTypeface(activity, fontFamily);
                        if (null != typeface)
                            btn.setTypeface(typeface);
                    }
                    btn.setOnClickListener(nextButtonClickListener);
                    LL_Button_horizontal.addView(btn);
                }
            }
        }
        return LL_Button_horizontal;
    }


    protected View getUrlContentView(Activity activity, String url_content) {
        if (null != url_content) {
            TextView url_contentTV = new TextView(activity);
            url_contentTV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            Spanned text = Html.fromHtml(url_content);
            url_contentTV.setText(text);
            return url_contentTV;
        }
        return null;
    }

    protected void dimBackground(Activity activity, View view, int dimAmount, String bgColor) {
        if (-1 == dimAmount)
            return;
        float dim = dimAmount / 100.0f;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = dim;
        view.setBackgroundColor(Color.parseColor(bgColor));
        windowManager.updateViewLayout(view, params);
    }

    protected View.OnClickListener acceptedButtonOnClickListener(Activity activity, final PointziBase widget, final boolean[] result) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mClickEventListeners) {
                    mClickEventListeners.onButtonClicked(widget, ACCEPTED_BUTTON, result);

                }
            }
        };
    }

    protected View.OnClickListener declineButtonOnClickListener(Activity activity, final PointziBase widget, final boolean[] result) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mClickEventListeners) {
                    mClickEventListeners.onButtonClicked(widget, DECLINE_BUTTON, result);
                }
            }

        };
    }

    protected View.OnClickListener dismissButtonOnClickListener(Activity activity, final PointziBase widget, final boolean[] result) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mClickEventListeners) {
                    mClickEventListeners.onButtonClicked(widget, DISMISS_BUTTON, result);

                }
            }
        };
    }

    protected View.OnClickListener doNothing() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    /**
     * @param feedId     Id of the feed item associated with the result
     * @param result     Feed result in String
     * @param feedDelete set to true if feed items should be deleted from server for the given install
     */
    protected void notifyFeedResult(Activity activity, int feedId, String result, boolean feedDelete, boolean completed) {
        if (null == activity) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
            return;
        }
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_RESULT);
        params.putInt(SHFEEDID, feedId);
        JSONObject status = new JSONObject();
        try {
            status.put(RESULT_RESULT, result);
            status.put(RESULT_FEED_DELETE, feedDelete);
            status.put(RESULT_FEED_COMPLETED, completed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.putString(STATUS, status.toString());
        Logging manager = Logging.getLoggingInstance(activity.getApplicationContext());
        manager.addLogsForSending(params);
    }

    public String getURL_Content() {
        return URL_Content;
    }

    public void setURL_Content(String URL_Content) {
        this.URL_Content = URL_Content;
    }


    protected interface ICompletedWebView {
        public void onCompletedwebView();
    }

    protected WebView getWebView(Activity activity, String url) {
        WebView webView = new WebView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        webView.setLayoutParams(params);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setInitialScale(1);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        return webView;
    }
}
