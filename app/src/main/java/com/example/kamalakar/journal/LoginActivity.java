package com.example.kamalakar.journal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kamalakar.journal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    Button login,create_account;
    ProgressBar progressBar;
    EditText password;
    AutoCompleteTextView email;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=db.collection("Users");
    FirebaseAuth.AuthStateListener authStateListener;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("LoginActivity");
        getSupportActionBar().setElevation(0);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        progressBar=findViewById(R.id.progress_login);
        create_account=findViewById(R.id.register);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email=email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                signinuser(user_email,pass);

            }
        });
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });
    }

    private void signinuser(String user_email, String pass) {
        if(!TextUtils.isEmpty(user_email)&&!TextUtils.isEmpty(pass)){
            if(checkerrors()){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(user_email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        currentuser=mAuth.getCurrentUser();
                    final String userid=currentuser.getUid();
                    collectionReference.whereEqualTo("userId",userid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.d("error", "onEvent: "+e.toString());
                            }
                            if(!queryDocumentSnapshots.isEmpty()){
                                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                    JournalApi journalApi=JournalApi.getInstance();
                                    journalApi.setUsername(snapshot.getString("userName"));
                                    journalApi.setUserId(userid);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this,JournalListActivity.class));

                            }
                        }
                    });
                }
                else{ Toast.makeText(LoginActivity.this, "Sign in failed...", Toast.LENGTH_SHORT).show();}
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Sign in failed...Check your email and password", Toast.LENGTH_SHORT).show();

                }
            });
        }}
        else{
            Toast.makeText(this, "Empty fields not allowed", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkerrors() {
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString().trim();


        if(!pattern.matcher(mail).matches()){
            Toast.makeText(this, "Invalid mail", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(pass.length()<=5){
            Toast.makeText(this, "Not a valid password!", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
