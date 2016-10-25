package com.streethawk.library.feeds;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class synchronises tours
 */

public class SHTours implements Constants, IPointziClickEventsListener {
    Activity mActivity;
    private int mTourStepNumber = 0;
    private ArrayList<TipObject> mTourList;
    private String mtourID = null;
    private PointiziActionListener mListener;

    public SHTours(Activity activity) {
        mActivity = activity;
    }

    private void parsePayloadToGetObject(SHTriger source) {
        if (null == source)
            return;
        JSONArray tourArray = null;
        mtourID = source.getFeedID();
        if (null == mtourID)
            return;
        try {
            tourArray = new JSONArray(source.getJSON());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < tourArray.length(); i++) {
            JSONObject tip = null;
            TipObject dest = new TipObject();
            try {
                tip = tourArray.getJSONObject(i);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (null != tip) {
                try {
                    dest.setId(tip.getString(TIP_ID));
                } catch (JSONException e1) {
                }
                try {
                    dest.setTitle(tip.getString(TITLE));
                } catch (JSONException e1) {
                }
                try {
                    dest.setContent(tip.getString(CONTENT));
                } catch (JSONException e1) {
                }
                try {
                    dest.setPlacement(tip.getString(PLACEMENT));
                } catch (JSONException e1) {
                }
                try {
                    dest.setTarget(tip.getString(TARGET));
                } catch (JSONException e1) {
                }
                try {
                    dest.setBackGroundColor(tip.getString(BG_COLOR));
                } catch (JSONException e1) {
                }
                try {
                    dest.setTitleColor(tip.getString(TITLE_COLOR));
                } catch (JSONException e1) {
                }
                try {
                    dest.setContentColor(tip.getString(CONTENT_COLOR));
                } catch (JSONException e1) {
                }
                try {
                    dest.setImageUrl(tip.getString(IMG_URL));
                } catch (JSONException e1) {
                }
                try {
                    dest.setParent(tip.getString(PARENT));
                } catch (JSONException e1) {
                }
                try {
                    dest.setChild(tip.getJSONArray(CHILD));
                } catch (JSONException e1) {
                }
                JSONObject customData = null;
                try {
                    customData = tip.getJSONObject(CUSTOM_DATA);
                } catch (JSONException e) {
                    customData = null;
                }
                if (null != customData) {
                    try {
                        dest.setAcceptedButtonTitle(customData.getString(NEXT_BUTTON));
                    } catch (JSONException e1) {
                    }
                    try {
                        dest.setDeclinedButtonTitle(customData.getString(PREV_BUTTON));
                    } catch (JSONException e1) {
                    }
                    try {
                        dest.setCloseButtonTitle(customData.getString(CLOSE_BUTTON));
                    } catch (JSONException e1) {
                    }
                    JSONObject DNDObject = null;
                    try {
                        DNDObject = customData.getJSONObject(DND);
                    } catch (JSONException e) {
                        DNDObject = null;
                    }
                    if (null != DNDObject) {
                        try {
                            dest.setDNDTitle(DNDObject.getString(DND_TITLE));
                        } catch (JSONException e1) {
                        }

                        try {
                            dest.setDNDContent(DNDObject.getString(DND_CONTENT));
                        } catch (JSONException e1) {
                        }
                        String DNDB1 = null;
                        String DNDB2 = null;

                        try {
                            DNDB1 = DNDObject.getString(DND_B1);
                            dest.setDNDB1(DNDB1);
                        } catch (JSONException e1) {
                        }
                        try {
                            DNDB2 = DNDObject.getString(DND_B2);
                            dest.setDNDB2(DNDB2);
                        } catch (JSONException e1) {
                        }

                        if (null == DNDB1 && null == DNDB2) {
                            dest.setHasDND(false);
                        } else {
                            dest.setHasDND(true);
                        }
                    }
                }
            }
            mTourList.add(i, dest);
            dest = null;  //GC
        }
    }

    public void registerClickListener(PointiziActionListener listener) {
        mListener = listener;
    }


    public void startTour(SHTriger triggerObject) {
        if (null == mActivity)
            return;
        if (null == mTourList) {
            mTourList = new ArrayList<TipObject>();
        }
        parsePayloadToGetObject(triggerObject);
        mTourStepNumber = 0;
        SHTips tips = new SHTips();
        tips.registerClickListener(this);
        if (null != mListener) {
            tips.registerClickListener(mListener);
        }
        tips.showTip(mActivity, mTourList.get(0), false);     //false not to send feed result
    }

    @Override
    public void onButtonClickedOnTip(TipObject object, int[] feedResults) {
        JSONObject status = new JSONObject();
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_RESULT);
        int isNext = feedResults[0];
        try {
            params.putInt(SHFEEDID, Integer.parseInt(mtourID));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return; //No feed result sent if feedid is not a string
        }
        if (-2 == feedResults[0]) {
            try {
                status.put(RESULT_FEED_DELETE, true);
                if (1 == feedResults[1]) {
                    status.put(RESULT_RESULT, object.getAcceptedButtonTitle());
                } else {
                    status.put(RESULT_RESULT, object.getDelineButtonTitle());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.putString(STATUS, status.toString());
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
            return;
        }
        try {
            params.putInt(SHFEEDID, Integer.parseInt(mtourID));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return; //No feed result sent if feedid is not a string
        }
        try {
            if (1 == isNext) {
                status.put(RESULT_RESULT, object.getAcceptedButtonTitle());
            } else {
                status.put(RESULT_RESULT, object.getDelineButtonTitle());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTourStepNumber += feedResults[0];
        if (mTourStepNumber < mTourList.size()) {
            try {
                status.put(RESULT_FEED_DELETE, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logging manager = Logging.getLoggingInstance(mActivity.getApplicationContext());
            manager.addLogsForSending(params);
            if (mTourStepNumber >= 0)
                Log.e("Anurag", "Button Title " + mTourList.get(mTourStepNumber).getDelineButtonTitle());
            new SHTips().showTip(mActivity, mTourList.get(mTourStepNumber), false);     //false not to send feed result
        } else {
            Context context = mActivity.getApplicationContext();
            try {
                TrigerDB trigerDb = new TrigerDB(context);
                trigerDb.open();
                trigerDb.updateActionedFlag(mtourID,Constants.FLAG_ACTIONED);
                trigerDb.close();
                status.put(RESULT_FEED_DELETE, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.putString(STATUS, status.toString());
            Logging manager = Logging.getLoggingInstance(context);
            manager.addLogsForSending(params);
        }
    }

    @Override
    public void onButtonClickedOnTour(TipObject object, int[] feedResults) {

    }

    @Override
    public void onButtonClickedOnModal(TipObject object, int[] feedResults) {

    }
}