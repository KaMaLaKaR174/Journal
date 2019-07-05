package com.example.kamalakar.journal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamalakar.journal.model.Journal;
import com.example.kamalakar.journal.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {

    TextView nothoughts;
    FirebaseUser currentuser;
    FirebaseAuth mAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    StorageReference storageReference;
    CollectionReference collectionReference=db.collection("Journal");
    FirebaseAuth.AuthStateListener authStateListener;
    List<Journal> journals;
    RecyclerView recyclerView;
    ListViewAdapter recyclerviewadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Your Journals");
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nothoughts=findViewById(R.id.list_nothoughts);
        journals=new ArrayList<>();
        Toast.makeText(this, "Hello "+JournalApi.getInstance().getUsername()+".Great to see you!!", Toast.LENGTH_LONG).show();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logout:
                if(mAuth!=null&&currentuser!=null){
                    mAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                    finish();
                }

                break;
            case R.id.add:
                if(mAuth!=null&&currentuser!=null){
                    startActivity(new Intent(JournalListActivity.this,PostJournalActivity.class));

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser=mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);
        collectionReference.whereEqualTo("userId",JournalApi.getInstance().getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    journals.clear();
                    for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                        Journal journal=snapshot.toObject(Journal.class);
                        journals.add(journal);

                    }
                    recyclerviewadapter=new ListViewAdapter(JournalListActivity.this,journals);
                    recyclerView.setAdapter(recyclerviewadapter);
                    recyclerviewadapter.notifyDataSetChanged();
                }
                else{
                    nothoughts.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
