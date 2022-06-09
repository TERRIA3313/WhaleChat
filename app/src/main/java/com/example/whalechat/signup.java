package com.example.whalechat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;


public class signup extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private static final String TAG = "Register";
    EditText mIdText, mPasswordText, mPasswordcheckText, mNickname;
    private boolean profileChange;
    private ImageView profileImage;
    private Intent intent;
    private Uri file;
    private boolean equalName;
    private CipherModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        // 파이어베이스 연동
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        mIdText = findViewById(R.id.user_id);
        mPasswordText = findViewById(R.id.user_password);
        mPasswordcheckText = findViewById(R.id.user_passwordcheck);
        mNickname = findViewById(R.id.user_nickname);
        profileImage = findViewById(R.id.profileImage);
        profileChange = false;
        equalName = true;
        module = new CipherModule(getApplicationContext());

        //사진 변경 클릭
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
                                //이미지뷰 사진 변경
                                profileImage.setImageURI(intent.getData());
                                profileChange = true;
                            }
                        }
                    }
            );
        });

        Button button_sign_up = findViewById(R.id.Sign_Up);
        // 회원가입 버튼 클릭시
        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 정보 획득
                final String User_Id = mIdText.getText().toString().trim();
                String password = mPasswordText.getText().toString().trim();
                String passwordcheck = mPasswordcheckText.getText().toString().trim();

                // 파이어베이스 자체 보안으로 비밀번호를 6자 이상으로 요구함
                if(password.length() < 6) {
                    Toast.makeText(signup.this, "비밀번호가 너무 짧습니다.(6자 이상)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 패스워드와 패스워드 재입력 부분이 일치할 경우
                if(password.equals(passwordcheck)){
                    Log.d(TAG, "등록 버튼 " + User_Id + " , " + password);

                    SharedPreferences preferences = getSharedPreferences("RSA", MODE_PRIVATE);
                    if(preferences.getAll().isEmpty()) {
                        module.createPublicKey();
                        SharedPreferences newPreferences = getSharedPreferences("RSA", MODE_PRIVATE);
                        loadRSA(newPreferences);
                    }
                    else {
                        loadRSA(preferences);
                    }

                    // 파이어베이스에 유저 정보 등록
                    firebaseAuth.createUserWithEmailAndPassword(User_Id + "@gmail.com", password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //가입 성공
                            if(task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String User_Id = user.getEmail();
                                String uid = user.getUid();
                                String name = mNickname.getText().toString().trim();

                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", User_Id);
                                hashMap.put("name",name);
                                hashMap.put("pubKey", RSAModel.publicKey);

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
                                    hashMap.put("profileImage", "Users/" + uid + ".png");
                                }
                                else hashMap.put("profileImage", "Users/default.png");

                                //해쉬맵 테이블을 파이어베이스에 저장
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                assert User_Id != null;
                                save.ID=User_Id.substring(0, User_Id.length()-10);
                                save.Password=password;

                                SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("ID", save.ID);
                                editor.putString("Password", save.Password);

                                editor.apply();

                                //회원가입 성공시
                                Intent Sign_Up_Intent = new Intent(signup.this, MainActivity.class);
                                startActivity(Sign_Up_Intent);
                                finish();
                                Toast.makeText(signup.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                            }

                            // 비밀번호는 일치하는데 오류가 나는 경우 ex) 아이디 중복, 서버 오류 등등
                            else{
                                Toast.makeText(signup.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
                // 비밀번호 불일치시
                else{
                    Toast.makeText(signup.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    void loadRSA(SharedPreferences preferences){
        RSAModel.publicKey = preferences.getString("publicKey", null);
        RSAModel.privateKey = preferences.getString("privateKey", null);
    }
}