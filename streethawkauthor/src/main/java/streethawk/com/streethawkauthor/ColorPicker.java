package streethawk.com.streethawkauthor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorPicker {

    private String mColor;
    private String mA = "FF";
    private String mR = "00";
    private String mG = "00";
    private String mB = "00";

    private TextView mPreview;
    private SeekBar alpha;
    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;
    private EditText colorCode;

    private void changePreview() {
        mColor = "#" + mA + mR + mG + mB;
        mPreview.setBackgroundColor(Color.parseColor(mColor));
        colorCode.setText(mA + mR + mG + mB);
    }

    public void showColorPicker(final Activity activity,final IColorPickerObserver observer) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(activity.getResources().getString(R.string.color_picker_select_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(null!=observer){
                            observer.onColorSelected(mColor);
                        }
                    }
                });
        final View view = activity.getLayoutInflater().inflate(R.layout.colorpicker, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.show();

        mPreview = (TextView) view.findViewById(R.id.displayColorTV);
        alpha = (SeekBar) view.findViewById(R.id.alphavalue);
        red = (SeekBar) view.findViewById(R.id.redvalue);
        green = (SeekBar) view.findViewById(R.id.greenValue);
        blue = (SeekBar) view.findViewById(R.id.blueValue);
        colorCode = (EditText) view.findViewById(R.id.colorcode);

        /*
        colorCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = colorCode.getText().toString();
                if (code.length() == 8) {
                    mA = code.substring(0, 1);
                    mR = code.substring(2, 3);
                    mG = code.substring(4, 5);
                    mB = code.substring(6, 7);
                    alpha.setProgress(Integer.parseInt(mA, 16));
                    red.setProgress(Integer.parseInt(mR, 16));
                    green.setProgress(Integer.parseInt(mG, 16));
                    blue.setProgress(Integer.parseInt(mB, 16));
                    changePreview();
                }
                return;
            }
        });
        */

        alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {


                mA = Integer.toString(progress, 16);
                if (progress <= 15)
                    mA = "0" + mA;
                changePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                mR = Integer.toString(progress, 16);
                if (progress <= 15)
                    mR = "0" + mR;
                changePreview();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mG = Integer.toString(progress, 16);
                if (progress <= 15)
                    mG = "0" + mG;
                changePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                mB = Integer.toString(progress, 16);
                if (progress <= 15)
                    mB = "0" + mB;
                changePreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}