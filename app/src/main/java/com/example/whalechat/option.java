package com.example.whalechat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class option extends AppCompatActivity {
    static private final String TAG = "OPTION";
    String uid;
    EditText editNickname;
    ImageView profileImage;
    Uri file;
    Boolean profileChange = false;
    UserModel chatModel;
    String nickname;
    private Intent intent;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        editNickname = findViewById(R.id.editNickname);
        profileImage = findViewById(R.id.editProfile);

        Intent uidIntent = getIntent();
        uid = uidIntent.getStringExtra("data");

        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // ?????? UID ??? ????????? ????????????
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModel = snapshot.getValue(UserModel.class);
                editNickname.setText(chatModel.name);
                nickname = chatModel.name;
                StorageReference storageRef = storage.getReference(chatModel.profileImage);
                storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Glide.with(getApplicationContext())
                                .load(task.getResult())
                                .into(profileImage);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // ????????? ????????? ??????
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityResult.launch(intent);;
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode()==RESULT_OK){
                                intent = result.getData();
                                file = intent.getData();
                                //???????????? ?????? ??????
                                profileImage.setImageURI(intent.getData());
                                profileChange = true;
                            }
                        }
                    }
            );
        });
    }

    public void onOkClose(View v){
        // ????????? ????????? ??????
        if(profileChange)
        {
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("Users/" + uid + ".png");
            UploadTask uploadTask = riversRef.putFile(file);

            try{
                InputStream in = getContentResolver().openInputStream(file);
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            mDatabase.child("Users").child(uid).child("profileImage").setValue("Users/" + uid + ".png");
        }

        // ????????? ??????
       if(!editNickname.getText().toString().equals(chatModel.name))
        {
            if(editNickname.getText().toString().equals(""))
            {
                Toast.makeText(option.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mDatabase.child("Users").orderByChild("name").equalTo(editNickname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() == null){
                            mDatabase.child("Users").child(uid).child("name").setValue(editNickname.getText().toString());
                        }
                        else{
                            Toast.makeText(option.this, "?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                changeNickname(nickname, editNickname.getText().toString());
            }
        }

        Log.d(TAG, "OK Close");
        finish();
    }

    public void onCancelClose(View v){
        //????????????(??????) ??????
        Log.d(TAG, "Cancel Close");
        Intent intent = new Intent(option.this, ChatList.class);
        startActivity(intent);
        finish();
    }

    public void logOff(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(option.this);
        builder.setTitle("????????????");
        builder.setMessage("???????????? ???????????????????");
        builder.setNegativeButton("?????????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.setPositiveButton("???",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();

                        PackageManager packageManager = getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                        ComponentName componentName = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                        startActivity(mainIntent);
                        System.exit(0);
                    }
                });
        builder.show();
    }

    public boolean onKeyDown(int keycode, KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(option.this, ChatList.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    void changeNickname(String before_name, String after_name){
        Log.d(TAG, before_name);
        FirebaseDatabase.getInstance().getReference().child("Rooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item1 :snapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference().child("Rooms/"+item1.getKey()+"/comments").orderByChild("nickname").equalTo(before_name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot item2 :snapshot.getChildren()){
                                FirebaseDatabase.getInstance().getReference().child("Rooms/" + item1.getKey() + "/comments/" +item2.getKey()+"/nickname").setValue(after_name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }
}