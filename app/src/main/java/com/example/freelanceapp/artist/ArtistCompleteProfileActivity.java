package com.example.freelanceapp.artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freelanceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArtistCompleteProfileActivity extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        EditText exp = findViewById(R.id.exp);
        EditText bio = findViewById(R.id.bio);
        EditText hourlyRate = findViewById(R.id.rate);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!exp.getText().toString().isEmpty()) {
                    if (!bio.getText().toString().isEmpty()) {
                        if (!hourlyRate.getText().toString().isEmpty()) {
                            updateData(exp.getText().toString(), bio.getText().toString(), hourlyRate.getText().toString());
                        } else {
                            hourlyRate.setError("Required");
                        }
                    } else {
                        bio.setError("Required");
                    }
                } else {
                    exp.setError("Required");
                }
            }
        });
    }

    private void updateData(String exp, String bio, String rate) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("experience", exp);
        docData.put("bio", bio);
        docData.put("hourly_rate", rate);
        firestore.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).update(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(ArtistCompleteProfileActivity.this, ArtistMainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Toast.makeText(ArtistCompleteProfileActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}