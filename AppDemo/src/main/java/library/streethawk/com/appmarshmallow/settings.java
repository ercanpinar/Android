package library.streethawk.com.appmarshmallow;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
public class Settings extends Fragment implements Constants {


    private View mView;

    final String HOST = "shKeyHost";
    final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";
    final String PROD_URL = "https://api.streethawk.com";
    final String DEV_URL = "https://dev.streethawk.com";
    final String KFACTOR_URL = "https://api.kfacta.com";

    private OnFragmentInteractionListener mListener;

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void changeTargetUrl(String url) {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(HOST, url);
        e.commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView installid = (TextView) mView.findViewById(R.id.installId);
        TextView appKey = (TextView) mView.findViewById(R.id.appkey);
        TextView Server = (TextView) mView.findViewById(R.id.server);
        TextView senderid = (TextView) mView.findViewById(R.id.senderid);
        Button reregister = (Button) mView.findViewById(R.id.reregister);
        reregister.setOnClickListener(reRegister(mView));

        Context context = getActivity().getApplicationContext();
        installid.setText(" "+StreetHawk.INSTANCE.getInstallId(context));
        appKey.setText(" "+StreetHawk.INSTANCE.getAppKey(context));

        SharedPreferences prefs = getActivity().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String setServer = prefs.getString(HOST, PROD_URL);
        String senderId = prefs.getString(SHGCM_SENDER_KEY_APP, "");
        Server.setText(" "+setServer);
        senderid.setText(" "+senderId);
        return mView;
    }


    private String getUrlFromRadioButtonSelection(final View view) {
        if (null == view) {
            return null;
        }
        RadioButton prod = (RadioButton) mView.findViewById(R.id.prod);
        RadioButton dev = (RadioButton) mView.findViewById(R.id.dev);
        RadioButton kfactor = (RadioButton) mView.findViewById(R.id.kfactor);

        if (prod.isChecked()) {
            return PROD_URL;
        }
        if (dev.isChecked()) {
            return DEV_URL;
        }
        if (kfactor.isChecked()) {
            return KFACTOR_URL;
        }
        return null;
    }

    public View.OnClickListener reRegister(final View view) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                EditText customUrlET = (EditText) mView.findViewById(R.id.customUrl);
                String customUrl = customUrlET.getText().toString();
                if (null != customUrl) {
                    if (customUrl.isEmpty()) {
                        changeTargetUrl(getUrlFromRadioButtonSelection(mView));
                    } else {
                        changeTargetUrl(customUrl);
                    }
                } else {
                    changeTargetUrl(getUrlFromRadioButtonSelection(mView));
                }
                Context context = getActivity().getApplicationContext();
                SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putBoolean("install_state", false);
                e.putString("installid", null);
                e.commit();

                SharedPreferences aprefs = getActivity().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor ae = aprefs.edit();
                ae.putString(KEY_APP_KEY, "");
                ae.putString(KEY_SENDER_ID, "");
                ae.putBoolean(KEY_SETUP,false);
                ae.commit();

                // kill the app and let user reopen it
                getActivity().finish();
                System.exit(0);
            }
        };
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
