package library.streethawk.com.appmarshmallow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.push.Push;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Setup.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Setup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Setup extends Fragment implements Constants {
    View mView = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Setup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Setup.
     */
    // TODO: Rename and change types and number of parameters
    public static Setup newInstance(String param1, String param2) {
        Setup fragment = new Setup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setup2, container, false);
        Button proceed = (Button) mView.findViewById(R.id.setupsave);
        Button registerAppKeyWithSH = (Button) mView.findViewById(R.id.registerAppKey);
        registerAppKeyWithSH.setOnClickListener(registerAppKey(mView));
        proceed.setOnClickListener(saveRegisterParams(mView));
        return mView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Function opens webview to register with StreetHawk
     */
    public View.OnClickListener registerAppKey(View view) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent docs = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dashboard.streethawk.com/static/bb/#signup"));
                startActivity(docs);
            }
        };
    }

    /**
     * Function stores setup params in database
     *
     * @param view
     */
    public View.OnClickListener saveRegisterParams(View view) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String appKey = null;
                String senderid = null;
                EditText app_keyET = (EditText) getActivity().findViewById(R.id.appkey);
                EditText senderidET = (EditText) getActivity().findViewById(R.id.senderid);
                if (null != app_keyET)
                    appKey = app_keyET.getText().toString();
                if (appKey.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.ERRMSG_EMPTY_APP_KEY), Toast.LENGTH_LONG).show();
                    return;
                }
                if (appKey.startsWith("demo")) {
                    Toast.makeText(getActivity(), getString(R.string.ERRMSG_DEMO_APP_KEY), Toast.LENGTH_LONG).show();
                    return;
                }

                if (null != senderidET) {
                    senderid = senderidET.getText().toString();
                }
                SharedPreferences prefs = getActivity().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putString(KEY_APP_KEY, appKey);
                if (null != senderid) {
                    if (!(senderid.isEmpty()))
                        Push.getInstance(getActivity().getApplicationContext()).registerForPushMessaging(senderid);
                        e.putString(KEY_SENDER_ID, senderid);
                }
                e.putBoolean(KEY_SETUP, true);
                e.commit();
                Push.getInstance(getActivity().getApplicationContext()).registerForPushMessaging("491295755890");
                StreetHawk.INSTANCE.setAppKey(appKey);
                StreetHawk.INSTANCE.init(getActivity().getApplication());
                ShowAppOptions options = new ShowAppOptions();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, options).commit();
            }
        };
    }
}
