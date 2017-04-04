package com.etranslate.pilot.dto;

import java.util.List;

/**
 * Created by TuanTai on 4/04/2017.
 */

public class Room {

    String userId;
    String translatorID;
    Object timestamp;

    List<Message> messageList;

    public Room(String userId, String translatorID) {
        this.userId = userId;
        this.translatorID = translatorID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTranslatorID() {
        return translatorID;
    }

    public void setTranslatorID(String translatorID) {
        this.translatorID = translatorID;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
