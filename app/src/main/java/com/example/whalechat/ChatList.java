package com.example.whalechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "Chat_List";

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private ArrayList<ChatModel> chatModels = new ArrayList<>();

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
                    mAdapter.addItem(item.getValue(ChatModel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //button_AddList = findViewById(R.id.AddList);
    }

        //현재 개발중
    private void CreateRooms()
    {
        button_AddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //채팅방의 해쉬맵 테이블을 등록
                HashMap<Object, String> hashMap = new HashMap<>();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Rooms");
            }
        });
    }
}