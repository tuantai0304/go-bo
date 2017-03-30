package com.etranslate.pilot.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.etranslate.pilot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
//    @Email(message = "Please enter an valid email")
    EditText edtEmail;

//    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS)
    EditText edtPassword;
    EditText edtLName;
    EditText edtFName;


    Spinner spnGender;
    Button btnRegister;
    ProgressBar progressBar;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    private  String Uid;

    /* Validator */
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtLName = (EditText) findViewById(R.id.edtLname);
        edtFName = (EditText) findViewById(R.id.edtFname);
        spnGender = (Spinner) findViewById(R.id.spnGenders);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /* Set validation */
        validator = new Validator(this);
        validator.setValidationListener(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onValidationSucceeded() {
        registerNewUser();
    }

    private void registerNewUser() {
        /* Set progressbar visible */
        progressBar.setVisibility(ProgressBar.VISIBLE);

        /* Register new user with Firebase */
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    if (task.isSuccessful()) {
                        // TODO: Read response in task to display meaningful message
                        updateCurrentUserInformation();
//                        Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    private void updateCurrentUserInformation() {
        String fname = edtFName.getText().toString().trim();
        String lname = edtLName.getText().toString().trim();
        String gender = spnGender.getSelectedItem().toString();

//        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
//                .

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

//        UserProfileChangeRequest request = mFirebaseUser.getUid();
        if (mFirebaseUser != null) {
            Uid = mFirebaseUser.getUid();
            UserInfo info = new UserInfo(fname, lname, gender);
            mFirebaseDatabaseReference.child("UserInfo").child(Uid).setValue(info)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User account is created, but infomration not updated", Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(getApplicationContext(), "Registered Success", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
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
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
