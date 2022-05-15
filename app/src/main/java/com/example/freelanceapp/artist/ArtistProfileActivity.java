package com.example.freelanceapp.artist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.activities.RegisterActivity;
import com.example.freelanceapp.chat.ChatActivity;
import com.example.freelanceapp.consumer.MainActivity;
import com.example.freelanceapp.consumer.ReviewsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArtistProfileActivity extends AppCompatActivity {

    TextView bio, name, area, exp;
    RatingBar ratingBar;
    String artistId;

    String artistName = "";

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

        artistId = getIntent().getStringExtra("uId");

        getData();

        findViewById(R.id.book_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBooking();
            }
        });

        findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
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

                String currentUserName= Utils.getUserName(ArtistProfileActivity.this);

                Map<String, Object> docData = new HashMap<>();
                docData.put("artist", artistId);
                docData.put("booked_by_id", auth.getUid());
                docData.put("booked_by_name", currentUserName);
                docData.put("date", dtf.format(now));

                firestore.collection("bookings").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                   if(task.isSuccessful()){
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
                    bio.setText(document.getString("bio"));
                    artistName = document.getString("name");
                    String experience = "Experience: " + document.getString("experience");
                    String hourlyRate = "years & \tHourly Rate: " + document.getString("hourly_rate") + "$/hour";
                    exp.setText(experience + hourlyRate);
                    Float ratings = Float.parseFloat(document.getString("ratings"));
                    ratingBar.setRating(ratings);


                }
            }
        });
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