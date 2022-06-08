package com.example.whalechat;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<ChatData> mDataset = new ArrayList<>();
    private String myNickName;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView TextView_Nickname;
        public TextView TextView_Message;
        public TextView TextView_Timestamp;
        public ImageView ImageView_ProfileImage;
        public String ProfileUri;
        public View rootView;

        public MyViewHolder(View v){
            super(v);
            TextView_Nickname = v.findViewById(R.id.TextView_Nickname);
            TextView_Message = v.findViewById(R.id.TextView_Message);
            TextView_Timestamp = v.findViewById(R.id.TextView_Timestamp);
            ImageView_ProfileImage = v.findViewById(R.id.ImageView_Profile);
            rootView = v;

            v.setClickable(true);
            v.setEnabled(true);
        }
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        ChatData chat = mDataset.get(position);
        holder.TextView_Nickname.setText(chat.getNickname());
        holder.TextView_Message.setText(chat.getMessage());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        long unixTime = (long) chat.getTimestamp();
        Date date = new Date(unixTime);
        holder.TextView_Timestamp.setText(simpleDateFormat.format(date));
        Log.d(TAG, "My Nickname is " + this.myNickName);
        if(chat.getNickname().equals(this.myNickName)){
            holder.TextView_Message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.TextView_Timestamp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.TextView_Nickname.setVisibility(View.INVISIBLE);
        }
        else if(chat.getNickname().equals("server")){
            holder.TextView_Message.setGravity(View.TEXT_ALIGNMENT_CENTER);
            holder.TextView_Message.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.TextView_Timestamp.setVisibility(View.INVISIBLE);
            holder.TextView_Nickname.setVisibility(View.INVISIBLE);
        }
        else{
            String Uid = chat.getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    .into(myViewHolder.ImageView_ProfileImage);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return mDataset==null ? 0 : mDataset.size();
    }

    public ChatData getChat(int position){
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(ChatData chat){
        mDataset.add(chat);
        notifyItemInserted(mDataset.size()-1);
    }

    public void setNickname(String Nickname){
        myNickName = Nickname;
    }
}
