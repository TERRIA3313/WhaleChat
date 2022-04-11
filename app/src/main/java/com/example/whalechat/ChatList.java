package com.example.whalechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "Chat_List";
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<ChattingList> arrayList;
    private RecyclerView chat_list;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private View view;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        database = FirebaseDatabase.getInstance();

        chat_list=findViewById(R.id.ChattingList);
        ChattingListAdapter adapter = new ChattingListAdapter();

    }
}