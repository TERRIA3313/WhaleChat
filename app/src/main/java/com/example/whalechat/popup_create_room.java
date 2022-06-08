package com.example.whalechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class popup_create_room extends Activity {
    EditText roomName;
    String uid;
    ChatModel chatModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_create_room);

        roomName = findViewById(R.id.inputRoomName);

        Intent intent = getIntent();
        uid = intent.getStringExtra("data");
        chatModel = new ChatModel();
        chatModel.owner = uid;
        ChatModel.Comment comment = new ChatModel.Comment();
        comment.timestamp = 0;
        comment.uid = "server";
        comment.message = "Welcome";
        comment.nickname = "server";
        chatModel.comments.put("welcome", comment);
        chatModel.users.put(uid, true);
    }
    
    public  void mOnClose(View v) {
        chatModel.name = roomName.getText().toString();
        String key = FirebaseDatabase.getInstance().getReference().child("Rooms").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Rooms/" + key, chatModel);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

        Toast.makeText(this, "키 : " + key, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("key", key);
        setResult(Activity.RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
}