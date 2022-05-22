package com.example.freelanceapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.consumer.adapters.CommentAdapter;
import com.example.freelanceapp.consumer.models.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    CollectionReference commentCollection;
    LinearLayout layout1;
    LinearLayout layout2;
    EditText comment;
    TextView noReviewText;

    FirebaseFirestore db;
    String userName = "Hidden";
    CommentAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Comment> comments;
    String artistId;
    private String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Comments");

        artistId = getIntent().getStringExtra("artist_id");
        imageId = getIntent().getStringExtra("id");

        Log.d("clima",artistId);
        Log.d("clima2",imageId);

        if (artistId == null) {
            Toast.makeText(this, "Invalid Id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        db = FirebaseFirestore.getInstance();
        comments = new ArrayList<>();

        layout1 = findViewById(R.id.lay1);
        layout2 = findViewById(R.id.lay2);
        comment = findViewById(R.id.comment);

        recyclerView = findViewById(R.id.rv);
        noReviewText = findViewById(R.id.no_text);

        if (Utils.IS_ARTIST) {
            layout1.setVisibility(View.GONE);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new CommentAdapter(comments);
        recyclerView.setAdapter(adapter);

        try {
            commentCollection = db.collection("users").document(artistId).collection("portfolio").document(imageId).collection("comments");
        } catch (Exception e) {
            Log.d("clima", e.getMessage());
        }

        loadData();
        loadComments();



        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentTxt = "None";
                if (!comment.getText().toString().isEmpty()) {
                    commentTxt = comment.getText().toString();
                }

                //Save to FIRESTORE
                Map<String, String> data = new HashMap<>();
                data.put("name", userName);
                data.put("comment", commentTxt);

                commentCollection
                        .add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CommentsActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();

                                    layout1.setVisibility(View.GONE);
                                    loadComments();
                                } else {
                                    Toast.makeText(CommentsActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }


    private void loadData() {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("name");
                        Log.d("clima", userName);
                    }

                }
            });

        } catch (Exception e) {
            //utils.toast(this, e.getMessage());
        }
    }

    private void loadComments() {
        comments.clear();
        try {
            commentCollection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    //userName = documentSnapshot.getString("firstname").concat(" ").concat(documentSnapshot.getString("lastName"));
                                    Comment comment = new Comment();
                                    comment.setComment(document.getString("comment"));
                                    comment.setUserName(document.getString("name"));
                                    comments.add(comment);


                                }

                                adapter = new CommentAdapter(comments);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if (comments.isEmpty()) {
                                    layout2.setVisibility(View.GONE);
                                    noReviewText.setVisibility(View.VISIBLE);
                                } else {
                                    noReviewText.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                //utils.toast(ReviewsActivity.this, task.getException().getMessage());
                            }
                        }
                    });


        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

