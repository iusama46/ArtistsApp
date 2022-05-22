package com.example.freelanceapp.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ImageActivity extends AppCompatActivity {

    ImageView likeBtn, commentBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String artistId = "";
    String imageId = "";
    TextView likeCount, commentCount;
    boolean isLiked = false;
    int count = 0;
    CollectionReference likeCollection;
    CollectionReference commentsCollection;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Image Viewer");
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);

        likeBtn = findViewById(R.id.like);
        commentBtn = findViewById(R.id.comment);
        commentCount = findViewById(R.id.comment_count);
        likeCount = findViewById(R.id.like_count);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String url = getIntent().getStringExtra("img_url");

        artistId = getIntent().getStringExtra("artist_id");
        imageId = getIntent().getStringExtra("id");



        Glide
                .with(this)
                .load(Uri.parse(url))
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(photoView);
        fetchData();

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLiked = !isLiked;

                if (isLiked) {

                    likeBtn.setColorFilter(getColor(R.color.purple_700), PorterDuff.Mode.SRC_ATOP);
                    likeCount.setTextColor(getColor(R.color.purple_700));
                    //likeBtn.setBackgroundColor(getColor(R.color.purple_700));
                    setText(count = count + 1);
                    updateLike();
                } else {
                    likeBtn.setBackgroundColor(getColor(R.color.grey_medium));
                    setText(count = count - 1);
                    likeBtn.setColorFilter(null);
                    likeCount.setTextColor(getColor(R.color.white));
                }
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageActivity.this,CommentsActivity.class);
                intent.putExtra("artist_id",artistId);
                intent.putExtra("id",imageId);
                startActivity(intent);
            }
        });


        boolean isArtist = Utils.getUserIsArtist(this);
        if(isArtist){
            likeBtn.setEnabled(false);
        }
    }

    void setText(int count) {
        if (count > 0) {

            likeCount.setText(String.valueOf(count));
        } else {
            likeCount.setText("");
        }
    }

    private void fetchData() {
        try {
            likeCollection = db.collection("users").document(artistId).collection("portfolio").document(imageId).collection("likes");
            likeCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        count = queryDocumentSnapshots.size();
                        setText(count);
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.contains("uId")) {
                                String id = doc.getString("uId");

                                if (id.equals(auth.getUid())) {
                                    likeBtn.setColorFilter(getColor(R.color.purple_700), PorterDuff.Mode.SRC_ATOP);
                                    likeCount.setTextColor(getColor(R.color.purple_700));
                                    isLiked = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d("clima fetch", "error" + e.getMessage().toString());
        }

        try {
            commentsCollection = db.collection("users").document(artistId).collection("portfolio").document(imageId).collection("comments");
            commentsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        commentCount.setText(queryDocumentSnapshots.size() == 0 ? "" : String.valueOf(queryDocumentSnapshots.size()));
                        /*for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            if(doc.contains("uId")){
                                String id =doc.getString("uId");

                                if(id.equals(auth.getUid())){
                                    likeBtn.setColorFilter(getColor(R.color.purple_700), PorterDuff.Mode.SRC_ATOP);
                                    likeCount.setTextColor(getColor(R.color.purple_700));
                                    isLiked=true;
                                    break;
                                }
                            }
                        }*/
                    }
                }
            });
        } catch (Exception e) {
            Log.d("clima fetch", "error" + e.getMessage().toString());
        }
    }

    private void updateLike() {
        try {
            Map<String, Object> docData = new HashMap<>();
            docData.put("uId", auth.getUid());
            likeCollection.add(docData);
        } catch (Exception e) {
            Log.d("clima update", "error " + e.getMessage().toString());
        }
    }


}