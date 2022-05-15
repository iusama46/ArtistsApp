package com.example.freelanceapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    EditText bio, name, area, exp, rate;

    boolean isArtist;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");

        isArtist = Utils.getUserIsArtist(this);

        Log.d("clima", String.valueOf(isArtist));
        bio = findViewById(R.id.bio);
        name = findViewById(R.id.name);
        area = findViewById(R.id.area);
        exp = findViewById(R.id.exp);
        rate = findViewById(R.id.rate);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (!isArtist) {
            bio.setVisibility(View.GONE);
            exp.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
        }

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        getData();
    }

    private void updateData() {


        Map<String, Object> docData = new HashMap<>();
        docData.put("name", name.getText().toString());
        docData.put("area", area.getText().toString());

        if (isArtist) {
            docData.put("experience", exp.getText().toString());
            docData.put("bio", bio.getText().toString());
            docData.put("hourly_rate", rate.getText().toString());
        }


        db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).update(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        db.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    name.setText(document.getString("name"));
                    area.setText(document.getString("area"));

                    if (isArtist) {
                        bio.setText(document.getString("bio"));
                        exp.setText(document.getString("experience"));
                        rate.setText(document.getString("hourly_rate"));

                        bio.setSelection(bio.getText().toString().length()-1);
                    }
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