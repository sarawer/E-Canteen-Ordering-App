package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import androidx.core.content.ContextCompat;
import android.widget.LinearLayout;

public class WelcomeActivity extends AppCompatActivity {

    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check session
        SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
        String role = sp.getString("session_role", null);
        if (role != null) {
            // Already logged in
            if ("admin".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, com.example.e_canteenorderingapp.admin.AdminHomeActivity.class));
            } else {
                startActivity(new Intent(this, com.example.e_canteenorderingapp.student.StudentHomeActivity.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        LinearLayout btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerView pv = findViewById(R.id.playerView);
        if (pv != null) {
            player = new ExoPlayer.Builder(this).build();
            pv.setPlayer(player);
            // Ensure pre-first-frame shows white instead of black
            pv.setShutterBackgroundColor(ContextCompat.getColor(this, R.color.white));
            pv.setUseController(false); // Hide controls – act like an animated logo
            MediaItem item = MediaItem.fromUri("asset:///intro.mp4"); // or rawresource
            player.setMediaItem(item);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            player.setVolume(0f);
            player.prepare();
            // Autoplay once, then keep last frame
            player.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) { player.release(); player = null; }
    }
}
