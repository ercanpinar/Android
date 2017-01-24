package com.streethawk.library.pointzi;

/**
 * Created by anuragkondeya on 31/10/16.
 */

public interface Constants {


    String TAG = "StreetHawk";
    String SUBTAG = "Pointzi ";

    String CODE             = "code";
    String SHFEEDID         = "feed_id";
    int CODE_FEED_ACK       = 8200;
    int CODE_FEED_RESULT    = 8201;
    String STATUS                   = "result";
    String STEP_ID                  = "step_id";
    String RESULT_RESULT            = "status";
    String RESULT_FEED_DELETE       = "feed_delete";
    String RESULT_FEED_COMPLETED    = "complete";


    String VALUE            = "value";
    String DATA             = "d";
    String SETUP            = "setup";
    String TRIGGER          = "launcher";
    String TYPE             = "type";
    String COLOR            = "color";
    String BACKGROUND_COLOR = "background-color";
    String PADDING_TOP      = "padding-top";
    String PADDING_RIGHT    = "padding-right";
    String PADDING_BOTTOM   = "padding-bottom";
    String PADDING_LEFT     = "padding-left";
    String TARGET           = "target";
    String VIEW             = "view";
    String TOOL             = "tool";

    String PAYLOAD          = "payload";
    String META             = "meta";
    String ID               = "id";
    String PLACEMENT        = "placement";
    String CHILD            = "child";
    String URL              = "url";
    String URL_CONTENT      = "urlcontent";
    String Z_INDEX          = "z-index";
    String TEMPLATE         = "template";
    String DELAY            = "delay";
    String DISPLAY_PARAMS   = "display-params";
    String DISMISS_PARAMS   = "dismiss-params";
    String BORDER_COLOR     = "border-color";
    String BORDER_WIDTH     = "border-width";
    String CORNER_RADIUS    = "corner-radius";
    String PERCENTAGE       = "percentage";
    String WIDTH            = "width";
    String HEIGHT           = "height";
    String DIM              = "dim";
    String TOUCH_IN         = "touch-in";
    String TOUCH_OUT        = "touch-out";
    String ANIMATION        = "animation";
    String TITLE            = "title";
    String TEXT             = "text";
    String TEXT_ALIGH       = "text-align";
    String FONT_FAMILY      = "font-family";
    String FONT_SIZE        = "font-size";
    String CONTENT          = "content";
    String BUTTONS          = "buttons";
    String PAIR             = "pair";
    String NEXT             = "next";
    String CTA              = "cta";
    String PREV             = "prev";
    String DISMISS          = "dismiss";
    String B1               = "b1";
    String B2               = "b2";
    String BUTTON           = "button";
    String FALSE            = "false";
    String TRUE             = "true";


    String TRIGGER_ON_PAGE_OPEN = "page-open";
    String TRIGGER_CLICK        = "click";
    String TRIGGER_BUTTON       = "button";
    String FILE_NAME            = "file-name";


    String MODAL        = "modal";
    String TIP          = "tip";
    String TOUR         = "tour";
    String COUCH_MARKS  = "COUCH_MARKS";

    /*
    String DEFAULT_DIM_COLOR    = "#00FFFFFF";  //Transparent
    String DEFAULT_BG_COLOR     = "#FFFFFF";
    String DEFAULT_TEXT_COLOR   = "#000000";
    String DEFAULT_PLACEMENT    = "bottom";
    String DEFAULT_BORDER_COLOR  = "#FF0000";
    */
    int DEFAULT_Z_INDEX         = 0;
    int DEFAULT_PADDING         = 1;
    String DEFAULT_GRAVITY      = "left";
    int DEFAULT_FONT_SIZE       =-1;

    String GRAVITY_LEFT         = "left";
    String GRAVITY_RIGHT        = "right";
    String GRAVITY_CENTER       = "center";

    int ACCEPTED_BUTTON         = 1;
    int DECLINE_BUTTON          = -1;
    int DISMISS_BUTTON          = 0;


    int ACTION_PENDING          = 0;
    int ACTION_ACTIONED         = 1;


    String KEY_PARCELABLE_MODAL = "KeyParcelableModel";
    String KEY_FEEDID           = "keyfeedid";
    String KEY_EXTRAS           = "extras";

    /**
     * Template codes
     */
    int TEMPLATE_MODAL_SIMPLE_DISMISS      = 0;
    int TEMPLATE_MODAL_WITH_TITLE          = 1;
    int TEMPLATE_MODAL_WITH_CTA            = 2;

    String SHFEEDTIMESTAMP   = "shfeedtimestamp";
    String FEED             = "feed";


}
