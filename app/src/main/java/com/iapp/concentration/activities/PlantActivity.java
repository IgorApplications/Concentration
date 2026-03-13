package com.iapp.concentration.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.iapp.concentration.R;
import com.iapp.concentration.util.KeySettings;
import com.iapp.concentration.views.TimerCircleView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlantActivity extends AppCompatActivity {

    private static final int COUNT_RESOURCES = 4;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int[][] plantStates = {
            {
                    R.drawable.ic_size_0_index_0,
                    R.drawable.ic_size_0_index_1,
                    R.drawable.ic_size_0_index_2
            },

            {
                    R.drawable.ic_size_1_index_0,
                    R.drawable.ic_size_1_index_1,
                    R.drawable.ic_size_1_index_2
            },

            {
                    R.drawable.ic_size_2_index_0,
                    R.drawable.ic_size_2_index_1,
                    R.drawable.ic_size_2_index_2,
                    R.drawable.ic_size_2_index_3,
                    R.drawable.ic_size_2_index_4
            }
    };
    private final int[] greenStateIndex = {
            2, // size0 -> ic_size_0_index_2
            2, // size1 -> ic_size_1_index_2
            3  // size2 -> ic_size_2_index_3 (полностью выросшее)
    };

    private int maxPlantLevel = 0;
    private int plantLevel = 0;
    private int plantHealth = greenStateIndex[0];

    private boolean isTimerWork = false;
    private long pauseTimestamp = 0;
    private long generalTime;
    private long startTime;

    private SharedPreferences sharedPreferences;

    private TimerCircleView timerCircle;

    private TextView timerView;
    private ImageView animationObj;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);


        initScreenAnimation();
        // --------------------------------------------------------------------------------

        // TODO 30 * 1000 replace to 25 * 60 * 1000
        generalTime = 30 * 60 * 1000;

        timerView = findViewById(R.id.timerView);
        animationObj = findViewById(R.id.imageView);
        animationObj.setImageResource(
                plantStates[0][greenStateIndex[0]]
        );
        timerCircle = findViewById(R.id.timerCircle);
        timerCircle.animate().setInterpolator(new DecelerateInterpolator());

        updateTimerText(generalTime);
        // -------------------------------------------------------------------------------

        BiConsumer<View, Runnable> bottomButtonsAnimation = (view, task) -> {
            view.setPressed(true);
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            view.animate().cancel();
            view.animate()
                .setInterpolator(new DecelerateInterpolator())
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(100)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .withEndAction(task));
        };

        View.OnTouchListener pressEffect = (v, event) -> {

            if(event.getAction()==MotionEvent.ACTION_DOWN) {
                v.animate().cancel();
                v.animate().setInterpolator(new DecelerateInterpolator())
                        .scaleX(0.9f).scaleY(0.9f).setDuration(70);
            }

            if(event.getAction()==MotionEvent.ACTION_UP ||
                    event.getAction()==MotionEvent.ACTION_CANCEL){
                v.animate().cancel();
                v.animate().setInterpolator(new DecelerateInterpolator())
                        .scaleX(1f).scaleY(1f).setDuration(70);
            }

            return false;
        };

        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton startButton = findViewById(R.id.startButton);
        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton listButton = findViewById(R.id.list);
        ImageButton profileButton = findViewById(R.id.profile);
        ImageButton tuneButton = findViewById(R.id.tune);

        startButton.setOnClickListener(v -> {
            v.setPressed(true);
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            v.animate().cancel();
            v.animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(150)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .withEndAction(() -> {
                                        if (isTimerWork) {
                                            Toast.makeText(this, "Таймер уже запущен", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // IMPORTANT !!!!!!!!
                                        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
                                        animator.setDuration(generalTime);
                                        animator.addUpdateListener(a -> {
                                            float progress = (float)a.getAnimatedValue();
                                            timerCircle.setProgress(progress);
                                        });
                                        animator.start();

                                        // IMPORTANT !!!!!!!!
                                        isTimerWork = true;
                                        startTimer();
                                        startPlantBreathing();
                                    })
                    );

        });
        backButton.setOnClickListener(v -> {
            v.setPressed(true);
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            v.animate().cancel();
            v.animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .translationX(-20)
                    .alpha(0.6f)
                    .setDuration(120)
                    .withEndAction(() ->
                            v.animate()
                                    .translationX(0)
                                    .alpha(1f)
                                    .setDuration(120)
                                    .withEndAction(() ->
                                            showExitDialog("Выход",
                                                    "Вы уверены, что хотите выйти из режима 'Концентрация'?",
                                            (dialog, which) -> {
                                                // IMPORTANT!!!
                                                closeScreen();
                                            }
                                            ))
                    );
        });

        calendarButton.setOnClickListener(v -> bottomButtonsAnimation.accept(v, () -> {}));
        homeButton.setOnClickListener(v -> {
            v.setPressed(true);
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            v.animate().cancel();
            v.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(80)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1.15f)
                                    .scaleY(1.15f)
                                    .setDuration(120)
                                    .withEndAction(() ->
                                            v.animate()
                                                    .scaleX(1f)
                                                    .scaleY(1f)
                                                    .setDuration(120)
                                    )
                    );

        });

        Context context = this;
        listButton.setOnClickListener(v -> {
            bottomButtonsAnimation.accept(v, () -> showExitDialog("Выход",
                    "Вы уверены, что хотите выйти из режима 'Концентрация'?",
                    (dialog, which) -> {
                        // IMPORTANT!!!
                        closeScreen(() -> {
                            resetTimerState();

                            Intent intent = new Intent(context, ListActivity.class);
                            startActivity(intent);
                            myFinish = false;


                            overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                            );
                        });
                    }
            ));
        });
        profileButton.setOnClickListener(v -> bottomButtonsAnimation.accept(v, () -> {}));


        tuneButton.setOnClickListener(v -> bottomButtonsAnimation.accept(v, () -> {

            if (isTimerWork) {
                Toast.makeText(this, "Таймер уже запущен", Toast.LENGTH_SHORT).show();
                return;
            }

            int currentMinutes = (int)(generalTime / 60000);

            com.google.android.material.timepicker.MaterialTimePicker picker =
                    new com.google.android.material.timepicker.MaterialTimePicker.Builder()
                            .setTitleText("Выберите время концентрации")
                            .setTheme(R.style.CustomTimePicker)
                            .setHour(currentMinutes)
                            .setMinute(0)
                            .setTimeFormat(
                                    com.google.android.material.timepicker.TimeFormat.CLOCK_24H
                            )
                            .build();

            picker.addOnPositiveButtonClickListener(dialog -> {

                int minutes = picker.getHour();
                int seconds = picker.getMinute();

                if (minutes == 0 && seconds == 0) {
                    Toast.makeText(this, "Должно быть >= 1 секунды", Toast.LENGTH_SHORT).show();
                }

                generalTime = (minutes * 60L + seconds) * 1000L;

                updateTimerText(generalTime);

            });

            picker.show(getSupportFragmentManager(), "timer_picker");

        }));

        LinearLayout profileTop = findViewById(R.id.topProfile);
        profileTop.setOnClickListener(v -> {
            v.setPressed(true);
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            v.animate().cancel();
            v.animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(120)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(120)
                    );
        });

        backButton.setOnTouchListener(pressEffect);
        startButton.setOnTouchListener(pressEffect);
        profileTop.setOnTouchListener(pressEffect);
        calendarButton.setOnTouchListener(pressEffect);
        homeButton.setOnTouchListener(pressEffect);
        listButton.setOnTouchListener(pressEffect);
        profileButton.setOnTouchListener(pressEffect);
        profileTop.setOnTouchListener(pressEffect);
        tuneButton.setOnTouchListener(pressEffect);

        sharedPreferences = getSharedPreferences(KeySettings.FILE_NAME, MODE_PRIVATE);
        TextView greetingsView = findViewById(R.id.greetings);
        String name = sharedPreferences.getString(KeySettings.APPLICATION_NAME, "");
        if (!name.equals("")) {
            greetingsView.setText("Приветствую, " + name + "!");
            return;
        }
    }

    private void resetTimerState() {

        handler.removeCallbacksAndMessages(null);

        isTimerWork = false;
        pauseTimestamp = 0;

        plantLevel = 0;
        maxPlantLevel = 0;
        plantHealth = greenStateIndex[0];

        animationObj.setImageResource(
                plantStates[0][greenStateIndex[0]]
        );

        timerCircle.setProgress(0f);

        updateTimerText(generalTime);
    }


    private boolean myFinish = false;

    @Override
    public void finish() {
        if (myFinish) {
            super.finish();
            return;
        }

        showExitDialog("Выход",
                "Вы уверены, что хотите выйти из режима 'Концентрация'?",
                (dialog, which) -> {
                    // IMPORTANT!!!
                    closeScreen();
                }
        );
    }

     private void initScreenAnimation() {
         View root = findViewById(R.id.rootLayout);

         // dangerous access!!!
         int cx = getIntent().getIntExtra("cx",0);
         int cy = getIntent().getIntExtra("cy",0);

         root.post(() -> {

             float finalRadius = (float) Math.hypot(root.getWidth(), root.getHeight());

             Animator anim = ViewAnimationUtils.createCircularReveal(
                     root,
                     cx,
                     cy,
                     0,
                     finalRadius
             );

             anim.setDuration(500);
             anim.start();

         });
     }

    private void closeScreen() {

        View root = findViewById(R.id.rootLayout);

        int cx = root.getWidth()/2;
        int cy = root.getHeight()/2;

        float initialRadius = (float) Math.hypot(root.getWidth(), root.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(
                root,
                cx,
                cy,
                initialRadius,
                0
        );

        anim.setDuration(400);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                myFinish = true;
                finish();
            }
        });

        anim.start();
    }

    private void closeScreen(Runnable task) {

        View root = findViewById(R.id.rootLayout);

        int cx = root.getWidth()/2;
        int cy = root.getHeight()/2;

        float initialRadius = (float) Math.hypot(root.getWidth(), root.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(
                root,
                cx,
                cy,
                initialRadius,
                0
        );

        anim.setDuration(400);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                myFinish = true;
                task.run();
            }
        });

        anim.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        pauseTimestamp = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isTimerWork) return;
        if(pauseTimestamp == 0) return;

        long delta = SystemClock.elapsedRealtime() - pauseTimestamp;

        // TODO
        int degradeSteps = (int)(delta / 5000); // каждые 5 секунд

        for (int i = 0; i < degradeSteps; i++) {
            degradePlant();
        }

        pauseTimestamp = 0;

        startRecovering();
    }

    private void startRecovering(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                recoverPlant();

                if(plantHealth < greenStateIndex[plantLevel]){
                    // TODO
                    handler.postDelayed(this, 60_000);
                }

            }
        },3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
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
                    timerCircle.setProgress(0);
                    finishTimer();
                    return;
                }

                float progress = (float) elapsed / generalTime;
                updatePlantGrowth(progress);

                updateTimerText(left);

                handler.postDelayed(this, 950);
            }
        };
        startTime = SystemClock.elapsedRealtime();

        handler.post(updateRunnable);
    }

    private boolean isPlantHealthy(){
        return plantHealth == greenStateIndex[plantLevel];
    }

    private void recoverPlant(){

        int green = greenStateIndex[plantLevel];

        if(plantHealth < green){

            plantHealth++;
            renderPlant();
            return;
        }

        // после полного восстановления снова может расти
    }

    private void degradePlant(){
        int minHealth = 0;

        if(plantHealth > minHealth){

            plantHealth--;
            animatePlantDecay();
            renderPlant();
        }
    }

    private void updatePlantGrowth(float progress){

        //  если растение не зелёное — рост запрещён
        if(!isPlantHealthy()){
            return;
        }

        int newLevel;

        if(progress < 0.4f){
            newLevel = 0;
        }
        else if(progress < 0.8f){
            newLevel = 1;
        }
        else{
            newLevel = 2;
        }

        if(newLevel > maxPlantLevel){
            maxPlantLevel = newLevel;
        }

        if(newLevel != plantLevel){

            plantLevel = newLevel;
            plantHealth = greenStateIndex[plantLevel];

            renderPlant();
            animatePlantGrow();
            animationObj.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        }

        if(progress > 0.95f && plantLevel == 2){
            plantHealth = 4;
            renderPlant();
        }
    }

    private void renderPlant(){

        int drawable = plantStates[plantLevel][plantHealth];
        animationObj.setImageResource(drawable);

    }

    private void startPlantBreathing(){
        animationObj.animate()
                .scaleX(1.04f)
                .scaleY(1.04f)
                .rotation(-1f)
                .setDuration(1800)
                .withEndAction(() ->
                        animationObj.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .rotation(1f)
                                .setDuration(1800)
                                .withEndAction(this::startPlantBreathing)
                );
    }

    private void animatePlantGrow(){
        animationObj.animate()
                .scaleX(1.25f)
                .scaleY(1.25f)
                .setDuration(250)
                .withEndAction(() ->

                        animationObj.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(250)
                );
    }

    private void animatePlantDecay(){

        animationObj.animate()
                .rotation(3f)
                .alpha(0.7f)
                .setDuration(400)
                .withEndAction(() ->

                        animationObj.animate()
                                .rotation(0f)
                                .alpha(1f)
                                .setDuration(400)
                );
    }

    @SuppressLint("DefaultLocale")
    private void updateTimerText(long left) {
        int seconds = (int) (left / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        timerView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void finishTimer() {

        animationObj.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(500)
                .withEndAction(() ->

                        animationObj.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .withEndAction(() -> {

                                    updateTimerText(generalTime);
                                    resetTimerState();

                                    showMessageDialog("Сообщение", "Время вышло!");

                                })
                );
    }

    private void showMessageDialog(String title, String message) {

        if (isFinishing() || isDestroyed()) return;

        AlertDialog confirmDialog =
                new AlertDialog.Builder(this, R.style.CustomDialog)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ясно", (dialog, which) -> {})
                        .create();
        confirmDialog.show();
    }

    private void showExitDialog(String title, String question, DialogInterface.OnClickListener onAccept) {

        AlertDialog confirmDialog =
                new AlertDialog.Builder(this, R.style.CustomDialog)
                        .setTitle(title)
                        .setMessage(question)
                        .setNegativeButton("Нет", (dialog, which) -> {})
                        .setPositiveButton("Да", onAccept)
                        .create();

        confirmDialog.show();
    }
}