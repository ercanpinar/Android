package streethawk.com.streethawkauthor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Returns toolbar view
 */
public class Toolbar implements Constants{

    private IToolBarButtonListener mListener;


    public boolean showNextButton   = false;
    public boolean showPrevButton   = false;
    public boolean showPlayButton   = false;
    public boolean showAddButton    = false;
    public boolean showSaveButton   = false;
    public boolean showCancelButton = false;
    public boolean showEditButton   = false;


    private Activity mActivity;

    public void registerClickListener(IToolBarButtonListener listener){
        mListener = listener;
    }

    private View.OnTouchListener toolBarListener(){
        return new View.OnTouchListener(){

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        };
    }

    private Button getButtonView(final Context context,final String title){
        Button button = new Button(context);
        button.setText(title);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=mListener)
                    mListener.onButtonClick(title);

            }
        });
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(null!=mListener)
                    mListener.onTouchClick(motionEvent);
                return false;
            }
        });
        return button;
    }

    public View getToolBarView(Context context){
        LinearLayout baseLayout = new LinearLayout(context);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        baseLayout.setBackgroundColor(Color.WHITE);

        if(showAddButton){
            baseLayout.addView(getButtonView(context,BUTTON_ADD));
        }
        if(showEditButton){
            baseLayout.addView(getButtonView(context,BUTTON_EDIT));
        }
        if(showPrevButton){
            baseLayout.addView(getButtonView(context,BUTTON_BACK));
        }
        if(showSaveButton){
            baseLayout.addView(getButtonView(context,BUTTON_SAVE));
        }
        if(showPlayButton){
            baseLayout.addView(getButtonView(context,BUTTON_PREVIEW));
        }
        if(showNextButton){
            baseLayout.addView(getButtonView(context,BUTTON_NEXT));
        }
        if(showCancelButton){
            baseLayout.addView(getButtonView(context,BUTTON_CANCEL));
        }
        return baseLayout;

    }
}
