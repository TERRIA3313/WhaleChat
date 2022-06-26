package com.example.whalechat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "Chat_List";
    private String uid;
    private Button button_AddList;
    private Button option;
    private CipherModule module;
    private String SymmetricKey;
    ChattingListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_chatting_list);
        module = new CipherModule(getApplicationContext());
        option = findViewById(R.id.Option_button);
        SharedPreferences preferences = getSharedPreferences("RSA", MODE_PRIVATE);
        if(preferences.getAll().isEmpty()) {
            module.createPublicKey();
            SharedPreferences newPreferences = getSharedPreferences("RSA", MODE_PRIVATE);
            loadRSA(newPreferences);
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("pubKey").setValue(RSAModel.publicKey);
        }
        else {
            loadRSA(preferences);
        }
        mAdapter = new ChattingListAdapter();
        // 현재 유저의 uid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadChatList();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDecoration(10));

        // 채팅방 만들기 버튼 관련
        button_AddList = findViewById(R.id.AddList);

        button_AddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatList.this, popup_create_room.class);
                intent.putExtra("data", uid);
                startActivityResult.launch(intent);
            }
        });

        // 옵션 버튼
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatList.this, com.example.whalechat.option.class);
                intent.putExtra("data", uid);
                startActivityResult.launch(intent);
            }
        });

        // 기존에 있는 채팅방 선택
        mAdapter.setOnItemClickListener(new ChattingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ChatModel Room = mAdapter.getRoom(position);
                String Key = mAdapter.getKey(position);
                Intent intent = new Intent(ChatList.this, Chat.class);
                intent.putExtra("key", Key);
                startActivity(intent);
                finish();
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String Key = result.getData().getStringExtra("key");
                        Log.d(TAG, "key = " + Key);
                        Intent MainIntent = new Intent(ChatList.this, Chat.class);
                        MainIntent.putExtra("key", Key);
                        startActivity(MainIntent);
                    }
                }
            });

    void loadChatList(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item :snapshot.getChildren()){
                    SharedPreferences preferences = getSharedPreferences(item.getKey(), MODE_PRIVATE);
                    if(preferences.getAll().isEmpty()){
                        getSymmetricKey(FirebaseDatabase.getInstance().getReference(), item.getKey());
                    }
                    mAdapter.addItem(item.getValue(ChatModel.class), item.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void loadRSA(SharedPreferences preferences){
        RSAModel.publicKey = preferences.getString("publicKey", null);
        RSAModel.privateKey = preferences.getString("privateKey", null);
    }

    void loadAES(SharedPreferences preferences){
        AESModel.key = preferences.getString("key", null);
        AESModel.iv = preferences.getString("iv", null);
    }

    void getSymmetricKey(DatabaseReference myRef, String key){
        myRef.child("Keys").child(key).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SymmetricModel symmetricModel = snapshot.getValue(SymmetricModel.class);
                SymmetricKey = symmetricModel.SymmetricKey;
                String decodedKey = module.decryptRSA(SymmetricKey, RSAModel.privateKey);
                SharedPreferences preferences = getSharedPreferences(key, MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();

                editor.putString("key", decodedKey);
                editor.putString("iv", decodedKey.substring(0, 16));

                loadAES(preferences);

                myRef.child("Keys").child(key).child(uid).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}