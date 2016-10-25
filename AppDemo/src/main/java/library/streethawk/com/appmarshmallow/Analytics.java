package library.streethawk.com.appmarshmallow;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Analytics.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Analytics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Analytics extends Fragment {


    private OnFragmentInteractionListener mListener;
    private View mView;

    public Analytics() {

    }

    public static Analytics newInstance(String param1, String param2) {
        Analytics fragment = new Analytics();
        return fragment;
    }

    private void showToast(String message){
        Toast.makeText(getActivity().getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView  = inflater.inflate(R.layout.fragment_analytics, container, false);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(backButton());
        Button nt = (Button) mView.findViewById(R.id.numerictagbtn);
        nt.setOnClickListener(numericTag());


        Button st = (Button) mView.findViewById(R.id.stringtag);
        st.setOnClickListener(stringTag());

        Button dt = (Button) mView.findViewById(R.id.datetimetag);
        dt.setOnClickListener(dateTimeTag());

        Button it = (Button) mView.findViewById(R.id.incrementTag);
        it.setOnClickListener(dateTimeTag());

        Button rt = (Button) mView.findViewById(R.id.removetag);
        dt.setOnClickListener(dateTimeTag());

        return mView;
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
        void onFragmentInteraction(Uri uri);
    }


    /**
     * OnClick listener for String tags
     * @return
     */
    private View.OnClickListener stringTag(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Context context = getActivity().getApplicationContext();
                EditText keyet =(EditText) mView.findViewById(R.id.stringkey);
                String key  = keyet.getText().toString();
                EditText valet =(EditText) mView.findViewById(R.id.stringval);
                String val  = valet.getText().toString();

                if(key==null){
                    showToast("Enter String Key");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter String Key");
                    return;
                }
                if(key==null){
                    showToast("Enter String value");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter String value");
                    return;
                }
                if(key.equals("sh_cuid"))
                    StreetHawk.INSTANCE.tagCuid(val);
                else
                    StreetHawk.INSTANCE.tagString(key,val);
                showToast("Tagged install "+key+" value "+val);
            }
        };
    }


    private View.OnKeyListener backButton(){
        return new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                }
                return false;
            }
        };

    }
    /**
     * onClick listener for numeric tags
     * @return
     */
    private View.OnClickListener numericTag(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Context context = getActivity().getApplicationContext();
                EditText keyet =(EditText) mView.findViewById(R.id.numerickey);
                String key  = keyet.getText().toString();
                EditText valet =(EditText) mView.findViewById(R.id.numericval);
                String val  = valet.getText().toString();

                if(key==null){
                    showToast("Enter Numeric key ");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter Numeric key ");
                    return;
                }
                if(key==null){
                    showToast("Enter Numeric value ");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter Numeric value ");
                    return;
                }

                int num;
                try{
                    num = Integer.parseInt(val);
                }catch (NumberFormatException e){
                    Toast.makeText(context,"Enter valid number ",Toast.LENGTH_LONG).show();
                    return;
                }
                StreetHawk.INSTANCE.tagNumeric(key,num);
                showToast("Tagged install "+key+" value "+num);
            }
        };
    }


    /**
     * on click listeenr for datetime tags
     * @return
     */
    private View.OnClickListener dateTimeTag(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Context context = getActivity().getApplicationContext();
                EditText keyet =(EditText) mView.findViewById(R.id.datetimekey);
                String key  = keyet.getText().toString();
                EditText valet =(EditText) mView.findViewById(R.id.datetimeval);
                String val  = valet.getText().toString();

                if(key==null){
                    showToast("Enter DateTime key");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter DateTime key");
                    return;
                }
                if(key==null){
                    showToast("Enter DateTime value");
                    return;
                }
                if(key.isEmpty()){
                    showToast("Enter DateTime value");
                    return;
                }
                StreetHawk.INSTANCE.tagDatetime(key,val);
                showToast("Tagged install "+key+" value "+val);
            }
        };
    }

    /**
     * on click listener for increment tags
     * @return
     */
    private View.OnClickListener incrementTag(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        };
    }


    /**
     * onclick listener for delete tag
     * @return
     */
    private View.OnClickListener deleteTag(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        };
    }


}
