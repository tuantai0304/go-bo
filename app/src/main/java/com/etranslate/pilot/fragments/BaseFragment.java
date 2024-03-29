package com.etranslate.pilot.fragments;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.etranslate.pilot.R;
import com.etranslate.pilot.dto.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;

import java.util.List;

/**
 * Created by TuanTai on 1/04/2017.
 */

public abstract class BaseFragment extends Fragment
        implements ValidationListener {
    /* Variables */
    Resources res;
//    String s =  res.getString(R.string.f_ID);

    // Firebase instance variables
    protected FirebaseAuth mFirebaseAuth;
    protected FirebaseUser mFirebaseUser;
    protected DatabaseReference mFirebaseDatabaseReference;

    protected DatabaseReference m_dbUsers; /* Users db*/
    protected DatabaseReference m_dbUserInfo;
    protected DatabaseReference m_dbRooms;
    protected DatabaseReference m_dbRequest;
    protected DatabaseReference m_dbMessage;


    protected final String MODE_CHAT = "Real time chat";
    protected final String MODE_VIDEO = "Video Conference";
    protected final String MODE_IMAGE = "Image";

    public static final String ARG_ROOMID = "roomId";
    public static final String ARG_CREATE_OFFER = "createOffer";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();

        /* Get Firebase auth */
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        /* Get ref to Database*/
        m_dbUsers = mFirebaseDatabaseReference.child(
                res.getString(R.string.tbl_Users));

        m_dbUserInfo = mFirebaseDatabaseReference.child(
                res.getString(R.string.tbl_UserInfo));

        m_dbRooms = mFirebaseDatabaseReference.child(
                res.getString(R.string.tbl_Rooms));

        m_dbRequest = mFirebaseDatabaseReference.child(
                res.getString(R.string.tbl_Request));

        m_dbMessage = mFirebaseDatabaseReference.child(
                        res.getString(R.string.tbl_Message));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onValidationSucceeded() {

    }



    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
