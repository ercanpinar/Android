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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.streethawk.library.core.WidgetDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthoringService extends Service implements Constants{


    private ArrayList<Tip> tipList;   //parse this list for creating JSON to be submited to server

    private static AuthoringService instance=null;


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
        tipList = new ArrayList<Tip>();
    }



    /**
     * Add object to tip list for adding params in JSON
     * @param object
     */
    public void addTipToList(Tip object){
        tipList.add(object);
    }

    public void clearTipListObject(){
        tipList.clear();
    }

    private void sendPaylodToServer(JSONObject json){

    }



    public void prepareJSONForCampaign(String type){
        if(null==tipList)
            return;
        if(tipList.size()<=0)
            return;
        JSONObject jsonObject = new JSONObject();
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
            jsonObject.put(type,obj);
            }
            sendPaylodToServer(jsonObject);
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