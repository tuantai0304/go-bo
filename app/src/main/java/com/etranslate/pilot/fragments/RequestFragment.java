package com.etranslate.pilot.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.etranslate.pilot.R;
import com.etranslate.pilot.VideoConferenceActivity;
import com.etranslate.pilot.dto.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /* UI */
    Spinner spnSrcLang;
    Spinner spnTarLang;
    Spinner spnModes;
    Button btnRequest;
    ProgressDialog waitingTranslatorDialog;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        spnSrcLang = (Spinner) v.findViewById(R.id.spnSrcLang);
        spnTarLang = (Spinner) v.findViewById(R.id.spnTarLang);
        spnModes = (Spinner) v.findViewById(R.id.spnMode);
        btnRequest = (Button) v.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                waitingTranslatorDialog.show();
                requestTranslateService();
            }
        });

        waitingTranslatorDialog = new ProgressDialog(getActivity());
        waitingTranslatorDialog.setMessage("Please wait...");
        waitingTranslatorDialog.setTitle("Finding a translator");
        waitingTranslatorDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitingTranslatorDialog.setCanceledOnTouchOutside(false);

        return v;
    }

    private void requestTranslateService() {
        String srcLang = spnSrcLang.getSelectedItem().toString();
        String tarLang = spnTarLang.getSelectedItem().toString();
        String mode = spnModes.getSelectedItem().toString();

        if (srcLang.equals(tarLang)) {
            Toast.makeText(getActivity(), "Source Language and Target Language cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }



        Request req = new Request(srcLang, tarLang, mode, null, null, mFirebaseUser.getUid());
//        req.setUser(mFirebaseUser);

        /* Add to database */
        final DatabaseReference new_request_ref = m_dbRequest.push();
        String key = new_request_ref.getKey();

        new_request_ref.setValue(req)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity().getApplicationContext(), "New request created", Toast.LENGTH_SHORT).show();
                        }


                        waitingTranslatorDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel request", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new_request_ref.removeValue();
                            }
                        });

                        waitingTranslatorDialog.show();

                    }
                });

        /*
        *
        *
        * Set listener for accept status
        *
        * */
        new_request_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Request req = dataSnapshot.getValue(Request.class);
                if (req != null) {
                    String status = req.getAcceptStatus();
                    String mode = req.getMode();
                    if (status.equals("accepted")) {
                        /* TODO: Change to chat UI changing here*/
                        waitingTranslatorDialog.dismiss();

                        /* Get roomKey */
                        req.getRoomId();
                        Bundle b = new Bundle();
                        b.putString(ARG_ROOMID, req.getRoomId());

                        if (!mode.equals(MODE_VIDEO)) {
                            /* Change to chat UI screen */
                            ChatUIFragment chatUIFragment = new ChatUIFragment();
                            chatUIFragment.setArguments(b);
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction tx = fm.beginTransaction();
                            tx.replace(R.id.content_main , chatUIFragment).addToBackStack(null);
                            tx.commit();
                        } else {
                            Intent intent = new Intent(getContext(), VideoConferenceActivity.class);
                            intent.putExtra(ARG_ROOMID, req.getRoomId());
                            intent.putExtra(ARG_CREATE_OFFER, true);
                            startActivity(intent);
                        }

                    }
                    Log.i("onDataChange", "onDataChange: " + req.getAcceptStatus());
                    Log.i("RoomID", "onDataChange: " + req.getRoomId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
