package streethawk.com.streethawkauthor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.streethawk.library.core.WidgetDB;

import java.util.ArrayList;


/**
 * This activity creates transparent layout above the application's activity
 * and record touch events. Later it sends touch events to the service
 */
public class AuthoringActivity extends AppCompatActivity implements Constants{
    private String mTriggerOptionSelect;
    private Activity mActivity;
    private Dialog mDiaog;

    private Tip mTipObject;

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
        LinearLayout baseLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(params);
        baseLayout.setBackgroundColor(Color.TRANSPARENT);
        setContentView(baseLayout);
        mActivity = this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showDialog(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void showTipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        String positiveButtonTitle = mActivity.getResources().getString(R.string.tipcreate_positiveButton_title);
        builder.setPositiveButton(positiveButtonTitle,
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthoringService service = new AuthoringService();

                        finish();
                    }
                });
        builder.setNegativeButton(mActivity.getResources().getString(R.string.step1_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Anurag", "Cancel pressed");

            }
        });
        builder.setCancelable(false);
        final View view = mActivity.getLayoutInflater().inflate(R.layout.tooltipcreate, null);

        ImageButton titleColorPicker = (ImageButton)view.findViewById(R.id.title_colorpicker);
        ImageButton contentColorPicker = (ImageButton)view.findViewById(R.id.content_colorpicker);


        titleColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker();
                picker.showColorPicker(mActivity, new IColorPickerObserver() {
                    @Override
                    public void onColorSelected(String color) {

                    }
                });
            }
        });
        contentColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker();
                picker.showColorPicker(mActivity, new IColorPickerObserver() {
                    @Override
                    public void onColorSelected(String color) {

                    }
                });
            }
        });
        builder.setView(view);
        builder.show();
    }
}