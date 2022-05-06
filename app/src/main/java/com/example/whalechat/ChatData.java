package com.example.whalechat;

import java.io.Serializable;

public class ChatData implements Serializable {
    private String Message;
    private String Nickname;
    private String Timestamp;
    private String ProfileUri;

    public String getMessage(){
        return Message;
    }

    public void setMessage(String Message){
        this.Message = Message;
    }

    public String getNickname(){
        return Nickname;
    }

    public void setNickname(String Nickname){
        this.Nickname = Nickname;
    }

    public String getTimestamp(){
        return Timestamp;
    }

    public void setTimestamp(String Timestamp){
        this.Timestamp = Timestamp;
    }

    public String getProfileUri(){
        return ProfileUri;
    }

    public void setProfileUri(String ProfileUri){
        this.ProfileUri = ProfileUri;
    }
}
