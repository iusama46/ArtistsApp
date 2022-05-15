package com.example.freelanceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.artist.ArtistMainActivity;
import com.example.freelanceapp.consumer.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        long currentMillis = System.currentTimeMillis();
        long nextDate = 1653069138000L;


        if (nextDate < currentMillis) {
            Toast.makeText(this, "Season Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        if (auth.getCurrentUser() != null) {
            try {
                Utils.IS_ARTIST = Utils.getUserIsArtist(SplashActivity.this);
                Intent intent;
                if (Utils.IS_ARTIST) {
                    intent = new Intent(getApplicationContext(), ArtistMainActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}