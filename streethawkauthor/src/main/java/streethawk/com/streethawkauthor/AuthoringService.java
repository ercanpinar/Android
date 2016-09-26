package streethawk.com.streethawkauthor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.streethawk.library.core.WidgetDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthoringService extends Service implements Constants{


    //TODO : Change this into an intent service


    private static ArrayList<Tip> tipList;   //parse this list for creating JSON to be submited to server
    private static AuthoringService instance=null;
    private static String mType;
    private static String mTrigger;

    private String mPayload;


    /**
     * Singleton
     * @return
     */
    public static AuthoringService getInstance(){
        if(null==instance){
            instance = new AuthoringService();
        }
        return instance;
    }


    public String getPayload(){
        return mPayload;
    }

    private void setPayLoad(String payload){
        mPayload = payload;
    }


    public AuthoringService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    /**
     * Add object to tip list for adding params in JSON
     * @param object
     */
    public void addTipToList(Tip object){
        if(null==tipList)
            tipList = new ArrayList<Tip>();
        tipList.add(object);
    }

    public void setType(String type){
        mType = type;
    }

    public void setTrigger(String trigger){
        mTrigger = trigger;
    }

    public void clearTipListObject(){
        if(null!=tipList)
            tipList.clear();
        mType = null;
        mTrigger = null;
    }

    public void sendToolTipToServer(){
        Log.e("Anurag","Send tooltip to server "+mPayload);
    }


    public void prepareJSONForCampaign(){
        if(null==tipList) {
            return;
        }
        if(tipList.size()<=0) {
            return;
        }
        JSONObject jsonObject = new JSONObject();

        JSONObject setup = new JSONObject();
        try {
            setup.put(SETUP_DISPLAY,null);
            setup.put(SETUP_TRIGGER,null);
            setup.put(SETUP_TARGET,null);
            setup.put(SETUP_VIEW,null);
            setup.put(SETUP_HIDDEN,null);
            setup.put(SETUP_TOOL,null);
            JSONObject widget = new JSONObject();
            widget.put(SETUP_WIDGET_TYPE,null);
            widget.put(SETUP_WIDGET_LABEL,null);
            widget.put(SETUP_WIDGET_CSS,null);
            setup.put(SETUP_WIDGET,widget);
            jsonObject.put(SETUP,setup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for (Tip tip : tipList) {
                JSONObject obj = new JSONObject();
                obj.put(TITLE,tip.getTitle());
                obj.put(CONTENT,tip.getContent());
                obj.put(TITLE_COLOR,tip.getTitleColor());
                obj.put(CONTENT_COLOR,tip.getContentColor());
                obj.put(BG_COLOR,tip.getBackGroundColor());
                obj.put(TARGET,tip.getTarget());
                obj.put(PLACEMENT,tip.getPlacement());
                obj.put(CHILD,tip.getChild());
                obj.put(PARENT,tip.getParent());
                {
                    JSONObject customData = new JSONObject();
                    customData.put(NEXT_BUTTON, tip.getAcceptedButtonTitle());
                    customData.put(PREV_BUTTON, tip.getAcceptedButtonTitle());
                    {
                        JSONObject DNDObject = new JSONObject();
                        DNDObject.put(TITLE,tip.getDNDTitle());
                        DNDObject.put(CONTENT,tip.getDNDContent());
                        DNDObject.put(DND_B1,tip.getDNDB1());
                        DNDObject.put(DND_B2,tip.getDNDB2());
                        customData.put(DND,DNDObject);
                    }
                    obj.put(CUSTOM_DATA,customData);
                }
            jsonObject.put(mType,obj);
            }
            Log.e("Anurag","4");
            setPayLoad(jsonObject.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
}