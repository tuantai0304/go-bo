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

    private String ID;
    private String srcLang;
    private String tarLang;
    private String mode;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    private String roomId;

    private String acceptStatus;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String userID;

    private Object timestamp;

//    private FirebaseUser user;
//    private FirebaseUser translator;

    private String translatorName;
    private String translatorEmail;
    private String translatorUid;


    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Request(String Id, String srcLang, String tarLang, String mode, FirebaseUser user, FirebaseUser translator) {
        this.srcLang = srcLang;
        this.tarLang = tarLang;
        this.mode = mode;
//        this.user = user;
        this.acceptStatus = "new";
//        this.translator = translator;
        setID(Id);
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Request(String srcLang, String tarLang, String mode, FirebaseUser user, FirebaseUser translator, String userId) {
        this.srcLang = srcLang;
        this.tarLang = tarLang;
        this.mode = mode;
//        this.user = user;
        this.acceptStatus = "new";
//        this.translator = translator;
        this.timestamp = ServerValue.TIMESTAMP;
        setUserID(userId);
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

//    @Exclude
//    public FirebaseUser getUser() {
//        return user;
//    }
//
//    public void setUser(FirebaseUser user) {
//        this.user = user;
//    }
//
//    @Exclude
//    public FirebaseUser getTranslator() {
//        return translator;
//    }
//
//    public void setTranslator(FirebaseUser translator) {
//        this.translator = translator;
//        if (translator != null) {
//            acceptStatus = "accepted";
//        }
//    }

    public String getID() {
        return ID;
    }

    public String getTranslatorName() {
        return translatorName;
    }

    public void setTranslatorName(String translatorName) {
        this.translatorName = translatorName;
    }

    public String getTranslatorEmail() {
        return translatorEmail;
    }

    public void setTranslatorEmail(String translatorEmail) {
        this.translatorEmail = translatorEmail;
    }

    public String getTranslatorUid() {
        return translatorUid;
    }

    public void setTranslatorUid(String translatorUid) {
        this.translatorUid = translatorUid;
    }
}
