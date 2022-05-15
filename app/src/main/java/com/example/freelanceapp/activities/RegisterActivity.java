package com.example.freelanceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.freelanceapp.consumer.MainActivity;
import com.example.freelanceapp.R;
import com.example.freelanceapp.Utils;
import com.example.freelanceapp.artist.ArtistCompleteProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText name = findViewById(R.id.name);
        EditText area = findViewById(R.id.area);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        radioGroup=findViewById(R.id.radioGroup);

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().isEmpty()) {
                    if (!password.getText().toString().isEmpty()) {

                        if (radioGroup.getCheckedRadioButtonId()!=-1) {
                            if (!name.getText().toString().isEmpty()) {
                                if (!area.getText().toString().isEmpty()) {
                                    Signup(email.getText().toString(), password.getText().toString(),name.getText().toString(),area.getText().toString());
                                } else {
                                    area.setError("Required");
                                }
                            } else {
                                name.setError("Required");
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Select tour role", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        password.setError("Required");
                    }
                } else {
                    email.setError("Required");
                }
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Signup(String email, String password, String name, String area) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            //SAVING DATA TO DB

                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton) findViewById(selectedId);
                            int isArtist = Utils.ARTIST;
                            if(radioButton.getText().toString().equals("  Looking for an artist")){
                                 isArtist = Utils.CONSUMER;
                            }

                            Map<String, Object> docData = new HashMap<>();
                            docData.put("name", name);
                            docData.put("area", area);
                            docData.put("type", isArtist);
                            assert user != null;
                            docData.put("email", user.getEmail());
                            docData.put("ratings", "0");
                            //docData.put("regions", Arrays.asList("west_coast", "socal"));

                            int finalIsArtist = isArtist;
                            firestore.collection("users").document(user.getUid()).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Utils.saveUserRole(RegisterActivity.this, finalIsArtist, name);
                                        if(finalIsArtist ==Utils.ARTIST){
                                            startActivity(new Intent(RegisterActivity.this, ArtistCompleteProfileActivity.class)
                                                    );
                                        }else {
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        }
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(RegisterActivity.this, "Authentication failed. " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}