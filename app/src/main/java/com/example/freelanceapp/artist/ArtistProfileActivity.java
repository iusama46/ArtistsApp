package com.example.freelanceapp.artist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.artist.adapters.WorkAdapter;
import com.example.freelanceapp.artist.models.Work;
import com.example.freelanceapp.chat.ChatActivity;
import com.example.freelanceapp.consumer.ReviewsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArtistProfileActivity extends AppCompatActivity {

    TextView bio, name, area, exp, accountNo, categoriesTv, accountNo2;
    RatingBar ratingBar;
    String artistId;
    boolean isArtist;
    RecyclerView recyclerView;
    ImageView profileImg;
    ImageButton payBtn;
    ArrayList<Work> workImages = new ArrayList<>();
    WorkAdapter adapter;
    String artistName = "";
    String epAccountNo = "#", jcAccountNo = "#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");

        bio = findViewById(R.id.bio);
        name = findViewById(R.id.name);
        //email = findViewById(R.id.email);
        area = findViewById(R.id.loc);
        ratingBar = findViewById(R.id.simpleRatingBar);
        exp = findViewById(R.id.exp);
        profileImg = findViewById(R.id.image_pp);

        recyclerView = findViewById(R.id.sample);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        categoriesTv = findViewById(R.id.cat);
        payBtn = findViewById(R.id.pay_img);
        accountNo = findViewById(R.id.account);
        accountNo2 = findViewById(R.id.account2);

        artistId = getIntent().getStringExtra("uId");
        isArtist = getIntent().getBooleanExtra("isArtist", true);

        findViewById(R.id.switch_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (epAccountNo.equals("#") || epAccountNo.isEmpty())
                    accountNo.setText("Unavailable");
                else
                    accountNo.setText(epAccountNo);
            }
        });

        findViewById(R.id.switch_acc2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jcAccountNo.equals("#") || jcAccountNo.isEmpty())
                    accountNo2.setText("Unavailable");
                else
                    accountNo2.setText(jcAccountNo);
            }
        });

        getData();

        Button servicesBtn = findViewById(R.id.book_service);

        if(!isArtist) {
            servicesBtn.setVisibility(View.GONE);
            LinearLayout linearLayout = findViewById(R.id.artLay);
            linearLayout.setVisibility(View.GONE);
        }
        servicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBooking();
            }
        });
        Button msgBtn = findViewById(R.id.message);
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistProfileActivity.this, ChatActivity.class);
                intent.putExtra("name", artistName);
                intent.putExtra("u_id", artistId);
                startActivity(intent);
            }
        });

        findViewById(R.id.reviews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReviewsActivity.class).putExtra("uId", artistId));
            }
        });
    }

    private void saveBooking() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                String currentUserName = Utils.getUserName(ArtistProfileActivity.this);

                Map<String, Object> docData = new HashMap<>();
                docData.put("artist", artistId);
                docData.put("booked_by_id", auth.getUid());
                docData.put("booked_by_name", currentUserName);
                docData.put("date", dtf.format(now));

                firestore.collection("bookings").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            displayDialog();
                        } else {
                            Toast.makeText(ArtistProfileActivity.this, "Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void displayDialog() {
        new AlertDialog.Builder(ArtistProfileActivity.this)
                .setTitle("Success")
                .setMessage("Artist Service is booked successfully")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                //.setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(artistId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    name.setText(document.getString("name") + " | " + document.getString("email"));
                    area.setText(document.getString("area"));

                    artistName = document.getString("name");

                    if (isArtist) {
                        bio.setText(document.getString("bio"));
                        String experience = "Experience: " + document.getString("experience");
                        String hourlyRate = "years & \tHourly Rate: " + document.getString("hourly_rate") + "$/hour";
                        exp.setText(experience + hourlyRate);
                        Float ratings = Float.parseFloat(document.getString("ratings"));
                        ratingBar.setRating(ratings);
                        if (document.contains("account_no")) {
                            //accountNo.setText(document.getString("account_no"));
                            epAccountNo = document.getString("account_no");
                        }

                        if (document.contains("account_no2")) {
                            //accountNo.setText(document.getString("account_no"));
                            jcAccountNo = document.getString("account_no2");
                        }

                        if (document.contains("categories")) {
                            List<String> categories = (List<String>) document.get("categories");
                            String strNew = categories.toString().replace("[", "");
                            categoriesTv.setText(strNew.replace("]", ""));
                        }
                    }


                    if (document.contains("img_url")) {
                        Glide
                                .with(ArtistProfileActivity.this)
                                .load(document.getString("img_url"))
                                .centerCrop()
                                .placeholder(R.drawable.loading)
                                .into(profileImg);
                    }
                }
            }
        });

        try {
            db.collection("users").document(artistId).collection("portfolio").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d("clima2", doc.getString("img_url"));

                            Work work = new Work();
                            work.setId(doc.getId());
                            work.setImageUrl(doc.getString("img_url"));
                            work.setArtistId(artistId);
                            workImages.add(work);
                        }
                        adapter = new WorkAdapter(workImages);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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