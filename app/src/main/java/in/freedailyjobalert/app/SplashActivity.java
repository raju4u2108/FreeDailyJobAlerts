package in.freedailyjobalert.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private static final int SPLASH_DELAY = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Fade-in animation for logo
        ImageView logo = findViewById(R.id.splash_logo);
        TextView tagline = findViewById(R.id.splash_tagline);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);
        logo.startAnimation(fadeIn);

        AlphaAnimation fadeInSlow = new AlphaAnimation(0f, 1f);
        fadeInSlow.setDuration(1200);
        fadeInSlow.setStartOffset(400);
        fadeInSlow.setFillAfter(true);
        tagline.startAnimation(fadeInSlow);

        // Launch MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            // Pass any notification data if app was opened from FCM
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
