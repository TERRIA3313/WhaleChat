package com.example.whalechat;

import java.util.HashMap;
import java.util.Map;

public class ChatModel
{
    public Map<String, ChatModel.Comment> comments = new HashMap<>();
    public String name;
    public String owner;
    public Map<String,Boolean> users = new HashMap<>();

    public static class Comment
    {
        public String message;
        public Object timestamp;
        public String uid;
    }
}
