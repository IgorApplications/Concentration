package com.iapp.concentration.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import com.iapp.concentration.R;
import com.iapp.concentration.util.KeySettings;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String[] MONTH = new String[] {"ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"};
    private static final String[] DAY_WEEK = new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

    private SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(KeySettings.FILE_NAME, MODE_PRIVATE);

        LinearLayout header = findViewById(R.id.header);
        header.setAlpha(0f);
        header.animate().alpha(1f).setDuration(500);

        AppCompatButton filterToday = findViewById(R.id.filterToday);
        AppCompatButton filterTomorrow = findViewById(R.id.filterTomorrow);
        AppCompatButton filterMonth = findViewById(R.id.filterMonth);

        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton listButton = findViewById(R.id.list);
        ImageButton profileButton = findViewById(R.id.profile);
        ImageButton concentrationButton = findViewById(R.id.concentrationButton);
        ImageButton addButton = findViewById(R.id.addButton);
        LinearLayout profileTop = findViewById(R.id.topProfile);
        updateDate();

        Consumer<View> filterAnim = view -> {
            view.setPressed(true);
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            view.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction(() ->
                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(120)
                    );
        };

        View.OnTouchListener pressEffect = (v, event) -> {

            if(event.getAction()== MotionEvent.ACTION_DOWN) {
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

        Consumer<View> bottomButtonsAnimation = view -> {
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
                                    .setDuration(100));
        };

        BiConsumer<View, Runnable> circleButtonAnim = (view, task) -> {

            view.setPressed(true);
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            view.animate().cancel();

            view.animate()
                    .scaleX(0.85f)
                    .scaleY(0.85f)
                    .setDuration(90)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() ->
                            view.animate()
                                    .scaleX(1.05f)
                                    .scaleY(1.05f)
                                    .setDuration(120)
                                    .withEndAction(() ->
                                            view.animate()
                                                    .scaleX(1f)
                                                    .scaleY(1f)
                                                    .setDuration(120)
                                                    .withEndAction(task)
                                    )
                    );
        };

        filterToday.setOnTouchListener(pressEffect);
        filterTomorrow.setOnTouchListener(pressEffect);
        filterMonth.setOnTouchListener(pressEffect);
        concentrationButton.setOnTouchListener(pressEffect);
        addButton.setOnTouchListener(pressEffect);
        calendarButton.setOnTouchListener(pressEffect);
        homeButton.setOnTouchListener(pressEffect);
        listButton.setOnTouchListener(pressEffect);
        profileButton.setOnTouchListener(pressEffect);
        profileTop.setOnTouchListener(pressEffect);

        filterToday.setOnClickListener(v -> {

            filterAnim.accept(v);

            filterToday.setTextColor(Color.parseColor("#FFFFFF"));
            filterTomorrow.setTextColor(Color.parseColor("#000000"));
            filterMonth.setTextColor(Color.parseColor("#000000"));

            filterToday.setBackgroundResource(R.drawable.bg_filter_today);
            filterTomorrow.setBackgroundResource(R.drawable.bg_filter_outline);
            filterMonth.setBackgroundResource(R.drawable.bg_filter_outline);

        });
        filterTomorrow.setOnClickListener(v -> {
            filterAnim.accept(v);

            filterToday.setTextColor(Color.parseColor("#000000"));
            filterTomorrow.setTextColor(Color.parseColor("#FFFFFF"));
            filterMonth.setTextColor(Color.parseColor("#000000"));

            filterToday.setBackgroundResource(R.drawable.bg_filter_outline);
            filterTomorrow.setBackgroundResource(R.drawable.bg_filter_today);
            filterMonth.setBackgroundResource(R.drawable.bg_filter_outline);
        });
        filterMonth.setOnClickListener(v -> {
            filterAnim.accept(v);

            filterToday.setTextColor(Color.parseColor("#000000"));
            filterTomorrow.setTextColor(Color.parseColor("#000000"));
            filterMonth.setTextColor(Color.parseColor("#FFFFFF"));

            filterToday.setBackgroundResource(R.drawable.bg_filter_outline);
            filterTomorrow.setBackgroundResource(R.drawable.bg_filter_outline);
            filterMonth.setBackgroundResource(R.drawable.bg_filter_today);
        });

        concentrationButton.setOnClickListener(v -> circleButtonAnim.accept(v, () -> goToPlant(v)));
        addButton.setOnClickListener(v -> circleButtonAnim.accept(v, () -> {}));
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
        calendarButton.setOnClickListener(bottomButtonsAnimation::accept);
        Context context = this;
        listButton.setOnClickListener(v -> {
            MotionLayout motion = findViewById(R.id.bottomMenu);
            motion.transitionToEnd();
            v.postDelayed(() -> startActivity(new Intent(context, ListActivity.class)), 350);
        });
        profileButton.setOnClickListener(bottomButtonsAnimation::accept);

        showNameDialog();
    }

    public void goToPlant(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        Intent intent = new Intent(this, PlantActivity.class);

        intent.putExtra("cx", location[0]);
        intent.putExtra("cy", location[1]);

        startActivity(intent);
        overridePendingTransition(0,0);
    }
    private void updateDate() {
        TextView dayWeekView = findViewById(R.id.dayWeek);
        TextView dayMonthView = findViewById(R.id.dayMonth);
        TextView monthView = findViewById(R.id.month);

        Calendar calendar = new GregorianCalendar();
        dayWeekView.setText(DAY_WEEK[(calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7]);
        dayMonthView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        monthView.setText(MONTH[calendar.get(Calendar.MONTH)]);
    }

    @SuppressLint("SetTextI18n")
    private void showNameDialog() {
        TextView greetingsView = findViewById(R.id.greetings);

        String name = sharedPreferences.getString(KeySettings.APPLICATION_NAME, "");
        if (!name.equals("")) {
            greetingsView.setText("Приветствую, " + name + "!");
            return;
        }

        EditText editText = new EditText(this);
        editText.setText("Влад");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Укажите имя")
                .setMessage("Введите имя, котрое будет отображаться в приложении")
                .setView(editText)
                .setPositiveButton("Принять", (dialog, which) -> {
                    String newName = editText.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KeySettings.APPLICATION_NAME, editText.getText().toString());
                    editor.apply();
                    greetingsView.setText("Приветствую, " + newName + "!");
                })
                .create();

        alertDialog.show();
    }

}