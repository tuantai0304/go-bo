package com.etranslate.pilot.dto;

import java.util.ArrayList;

/**
 * Created by TuanTai on 30/03/2017.
 */
public class UserInfo {

    public String fname;
    public String lname;
    public String gender;
    public ArrayList<String> messages = new ArrayList<String>();

    public UserInfo() {

    }

    public UserInfo(String fname, String lname, String gender) {
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        messages.add("jhjhj");
        messages.add("jhjhj");
    }
}
