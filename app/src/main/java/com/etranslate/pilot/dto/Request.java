package com.etranslate.pilot.dto;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Objects;

/**
 * Created by TuanTai on 1/04/2017.
 */

public class Request {

//    public String[] STATUS = {"accepted", "rejected", "done", "ongoing", "new"};

    private String srcLang;
    private String tarLang;
    private String mode;

    private String acceptStatus;

    private Object timestamp;

    private FirebaseUser user;
    private FirebaseUser translator;

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Request(String srcLang, String tarLang, String mode, FirebaseUser user, FirebaseUser translator) {
        this.srcLang = srcLang;
        this.tarLang = tarLang;
        this.mode = mode;
        this.user = user;
        this.acceptStatus = "new";
        this.translator = translator;

        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Request() {

    }

    public String getSrcLang() {
        return srcLang;
    }

    public void setSrcLang(String srcLang) {
        this.srcLang = srcLang;
    }

    public String getTarLang() {
        return tarLang;
    }

    public void setTarLang(String tarLang) {
        this.tarLang = tarLang;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    @Exclude
    public FirebaseUser getUser() {
        return user;
    }


    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    @Exclude
    public FirebaseUser getTranslator() {
        return translator;
    }

    public void setTranslator(FirebaseUser translator) {
        this.translator = translator;
        if (translator != null) {
            acceptStatus = "accepted";
        }
    }
}
