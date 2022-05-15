package com.example.freelanceapp.consumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.consumer.adapters.ReviewAdapter;
import com.example.freelanceapp.consumer.models.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {
    LinearLayout layout1;
    LinearLayout layout2;
    EditText comment;
    TextView noReviewText;
    RatingBar ratingBar;
    FirebaseFirestore db;
    String userName = "Hidden";
    ReviewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Review> reviews;
    String artistId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Reviews");

        artistId = getIntent().getStringExtra("uId");

        if(artistId ==null){
            Toast.makeText(this, "Invalid Id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        db = FirebaseFirestore.getInstance();
        reviews = new ArrayList<>();

        layout1 = findViewById(R.id.lay1);
        layout2 = findViewById(R.id.lay2);
        comment = findViewById(R.id.comment);
        ratingBar = findViewById(R.id.simpleRatingBar);
        recyclerView = findViewById(R.id.rv);
        noReviewText = findViewById(R.id.no_text);

        if (Utils.IS_ARTIST) {
            layout1.setVisibility(View.GONE);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new ReviewAdapter(reviews);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadData();
        loadRatings();

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ratings = String.valueOf(ratingBar.getRating());
                String commentTxt = "None";
                if (!comment.getText().toString().isEmpty()) {
                    commentTxt = comment.getText().toString();
                }

                //Save to FIRESTORE
                Map<String, String> data = new HashMap<>();
                data.put("name", userName);
                data.put("rating", ratings);
                data.put("comment", commentTxt);

                db.collection("users").document(artistId).collection("reviews").document()
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ReviewsActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();

                                layout1.setVisibility(View.GONE);
                                loadRatings();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ReviewsActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    private void updateAvgRating() {
        try {
            float temp =0;
            for (Review review:
                    reviews) {
                temp=temp+ Float.parseFloat(review.getRating());

                if(Float.isNaN(temp)){
                    temp=ratingBar.getRating();
                }
                Log.d("clima 1", String.valueOf(temp));
            }
            temp = temp/reviews.size();

            Map<String, Object> data = new HashMap<>();
            data.put("ratings", String.format("%.1f", temp));
            db.collection("users").document(artistId).update(data);

        } catch (Exception e) {
            //utils.toast(this, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    private void loadRatings() {
        reviews.clear();
        try {
            db.collection("users").document(artistId).collection("reviews")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                    //userName = documentSnapshot.getString("firstname").concat(" ").concat(documentSnapshot.getString("lastName"));
                                    Review review = new Review();
                                    review.setComment(document.getString("comment"));
                                    review.setUserName(document.getString("name"));
                                    review.setRating(document.getString("rating"));
                                    reviews.add(review);


                                }

                                adapter = new ReviewAdapter(reviews);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(reviews.isEmpty()){
                                    layout2.setVisibility(View.GONE);
                                    noReviewText.setVisibility(View.VISIBLE);
                                }else {
                                    noReviewText.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                }
                                updateAvgRating();
                            } else {
                                Toast.makeText(ReviewsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
