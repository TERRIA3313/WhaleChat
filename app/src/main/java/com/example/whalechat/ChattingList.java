package com.example.whalechat;

public class ChattingList {
    private String RoomName;
    private String Message;
    private String LastChatTime;
    private String ProfileImage;

    public ChattingList(String RoomName, String Message, String LastChatTime, String ProfileIamge){
        this.RoomName = RoomName;
        this.Message = Message;
        this.LastChatTime = LastChatTime;
        this.ProfileImage = ProfileIamge;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getLastChatTime() {
        return LastChatTime;
    }

    public void setLastChatTime(String LastChatTime) {
        this.LastChatTime = LastChatTime;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String ProfileImage) {
        this.ProfileImage = ProfileImage;
    }
}
