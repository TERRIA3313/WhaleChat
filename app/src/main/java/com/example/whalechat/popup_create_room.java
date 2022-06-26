package com.example.whalechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class popup_create_room extends Activity {
    EditText roomName;
    String uid;
    ChatModel chatModel;
    CipherModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_create_room);
        module = new CipherModule(getApplicationContext());

        roomName = findViewById(R.id.inputFriendsName);

        Intent intent = getIntent();
        uid = intent.getStringExtra("data");
    }
    
    public  void mOnClose(View v) throws Exception {
        chatModel = new ChatModel();
        chatModel.owner = uid;

        chatModel.name = roomName.getText().toString();
        String key = FirebaseDatabase.getInstance().getReference().child("Rooms").push().getKey();

        module.createSymmetricKey(key);

        SharedPreferences preferences = getSharedPreferences(key, MODE_PRIVATE);
        AESModel.key = preferences.getString("key", null);
        AESModel.iv = preferences.getString("iv", null);
        String cipherMessage = module.encryptAES("Welcome",AESModel.key, AESModel.iv);

        ChatModel.Comment comment = new ChatModel.Comment();
        comment.timestamp = 0;
        comment.uid = "server";
        comment.message = cipherMessage;
        comment.nickname = "server";
        chatModel.comments.put("welcome", comment);
        chatModel.users.put(uid, true);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Rooms/" + key, chatModel);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

        Intent intent = new Intent();
        intent.putExtra("key", key);
        setResult(Activity.RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
}