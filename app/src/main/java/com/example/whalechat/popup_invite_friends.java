package com.example.whalechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class popup_invite_friends extends Activity {
    final static String TAG = "Invite_Friends";
    EditText inputNickname;
    Intent intent;
    String Key;
    CipherModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_invite_friends);
        intent = getIntent();
        Key = intent.getStringExtra("key");
        module = new CipherModule(getApplicationContext());
        inputNickname = findViewById(R.id.inputFriendsName);
    }

    public void mOnClose(View v){
        String name = inputNickname.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) Toast.makeText(popup_invite_friends.this, "없는 사용자 입니다.", Toast.LENGTH_SHORT).show();
                else{
                    for(DataSnapshot item:snapshot.getChildren()){
                        UserModel userModel = item.getValue(UserModel.class);
                        String uid = userModel.uid;
                        FirebaseDatabase.getInstance().getReference().child("Rooms").child(Key).child("users").child(uid).setValue(true);

                        SharedPreferences preferences = getSharedPreferences(Key, MODE_PRIVATE);
                        loadAES(preferences);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();

                        String encryptedKey = module.encryptRSA(AESModel.key,userModel.pubKey);
                        FirebaseDatabase.getInstance().getReference().child("Keys").child(Key).child(uid).child("SymmetricKey").setValue(encryptedKey);
                        Toast.makeText(popup_invite_friends.this, "친구 초대 완료.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadAES(SharedPreferences preferences){
        AESModel.key = preferences.getString("key", null);
        AESModel.iv = preferences.getString("iv", null);
    }
}