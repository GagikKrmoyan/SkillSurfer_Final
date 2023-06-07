package com.example.diliproj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    Button changePasswordButton;
    Button helpButton;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        changePasswordButton = findViewById(R.id.change_password);
        helpButton = findViewById(R.id.help);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        changePasswordButton.setOnClickListener(view -> {
            sendPasswordResetEmail();
        });

        helpButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Opening mail app...", Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:skillsurfer08@gmail.com"));
                startActivity(intent);
            }, 3000);
        });

        Button aboutButton = findViewById(R.id.about);
        aboutButton.setOnClickListener(view -> {
            showAboutDialog();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_user);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_user:
                    return true;
                case R.id.navigation_home:
                    Intent intent_home = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent_home);
                    finish();
                    return true;
            }
            return false;
        });
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Us")
                .setMessage("This app was created by Gagik Krmoyan and his team. The main goal of the app is to make the educational process easier for people. For any inquiries, please contact us at krmoyan@mail.com")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPasswordResetEmail() {
        String email = user.getEmail();

        if (email != null) {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Password change email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to send password change email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
