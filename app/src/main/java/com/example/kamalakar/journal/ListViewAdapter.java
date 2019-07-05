package com.example.kamalakar.journal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kamalakar.journal.model.Journal;
import com.example.kamalakar.journal.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.MyViewHolder> {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=db.collection("Journal");
    StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    FirebaseUser currentuser=mAuth.getCurrentUser();

    Context context;
    List<Journal> journalList;
    String imageurl;

    public ListViewAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journal_row,viewGroup,false);

        return new MyViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        Journal journal=journalList.get(position);
        myViewHolder.title.setText(journal.getTitle());
        myViewHolder.thoughts.setText(journal.getThoughts());
        myViewHolder.name.setText(journal.getUsername());
        Picasso.get().load(journal.getImageurl()).placeholder(R.drawable.scenary).fit().into(myViewHolder.imageView);
        String timeago= (String) DateUtils.getRelativeTimeSpanString(journal.getTimestamp().getSeconds()*1000);
        myViewHolder.dateadded.setText(timeago);
        myViewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable drawable=myViewHolder.imageView.getDrawable();
                Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();
                String path=MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"My image",null);
                Uri uri=Uri.parse(path);

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.putExtra(Intent.EXTRA_SUBJECT,"My Image");

                context.startActivity(Intent.createChooser(intent,"Share image using "));
            }
        });
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("Are you sure you want to delete it?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        journalList.remove(myViewHolder.getAdapterPosition());

                        notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentuser=mAuth.getCurrentUser();
                        collectionReference.whereEqualTo("userId",currentuser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                notifyDataSetChanged();

                            }
                        });

                    }
                });
                dialog.create();
                dialog.show();

            }
        });


    }



    @Override
    public int getItemCount() {
        return journalList.size();
    }

  public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateadded,name;
        public ImageView imageView;
        public ImageButton share,delete;
        public MyViewHolder(@NonNull final View itemView, Context ctx) {
            super(itemView);
            context=ctx;
            title=itemView.findViewById(R.id.list_title);
            thoughts=itemView.findViewById(R.id.list_thought);
            dateadded=itemView.findViewById(R.id.list_time);
            imageView=itemView.findViewById(R.id.list_imageview);
            name=itemView.findViewById(R.id.name);
            share=itemView.findViewById(R.id.share);
            delete=itemView.findViewById(R.id.delete);
        }


  }


}
