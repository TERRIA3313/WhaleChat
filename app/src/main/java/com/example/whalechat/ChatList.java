package com.example.whalechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "Chat_List";
    private String uid;
    private Button button_AddList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_chatting_list);

        ChattingListAdapter mAdapter = new ChattingListAdapter();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Rooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item :snapshot.getChildren()){
                    mAdapter.addItem(item.getValue(ChatModel.class), item.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewDecoration(10));

        // 채팅방 만들기 버튼 관련
        button_AddList = findViewById(R.id.AddList);

        button_AddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainIntent = new Intent(ChatList.this, Chat.class);
                startActivity(MainIntent);
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
            }
        });
    }

        //현재 개발중
    private void CreateRooms()
    {

    }
}