package com.prayertimes.app.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.prayertimes.app.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View name = findViewById(R.id.tv_splash_name);
        View sub  = findViewById(R.id.tv_splash_sub);
        View line = findViewById(R.id.view_line);

        // أنيميشن ظهور تدريجي للاسم
        AnimationSet nameAnim = new AnimationSet(true);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(900);
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 30, 0);
        slideUp.setDuration(900);
        nameAnim.addAnimation(fadeIn);
        nameAnim.addAnimation(slideUp);
        nameAnim.setFillAfter(true);
        name.startAnimation(nameAnim);

        AlphaAnimation subFade = new AlphaAnimation(0f, 1f);
        subFade.setDuration(700);
        subFade.setStartOffset(500);
        subFade.setFillAfter(true);
        sub.startAnimation(subFade);

        // الخط الذهبي يتوسع
        line.post(() -> {
            Animation expand = new Animation() {
                @Override
                protected void applyTransformation(float t, android.view.animation.Transformation tr) {
                    android.view.ViewGroup.LayoutParams lp = line.getLayoutParams();
                    lp.width = (int)(80 * t);
                    line.setLayoutParams(lp);
                    line.setAlpha(t);
                }
            };
            expand.setDuration(600);
            expand.setStartOffset(400);
            expand.setFillAfter(true);
            line.startAnimation(expand);
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }
}
