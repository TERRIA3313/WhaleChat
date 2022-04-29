package com.example.whalechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "Chat_List";
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<ChattingList> arrayList;
    private RecyclerView chat_list;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button button_AddList;
    private FirebaseAuth firebaseAuth;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        database = FirebaseDatabase.getInstance();

        chat_list=findViewById(R.id.ChattingList);
        ChattingListAdapter adapter = new ChattingListAdapter();

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

    private void CheckChatRooms()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        //database.getReference().child("Rooms").orderByChild("Users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener()
    }
}