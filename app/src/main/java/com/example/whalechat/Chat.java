package com.example.whalechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {
    private static final String TAG = "Chat";
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth firebaseAuth;
    private String Nickname;
    private CipherModule module;
    Button push_button;
    Button add_button;
    String Key;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Key = intent.getStringExtra("key");

        module = new CipherModule(getApplicationContext());
        load(Key);

        //파이어베이스 초기화
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        push_button = findViewById(R.id.push_button);
        message = findViewById(R.id.input_text);

        //리사이클러 뷰 등록
        mRecyclerView = findViewById(R.id.chat_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //레이아웃 매니저 등록
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //리스너 등록
        mAdapter = new ChatAdapter();
        //닉네임을 파이어베이스로 부터 가져오는 코드
        myRef.child("Users").child(user.getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Nickname = snapshot.getValue(String.class);
                mAdapter.setNickname(Nickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        //채팅방을 열었을 때
        myRef.child("Rooms").child(Key).child("comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mAdapter.addChat(snapshot.getValue(ChatData.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        push_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message.getText().toString().length() > 0){
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.nickname = Nickname;
                    comment.timestamp = System.currentTimeMillis();
                    comment.uid = user.getUid();
                    try {
                        comment.message = module.encryptAES(message.getText().toString(), AESModel.key, AESModel.iv);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String messageKey = myRef.child("Rooms").child(Key).child("comments").push().getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/Rooms/" + Key + "/comments/" + messageKey, comment);
                    if(myRef.child("Rooms").child(Key).child("comments").child("welcome").getKey() != null){
                        try{
                            myRef.child("Rooms").child(Key).child("comments").child("welcome").setValue(null);
                        }
                        catch(Exception e) {
                            Log.d(TAG, "ERROR : " + e);
                        }
                    }
                    myRef.updateChildren(childUpdates);
                    message.setText("");
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount());

                }
            }
        });

        add_button = findViewById(R.id.add_friends);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat.this, popup_invite_friends.class);
                String key = Key;
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });
    }

    void load(String Key){
        SharedPreferences preferences = getSharedPreferences(Key, MODE_PRIVATE);
        AESModel.key = preferences.getString("key", null);
        AESModel.iv = preferences.getString("iv", null);
    }

    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Chat.this, ChatList.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }
}