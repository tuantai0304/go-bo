package com.etranslate.pilot;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

/**
 * Created by TuanTai on 31/03/2017.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements Validator.ValidationListener{


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
