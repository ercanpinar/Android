package com.streethawk.streethawkapp;

/**
 * Created by anuragkondeya on 2/12/16.
 */

public class PointziParams {

    /**
     * Placement
     */
    final String BOTTOM = "bottom";
    final String TOP = "top";
    final String LEFT = "left";
    final String RIGHT = "right";


    /*Laucnher*/

    private String laucnher_type;
    private String launcher_file_name;


    /*meta*/
    private String feedID;
    private String ID;
    private String target;
    private String view;
    private String viewBackGroundColor;
    private int childNumber;
    private String placement;
    private String widgetType;
    private String URL;
    private String URL_Content;
    private int viewElevation;
    private int templateCode;
    private int delay;
    private int[] viewPadding;
    private String viewBorderColor;
    private int viewBorderWidth;
    private int viewCornerRadius;
    private int viewPercentage;
    private int viewWidth;
    private int viewHeight;
    private int animation;

    /*title*/
    private String title;
    private String titleColor;
    private String titleBackgroundColor;
    private String titleGravity;
    private int[] titlePadding;
    private int titleElevation;
    private String titleFontFamily;
    private int titleFontSize;


    /*content*/
    private String content;
    private String contentColor;
    private String contentBackgroundColor;
    private String contentGravity;
    private int[] contentPadding;
    private int contentElevation;
    private String contentFontFamily;
    private int contentFontSize;

    /*buttons*/

    private String buttonPair;
    private String nextButtonTitle;
    private String nextButtonTitleColor;
    private String nextButtonBackgroundColor;
    private int[] nextButtonPadding;
    private int nextButtonElevation;
    private String nextButtonFontFamily;
    private int nextButtonFontSize;
    private String nextButtonGravity;
    private String nextButtonCTA;

    private String prevButtonTitle;
    private String prevButtonTitleColor;
    private String prevButtonBackgroundColor;
    private int[] prevButtonPadding;
    private int prevButtonElevation;
    private String prevButtonFontFamily;
    private int prevButtonFontSize;
    private String prevButtonGravity;
    private String prevButtonCTA;

    // Release 1 DND will be alert Dialog only
    private boolean hasDND;
    private String DND_Button_ID;
    private String DND_Button_color;
    private String DND_Button_Background_Color;
    private int[] DND_Padding;
    private int DND_Widht;
    private int DND_Height;
    private String DND_Next_Button_title;
    private String DND_Prev_Button_title;
    private String DND_Title;
    private String DND_Content;

    private int dim;
    private String dim_color;

    private String touch_in;
    private String touch_out;

    public String getViewBackGroundColor() {
        return viewBackGroundColor;
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

    public String getURL_Content() {
        return URL_Content;
    }

    public void setURL_Content(String URL_Content) {
        this.URL_Content = URL_Content;
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

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
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
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitleBackgroundColor() {
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
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public String getContentBackgroundColor() {
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

    public String getButtonPair() {
        return buttonPair;
    }

    public void setButtonPair(String buttonPair) {
        this.buttonPair = buttonPair;
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

    public String getNextButtonGravity() {
        return nextButtonGravity;
    }

    public void setNextButtonGravity(String nextButtonGravity) {
        this.nextButtonGravity = nextButtonGravity;
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

    public String getPrevButtonGravity() {
        return prevButtonGravity;
    }

    public void setPrevButtonGravity(String prevButtonGravity) {
        this.prevButtonGravity = prevButtonGravity;
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

    public String getTouch_in() {
        return touch_in;
    }

    public void setTouch_in(String touch_in) {
        this.touch_in = touch_in;
    }

    public String getTouch_out() {
        return touch_out;
    }

    public void setTouch_out(String touch_out) {
        this.touch_out = touch_out;
    }

    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

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
}
