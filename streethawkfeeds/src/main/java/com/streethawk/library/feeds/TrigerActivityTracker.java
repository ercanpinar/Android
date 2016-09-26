package com.streethawk.library.feeds;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Class for tracking changes in activity refelcted from core module
 */
public class TrigerActivityTracker extends Utils implements Constants {

    private static TrigerActivityTracker instance;
    private Activity mActivity;
    private TrigerActivityTracker() {
    }

    public static TrigerActivityTracker getInstance() {
        if (null == instance) {
            instance = new TrigerActivityTracker();
        }
        return instance;
    }


    private class displayTriggerAsyncTask extends AsyncTask<SHTriger,Void,Void>{

        private SHTriger obj;

        @Override
        protected Void doInBackground(SHTriger... shTrigers) {
            obj = shTrigers[0];
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Context context = mActivity.getApplicationContext();
            final int delay = obj.getDelay();
            final String triger = obj.getTriger();
            final String view = obj.getView();
            final String target = obj.getTarget();
            final String type = obj.getWidgetType();
            final String label = obj.getWidgetLabel();
            final String bgcolor = obj.getBGColor();
            String placement = obj.getPlacement();
            final String tool = obj.getTool();
            final String toolId = obj.getToolId();
            LinearLayout baseLayout = new LinearLayout(context);
            baseLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (null == type) {
                return;
            }
            switch (type) {
                case DOT:
                    //TODO image of transparent dot in photoshop

                    break;
                default:
                    if (null != label) {
                        Button button = new Button(mActivity);
                        button.setText(label);
                        if (null != bgcolor) {
                            button.setBackgroundColor(Color.parseColor(bgcolor));
                        } else {
                            button.setBackgroundColor(Color.TRANSPARENT);
                        }
                        switch (triger) {
                            case TRIGER_CLICK:
                                defaut:
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(delay);
                                                    switch (tool) {
                                                        case TOUR:
                                                            new SHTours(mActivity).startTour(toolId);
                                                            break;
                                                        case TIP:
                                                            break;
                                                        case MODAL:
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                });
                                baseLayout.addView(button);
                                break;
                        }
                    }
                    break;

            }
            PopupWindow tipPopUpWindow = new PopupWindow(mActivity);
            if (null == placement)
                placement = BOTTOM;

            if (null == target) {
                //TODO: target center of screen
            } else {

                int widgetID = getResIdFromWidgetName(mActivity, target);

                if (-1 == widgetID) {
                    //TODO target center of screen
                } else {

                    View anchor = mActivity.findViewById(widgetID);

                    if (null != anchor) {
                        switch (placement) {
                            case BOTTOM:
                            default:
                                tipPopUpWindow.setContentView(baseLayout);
                                if(anchor.getVisibility()==View.VISIBLE) {
                                    tipPopUpWindow.showAsDropDown(anchor, 0, 0);
                                }

                                break;
                            case TOP:
                                break;
                            case RIGHT:
                                break;
                            case LEFT:
                                break;
                        }
                    }
                }
            }
        }
    }

    private void displayTriggerInCurrentView(final Activity activity, final SHTriger obj) {
        mActivity = activity;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayTriggerAsyncTask task = new displayTriggerAsyncTask();
                task.execute(obj);
            }
        }, 1000);
    }


    public void onOrientationChange(Activity activity) {

        //TODO

    }

    public void onEnteringNewActivity(Activity activity) {

        final String viewName = getViewName(activity.getClass().getName());
        SHTriger triger = new SHTriger();
        new TrigerDB(activity.getApplicationContext()).getTrigerForView(viewName, triger);
        if (null != triger) {
            //Start Tour/Modal based on the trigger
            displayTriggerInCurrentView(activity, triger);
            return;
        }else{

            //Start Tour/Modal based on user entering the activity
        }
    }

    public void onLeavingNewActivity(Activity activity) {

        //TODO
    }

    public void onApplicationForegronded(Activity activity) {
        //TODO

    }

    public void onApplicationBackgrounded(Activity activity) {
        //TODO

    }






}
