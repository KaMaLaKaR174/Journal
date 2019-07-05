package com.example.kamalakar.journal;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamalakar.journal.model.Journal;
import com.example.kamalakar.journal.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERYCODE = 1 ;
    Button save;
    ImageView addphoto,imageView;
    EditText post_title,thoughts;
    TextView post_username,post_date;
    ProgressBar progressBar;

    String username,userId;

    FirebaseUser currentuser;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=db.collection("Journal");
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        getSupportActionBar().setTitle("Post a Journal");
        getSupportActionBar().setElevation(0);
        save=findViewById(R.id.save);
        post_title=findViewById(R.id.post_title);
        thoughts=findViewById(R.id.post_thought);
        post_username=findViewById(R.id.post_username);
        post_date=findViewById(R.id.post_date);
        progressBar=findViewById(R.id.progressBar);
        addphoto=findViewById(R.id.camera);
        imageView=findViewById(R.id.post_imageView);
        mAuth=FirebaseAuth.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        currentuser=mAuth.getCurrentUser();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentuser=mAuth.getCurrentUser();
                if(currentuser!=null){

                }
                else{

                }
            }
        };
        save.setOnClickListener(this);
        addphoto.setOnClickListener(this);

        if(JournalApi.getInstance()!=null){
            username=JournalApi.getInstance().getUsername();
            userId=JournalApi.getInstance().getUserId();
            post_username.setText(username);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser=mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.camera:
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERYCODE);
                break;
            case R.id.save:
                savejournal();
                break;
        }
    }

    private void savejournal() {
        String title=post_title.getText().toString().trim();
        String thought=thoughts.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(thought)&&imageUri!=null){
            final StorageReference filepath=storageReference.child("journal_images").child("my Image_"+Timestamp.now().getSeconds());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageurl=uri.toString();
                            Journal journal=new Journal();
                            journal.setTitle(post_title.getText().toString().trim());
                            journal.setThoughts(thoughts.getText().toString().trim());
                            journal.setUsername(username);
                            journal.setUserId(userId);
                            journal.setImageurl(imageurl);
                            journal.setTimestamp(new Timestamp(new Date()));

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PostJournalActivity.this, "Successfully added a post!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));
                                    //finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERYCODE&&resultCode==RESULT_OK){
            if(data!=null){
                imageUri=data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

}

