package com.iapp.concentration;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PlantActivity extends AppCompatActivity {

    private static final int COUNT_RESOURCES = 4;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int[] resources = new int[] {R.drawable.anim_size_0, R.drawable.anim_size_1, R.drawable.anim_size_2, R.drawable.anim_size_3};
    private int currentIndex = 0;

    private long generalTime;
    private long startTime;

    private long growthStep;
    private long pauseTime;
    private long currentGrowthStep;

    private TextView nameMode;
    private TextView timerView;
    private ImageView animationObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        // very dangerous!!!
        String name = (String) getIntent().getExtras().get("title");
        // very dangerous!!!
        generalTime = (long) getIntent().getExtras().get("time");
        growthStep = generalTime / (COUNT_RESOURCES + 1);

        nameMode = findViewById(R.id.plant_ac_name);
        nameMode.setText(name);
        timerView = findViewById(R.id.plant_ac_timer);
        timerView.setText(String.valueOf(generalTime));

        animationObj = findViewById(R.id.imageView);
        AnimationDrawable animation = (AnimationDrawable) animationObj.getDrawable();
        animation.start();

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long dif = SystemClock.elapsedRealtime() - pauseTime;
        currentGrowthStep = SystemClock.elapsedRealtime();

        if (dif >= 2000) {
            currentIndex = Math.max(0, currentIndex - 2);
            setAnimation(resources[currentIndex]);
        } else {
            currentIndex = Math.max(0, currentIndex - 1);
            setAnimation(resources[currentIndex]);
        }
    }

    private void startTimer() {
        Runnable updateRunnable = new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                long elapsed = SystemClock.elapsedRealtime() - startTime;
                long left = generalTime - elapsed;
                if (left <= 0) {
                    timerView.setText("00:00");
                    handler.removeCallbacks(this);
                    finishTimer();
                    return;
                }

                if ((SystemClock.elapsedRealtime() - currentGrowthStep) >= growthStep) {
                    if (currentIndex != resources.length - 1) {
                        currentIndex++;
                    }

                    setAnimation(resources[currentIndex]);
                    currentGrowthStep = SystemClock.elapsedRealtime();
                }

                int seconds = (int) (left / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerView.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        };
        startTime = SystemClock.elapsedRealtime();
        currentGrowthStep = startTime;
        System.out.println("GEN = " + generalTime + ", CUR = " + startTime);
        handler.post(updateRunnable);
    }

    public void finishTimer() {
        showMessageDialog("Сообщение", "Время вышло!");
    }

    private void setAnimation(int animRes) {

        Drawable current = animationObj.getDrawable();
        if (current instanceof AnimationDrawable) {
            ((AnimationDrawable) current).stop();
        }

        animationObj.setImageResource(animRes);

        animationObj.post(() -> {
            AnimationDrawable anim =
                    (AnimationDrawable) animationObj.getDrawable();
            anim.start();
        });
    }

    private void showMessageDialog(String tittle, String message) {
        AlertDialog confirmDialog =
                new AlertDialog.Builder(this)
                        .setTitle(tittle)
                        .setMessage(message)
                        .setPositiveButton("Ясно", (dialog, which) -> {})
                        .create();
        confirmDialog.show();
    }
}