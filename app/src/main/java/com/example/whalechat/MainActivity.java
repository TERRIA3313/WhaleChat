package com.example.whalechat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText mEmailText, mPasswordText;
    private FirebaseAuth firebaseAuth;
    boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 파이어베이스 연동
        firebaseAuth =  FirebaseAuth.getInstance();

        Button button_sign_up = findViewById(R.id.Sign_Up);
        mEmailText = findViewById(R.id.user_id);
        mPasswordText = findViewById(R.id.user_password);

        loadData();

        if(save.ID != null && save.Password != null){
            mEmailText.setText(save.ID);
            mPasswordText.setText(save.Password);
            LogIn();
        }

        // 회원가입 화면으로 전환
        button_sign_up.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                Intent MainIntent = new Intent(MainActivity.this, signup.class);
                startActivity(MainIntent);
            }
        });

        // 로그인
        Button button_sign_in = findViewById(R.id.Sign_In);
        button_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn();
            }
        });
    }

    void loadData(){
        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
        save.ID = preferences.getString("ID", null);
        save.Password = preferences.getString("Password", null);
    }

    void LogIn(){
        String User_Id = mEmailText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(User_Id, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(isFirst){
                        save.ID=User_Id;
                        save.Password=password;

                        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("ID", save.ID);
                        editor.putString("Password", save.Password);

                        editor.apply();
                    }
                    Intent Sign_In_Intent = new Intent(MainActivity.this, ChatList.class);
                    startActivity(Sign_In_Intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "아이디나 비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}