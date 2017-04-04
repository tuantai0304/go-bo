package com.etranslate.pilot.dto;

import com.google.firebase.database.ServerValue;

/**
 * Created by TuanTai on 1/04/2017.
 */

public class Message {

//    public String[] STATUS = {"accepted", "rejected", "done", "ongoing", "new"};

    private String content;
    private String type;
    private String senderID;



    private String name;


    private String photoUrl;
    private String roomId;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
    private Object timestamp;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Message(String content, String type, String imageUrl, String photoUrl, String roomID, String senderID, String name) {
        this.content = content;
        this.type = type;
        this.senderID = senderID;
        this.roomId = roomID;
        setPhotoUrl(photoUrl);
        setImageUrl(imageUrl);
        setName(name);
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Message() {

    }

    public Message(String imageUrl, String userID) {
        setSenderID(userID);
        setImageUrl(imageUrl);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
