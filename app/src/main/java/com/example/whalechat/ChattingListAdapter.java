package com.example.whalechat;

import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChattingListAdapter extends RecyclerView.Adapter<ChattingListAdapter.MyViewHolder> {
    private static final String TAG = "ChattingListAdapter";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    private ArrayList<ChatModel> chatModels = new ArrayList<>();
    private String uid;
    private String ownerUid;

    public void addItem(ChatModel chatModel){
        chatModels.add(chatModel);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.chatting_room,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ownerUid = chatModels.get(position).owner;
        Log.d(TAG, "오너 UID : " + ownerUid);

        FirebaseDatabase.getInstance().getReference().child("Users").child(ownerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel chatModel = snapshot.getValue(UserModel.class);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference(chatModel.profileImage);
                storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(myViewHolder.itemView.getContext())
                                .load(task.getResult())
                                .apply(new RequestOptions().circleCrop())
                                .into(myViewHolder.profile);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
        commentMap.putAll(chatModels.get(position).comments);
        String lastMessageKey = (String) commentMap.keySet().toArray()[0];
        myViewHolder.Message.setText(chatModels.get(position).comments.get(lastMessageKey).message);


        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
        Date date = new Date(unixTime);
        myViewHolder.LastChatTime.setText(simpleDateFormat.format(date));
        myViewHolder.LastChatTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        myViewHolder.LastChatTime.setGravity(View.TEXT_ALIGNMENT_CENTER);

        String RoomName = chatModels.get(position).name;
        myViewHolder.RoomName.setText(RoomName);
        myViewHolder.RoomName.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView RoomName;
        private TextView Message;
        private TextView LastChatTime;
        private ImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            RoomName =itemView.findViewById(R.id.RoomName);
            RoomName.setTextSize(20);
            Message=itemView.findViewById(R.id.Message);
            LastChatTime=itemView.findViewById(R.id.LastChatTime);
            profile=itemView.findViewById(R.id.profile);
        }
    }

}
