package com.etranslate.pilot.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;

import com.etranslate.pilot.BaseActivity;
import com.etranslate.pilot.MainActivity;
import com.etranslate.pilot.R;
import com.etranslate.pilot.dto.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

public class RegisterActivity extends BaseActivity implements Validator.ValidationListener {

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
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
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

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            UserInfo info = new UserInfo(fname, lname, gender);

            m_dbUserInfo.push().setValue(info)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User account is created, but infomration not updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registered Success", Toast.LENGTH_SHORT).show();
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
