package com.lozano.camilo.shop.start_content;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.lozano.camilo.shop.R;

public class Splash extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 1800;
    private ImageView imgSplash;
    AnimationDrawable myAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        imgSplash = (ImageView) findViewById(R.id.imgSplash);
        imgSplash.setBackgroundResource(R.drawable.splash);
        myAnimationDrawable = (AnimationDrawable) imgSplash.getBackground();
        AnimationSplash();
    }

    // Genera el bucle del tiempo que pasa la anumación
    private void AnimationSplash() {
        new CountDownTimer(SPLASH_SCREEN_DELAY, 400) {
            @Override
            public void onTick(long millisUntilFinished) {
                imgSplash.post(new Runnable() {
                    @Override
                    public void run() {
                        myAnimationDrawable.start();
                    }

                });

            }
            @Override
            public void onFinish() {
                Intent In = new Intent(Splash.this, Login.class);
                startActivity(In);
                finish();
            }
        }.start();
    }
}
