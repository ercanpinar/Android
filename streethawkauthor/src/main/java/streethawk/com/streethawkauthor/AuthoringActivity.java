package streethawk.com.streethawkauthor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.core.WidgetDB;

import java.util.ArrayList;

/**
 * This activity creates transparent layout above the application's activity
 * and record touch events. Later it sends touch events to the service
 */
public class AuthoringActivity extends Activity implements Constants{
    private String mTriggerOptionSelect;
    private Activity mActivity;
    private Dialog mDiaog;
    private String mToolType;
    private String mParent;
    private float mTouchX;
    private float mTouchY;

    private ArrayList<ViewDetails> mViewsOnActivity;

    class ViewDetails {
        public String viewName;
        public String widgetName;
        public float viewX;
        public float viewY;
        public float viewWidth;
        public float viewHeight;


    }

    private Tip mTipObject;

    @Override
    protected void onResume() {
        super.onResume();
        fillViewList();
        Toast.makeText(this,"Touch the location for placing the tip",Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener doneButtonListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    public void showDialog(final float X, final float Y) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.step2_title));
        builder.setPositiveButton(mActivity.getResources().getString(R.string.step1_next),
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(mTriggerOptionSelect){
                            case TRIGER_TYPE_ITEMCLICKED:
                            default:
                                break;
                            case TRIGER_TYPE_INFO:
                                break;
                            case TRIGER_TYPE_HELP:
                                AuthoringService instance = AuthoringService.getInstance();
                                instance.setTrigger(TRIGER_TYPE_HELP);
                                Context context = mActivity.getApplicationContext();
                                ImageButton imgButton = new ImageButton(context);
                                Bitmap cancel = BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.shquestionmark);
                                imgButton.setImageBitmap(cancel);
                                imgButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                imgButton.setX(X);
                                imgButton.setY(Y);
                                ViewGroup rootView = (ViewGroup) mActivity.findViewById(android.R.id.content).getRootView();
                                //FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
                                rootView.addView(imgButton);
                                rootView.invalidate();
                                showTipDialog();
                                break;
                            case TRIGET_TYPE_WARNING:
                                break;
                        }
                    }
                });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Anurag", "Cancel pressed");

            }
        });
        builder.setCancelable(false);
        final View view = mActivity.getLayoutInflater().inflate(R.layout.tipoptions, null);
        builder.setView(view);
        final Spinner spinner = (Spinner)view.findViewById(R.id.typeoptions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTriggerOptionSelect = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mTriggerOptionSelect = spinner.getSelectedItem().toString();
            }
        });

        if (null == mDiaog)
            mDiaog = builder.create();
        mDiaog.setTitle(mActivity.getResources().getString(R.string.step3_title));
        WindowManager.LayoutParams wmlp = mDiaog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) X;
        wmlp.y = (int) Y;
        if (mDiaog.isShowing())
            mDiaog.dismiss();
        mDiaog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        mActivity = this;
        LinearLayout baseLayout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(params);

        LinearLayout toolBar = new LinearLayout(context);
        LinearLayout.LayoutParams toolBarparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolBar.setLayoutParams(toolBarparams);
        toolBar.setBackgroundColor(Color.WHITE);
        toolBar.setOrientation(LinearLayout.HORIZONTAL);

        TextView message = new TextView(context);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        message.setLayoutParams(messageParams);
        messageParams.weight=2;
        String messageTitle = this.getResources().getString(R.string.step2_title);
        message.setText(messageTitle);
        message.setTextColor(Color.BLACK);

        Button button = new Button(context);
        String done = this.getResources().getString(R.string.tipcreate_end_authoring);
        button.setText(done);
        button.setOnClickListener(doneButtonListener());
        toolBar.addView(message);
        toolBar.addView(button);

        //baseLayout.addView(toolBar);

        baseLayout.setBackgroundColor(Color.TRANSPARENT);
        setContentView(baseLayout);

        Bundle extras = getIntent().getExtras();
        if(null!=extras){
            mToolType = extras.getString(EXTRA_TOOL_TYPE);
            mParent = extras.getString(EXTRA_PARENT);
            Log.e("Anurag","Parent "+mParent);

        }
        mTipObject = new Tip();
    }


    /**
     * Function returns viewname by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void fillViewList() {
        mViewsOnActivity = new ArrayList<>();
        String viewName = mParent;
        Context context = mActivity.getApplicationContext();
        WidgetDB.WidgetDBHelper helper = new WidgetDB(context).new WidgetDBHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        String query = "select * from " + WidgetDB.WidgetDBHelper.TOOLTIP_TABLE_NAME +
                " where " + WidgetDB.WidgetDBHelper.COLUMN_PARENT_VIEW + " = '" + viewName.trim() + "'";
        Log.e("Anurag","Query "+query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int resId = (cursor.getInt(cursor.getColumnIndex(WidgetDB.WidgetDBHelper.COLUMN_RES_ID)));
                if (-1 != resId) {
                    Activity currentActivity = new ActivityTracker().getCurrentActivity();
                    View view = currentActivity.findViewById(resId);
                    if (null != view) {
                        ViewDetails viewDetails = new ViewDetails();
                        viewDetails.viewName = mParent;
                        viewDetails.widgetName = (cursor.getString(cursor.getColumnIndex(WidgetDB.WidgetDBHelper.COLUMN_TEXT_ID)));

                        int loc[] = new int[2];
                        view.getLocationOnScreen(loc);
                        viewDetails.viewX = loc[0];
                        viewDetails.viewY = loc[1];
                        viewDetails.viewWidth = view.getWidth();
                        viewDetails.viewHeight = view.getHeight();
                        mViewsOnActivity.add(viewDetails);
                    }
                }
                cursor.moveToNext();
            }
        }
    }

    private String getTarget(){
        if(mViewsOnActivity==null){
            Log.e("Anurag","1");
            return null;
        }
        if(mViewsOnActivity.isEmpty()){
            Log.e("Anurag","2");
            return null;
        }
        for(ViewDetails obj:mViewsOnActivity){
            float startX = obj.viewX;
            float endX = startX + obj.viewWidth;
            float startY = obj.viewY;
            float endY = startY + obj.viewHeight;

            Log.e("Anurag","Name "+obj.widgetName);
            Log.e("Anurag","X"+startX+","+endX);
            Log.e("Anurag","Y"+startY+","+endY);
            Log.e("Anurag","T"+mTouchX+","+mTouchY);


            if((mTouchX>=startX) && (mTouchX<=endX)){
                if((mTouchY>=startY) && (mTouchY<=endY)){
                    return obj.widgetName;
                }
            }
        }
        Log.e("Anurag","No ui element found corresponding to given touch id");
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchX = event.getX();
        mTouchY = event.getY();
        showDialog(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void showTipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        String positiveButtonTitle = mActivity.getResources().getString(R.string.tipcreate_positiveButton_title);
        final View view = mActivity.getLayoutInflater().inflate(R.layout.tooltipcreate, null);
        builder.setPositiveButton(positiveButtonTitle,
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView title = (TextView)view.findViewById(R.id.tiptitle);
                        mTipObject.setTitle(title.getText().toString());
                        TextView content = (TextView)view.findViewById(R.id.tipcontent);
                        mTipObject.setContent(content.getText().toString());
                        Spinner spinner = (Spinner)view.findViewById(R.id.tipplacement);
                        mTipObject.setPlacement(spinner.getSelectedItem().toString());
                        EditText acceptedButtonTitleET = (EditText) view.findViewById(R.id.buttonpositive);
                        String acceptedButtonTitle = acceptedButtonTitleET.getText().toString();
                        if(null!=acceptedButtonTitle){
                            if(!acceptedButtonTitle.isEmpty()){
                                mTipObject.setAcceptedButtonTitle(acceptedButtonTitle);
                            }
                        }
                        EditText DelineButtonTitleET = (EditText) view.findViewById(R.id.buttonNegative);
                        String DelineButtonTitle = DelineButtonTitleET.getText().toString();
                        if(null!=DelineButtonTitle){
                            if(!DelineButtonTitle.isEmpty()){
                                mTipObject.setDeclinedButtonTitle(DelineButtonTitle);
                            }
                        }
                        CheckBox check = (CheckBox) view.findViewById(R.id.dnd);
                        if(check.isChecked()){
                            mTipObject.setHasDND(true);

                            //TODO DND related stuff here

                        }
                        mTipObject.setTarget(getTarget());
                        mTipObject.setParent(mParent);
                        AuthoringService instance = AuthoringService.getInstance();
                        instance.addTipToList(mTipObject);
                        Log.e("Anurag","mToolType "+mToolType);
                        int mode=-1;
                        if(mToolType.equals("Tip")){
                            Log.e("Anurag","mToolType 2");
                            mode = TOOLBAR_TIP_SAVE;
                        }
                        Toast.makeText(getApplicationContext(),"Tip Saved",Toast.LENGTH_LONG).show();
                        Authoring authoring = Authoring.getInstance(mActivity);
                        authoring.displayMainToolBar(mode);
                        finish();
                    }
                });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               //TODO: alert dialog are u sure
                finish();

            }
        });
        builder.setCancelable(false);

        ImageButton titleColorPicker = (ImageButton)view.findViewById(R.id.title_colorpicker);
        ImageButton contentColorPicker = (ImageButton)view.findViewById(R.id.content_colorpicker);
        titleColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker picker = new ColorPicker();
                picker.showColorPicker(mActivity, new IColorPickerObserver() {
                    @Override
                    public void onColorSelected(String color) {
                        TextView title = (TextView)view.findViewById(R.id.tiptitle);
                        title.setTextColor(Color.parseColor(color));
                        mTipObject.setTitleColor(color);
                    }
                });
            }
        });
        contentColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker picker = new ColorPicker();
                picker.showColorPicker(mActivity, new IColorPickerObserver() {
                    @Override
                    public void onColorSelected(String color) {
                        TextView content = (TextView)view.findViewById(R.id.tipcontent);
                        content.setTextColor(Color.parseColor(color));
                        mTipObject.setContentColor(color);
                    }
                });
            }
        });
        builder.setView(view);
        builder.show();
    }
}