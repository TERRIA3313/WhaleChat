package com.example.whalechat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private ArrayList<ChatModel> chatModels = new ArrayList<>();
    private ArrayList<String> chatKey = new ArrayList<>();
    private String uid;
    private String ownerUid;
    private CipherModule module;
    private Context context;

    public void addItem(ChatModel chatModel, String key){
        chatModels.add(chatModel);
        chatKey.add(key);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        //View view=inflater.inflate(R.layout.chatting_room,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room, parent, false);
        context = parent.getContext().getApplicationContext();
        module = new CipherModule(context);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ownerUid = chatModels.get(position).owner;

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
        load(chatKey.get(position));
        Log.d(TAG, "Key = " + chatKey.get(position));
        String message = null;
        try {
            message = module.decryptAES(chatModels.get(position).comments.get(lastMessageKey).message, AESModel.key, AESModel.iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myViewHolder.Message.setText(message);

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

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

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

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }

    public ChatModel getRoom(int position){
        return chatModels.get(position);
    }

    public String getKey(int position){
        return chatKey.get(position);
    }

    void load(String Key){
        SharedPreferences preferences = context.getSharedPreferences(Key, context.MODE_PRIVATE);
        AESModel.key = preferences.getString("key", null);
        AESModel.iv = preferences.getString("iv", null);
    }
}
