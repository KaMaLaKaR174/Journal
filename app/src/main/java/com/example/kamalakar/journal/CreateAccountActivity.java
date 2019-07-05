package com.example.kamalakar.journal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kamalakar.journal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    EditText userName,password_reg,email_reg;
    Button create_account;
    ProgressBar progressBar;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    FirebaseAuth mAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=db.collection("Users");
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setElevation(0);
        mAuth=FirebaseAuth.getInstance();

        email_reg=findViewById(R.id.email_reg);
        userName=findViewById(R.id.user_name);
        password_reg=findViewById(R.id.password_reg);
        create_account=findViewById(R.id.create_account);
        progressBar=findViewById(R.id.progressbar);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentuser=firebaseAuth.getCurrentUser();
                if(currentuser!=null){

                }
                else{

                }
            }
        };
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(userName))
                String email=email_reg.getText().toString().trim();
                String password=password_reg.getText().toString().trim();
                String username=userName.getText().toString().trim();
                createAccount(email,password,username);
            }
        });
    }

    private void createAccount(String email, String password, final String userName) {
        if(!email.equals("")&&!password.equals("")&&!userName.equals("")){
            if(checkerrors()){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull  Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentuser = mAuth.getCurrentUser();
                                Map<String, String> userinfo = new HashMap<>();
                                userinfo.put("userId", currentuser.getUid());
                                userinfo.put("userName", userName);
                                collectionReference.add(userinfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        if (task!=null&&task.getResult().exists()) {
                                                            String name = task.getResult().getString("userName");
                                                            JournalApi journalApi=JournalApi.getInstance();
                                                            journalApi.setUserId(currentuser.getUid());
                                                            journalApi.setUsername(name);

                                                            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else{
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateAccountActivity.this, "Sign up failed!!",Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

        }}
        else{
            Toast.makeText(this, "Empty fields not allowed", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkerrors() {
        String mail=email_reg.getText().toString().trim();
        String pass=password_reg.getText().toString().trim();
        String username=userName.getText().toString().trim();
        if(username.length()<=3){
            Toast.makeText(this, "User name is very short.Must be atleast 4 characters!", Toast.LENGTH_SHORT).show();
            return false;

        }
        else if(!pattern.matcher(mail).matches()){
            Toast.makeText(this, "Invalid mail", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(pass.length()<=5){
            Toast.makeText(this, "Password is too short!Must be atleast 6 characters long!", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentuser=mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }
}
