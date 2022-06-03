package com.example.whalechat;

import java.io.Serializable;

public class ChatData implements Serializable {
    private String message;
    private String nickname;
    private Long timestamp;
    private String uid;

    public String getMessage(){
        return message;
    }

    public void setMessage(String Message){
        this.message = Message;
    }

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String Nickname){
        this.nickname = Nickname;
    }

    public Object getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Long Timestamp){
        this.timestamp = Timestamp;
    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
}
