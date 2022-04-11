package com.example.whalechat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChattingListAdapter extends RecyclerView.Adapter<ChattingListAdapter.MyViewHolder> {
    private static final String TAG = "ChattingListAdapter";

    private ArrayList<ChattingList> items=new ArrayList<>();

    public void additem(ChattingList chatlist){
        items.add(chatlist);
    }

    @NotNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Log.d(TAG, "onCreateViewHolder");
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.chatting_room,parent,false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+position);
        ChattingList chatlist=items.get(position);
        holder.setItem(chatlist);
    }

    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //규칙1
        private TextView RoomName;
        private TextView Message;
        private TextView LastChatTime;
        private TextView profileData;
        private ImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //규칙2
            RoomName=itemView.findViewById(R.id.RoomName);
            Message=itemView.findViewById(R.id.Message);
            LastChatTime=itemView.findViewById(R.id.LastChatTime);
            profile=itemView.findViewById(R.id.profile);
        }

        //규칙3
        public void setItem(ChattingList chatlist){
            Log.d(TAG, "MyViewHolder: ");
            RoomName.setText(chatlist.getRoomName());
            Message.setText(chatlist.getMessage());
            LastChatTime.setText(chatlist.getLastChatTime());
            profileData.setText(chatlist.getProfileImage());
            Glide.with(itemView).load(profileData).into(profile);
        }
    }

}
