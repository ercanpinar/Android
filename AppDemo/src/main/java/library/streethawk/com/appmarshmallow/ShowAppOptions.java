package library.streethawk.com.appmarshmallow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowAppOptions.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowAppOptions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowAppOptions extends Fragment {

        int ANALYTICS   = 0;
        int GROWTH      = ANALYTICS + 1;
        int PUSH        = GROWTH + 1;
        int BEACONS     = PUSH + 1;
        int GEOFENCE    = BEACONS + 1;
        int LOCATIONS   = GEOFENCE + 1;
        int FEEDS       = LOCATIONS + 1;
        int SETTINGS    = FEEDS + 1;
        int OLDAPP      = SETTINGS+1;


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    private ListView mListView = null;
    private View mView = null;
    private final String[] mOptions = new String[]{
            "Analytics",
            "Growth",
            "Push",
            "Beacons",
            "Geofence",
            "Locations",
            "Feeds",
            "Settings",
            "Old App"
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShowAppOptions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowAppOptions.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowAppOptions newInstance(String param1, String param2) {
        ShowAppOptions fragment = new ShowAppOptions();
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
        mView = inflater.inflate(R.layout.fragment_show_app_options, container, false);
        mListView = (ListView)mView.findViewById(R.id.appOptions);
        final ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<mOptions.length;i++){
            list.add(mOptions[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
                R.layout.customlistview, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(optionsOnclickListener());
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

    public AdapterView.OnItemClickListener optionsOnclickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(ANALYTICS==position){
                    Analytics analytics = new Analytics();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, analytics).commit();
                }
                if(GROWTH==position){

                }
                if(PUSH==position){

                }
                if(BEACONS==position){

                }
                if(GEOFENCE==position){

                }
                if(LOCATIONS==position){

                }
                if(FEEDS==position){

                }
                if(SETTINGS==position){
                    Settings settings = new Settings();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, settings).commit();
                }
                if(OLDAPP==position){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
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
}
