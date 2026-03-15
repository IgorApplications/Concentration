package com.iapp.concentration.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iapp.concentration.R;
import com.iapp.concentration.util.DataController;
import com.iapp.concentration.util.Task;
import com.iapp.concentration.views.TaskAdapter;

import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TomorrowActivity extends AppCompatActivity {

    private final android.os.Handler handler = new android.os.Handler();
    private static final String[] MONTH = new String[] {"ЯНВАРЯ", "ФЕВРАЛЯ", "МАРТА", "АПРЕЛЯ", "МАЯ", "ИЮНЯ", "ИЮЛЯ", "АВГУСТА", "СЕНТЯБРЯ", "ОКТЯБРЯ", "НОЯБРЯ", "ДЕКАБРЯ"};
    private static final String[] DAY_WEEK = new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> localTasks;

    @Override
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomorrow);

        LinearLayout header = findViewById(R.id.header);
        header.setAlpha(0f);
        header.animate().alpha(1f).setDuration(500);

        // -----------------------------------------------------------------------
        localTasks = DataController.getInstance(this).getCopyTasks();

        RecyclerView recyclerView = findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        filterTasksForDay(localTasks, tomorrow);

        adapter = new TaskAdapter(localTasks, this::taskClick);
        recyclerView.setAdapter(adapter);
        startOverdueChecker();
        // -----------------------------------------------------------------------

        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton listButton = findViewById(R.id.list);
        ImageButton profileButton = findViewById(R.id.profile);
        TextView dayAndMonth = findViewById(R.id.dayAndMonth);

        AppCompatButton filterToday = findViewById(R.id.filterToday);
        AppCompatButton filterTomorrow = findViewById(R.id.filterTomorrow);
        AppCompatButton filterMonth = findViewById(R.id.filterMonth);

        dayAndMonth.setText(tomorrow.get(Calendar.DAY_OF_MONTH) + " " + MONTH[tomorrow.get(Calendar.MONTH)]);
        LinearLayout profileTop = findViewById(R.id.topProfile);

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

        calendarButton.setOnTouchListener(pressEffect);
        homeButton.setOnTouchListener(pressEffect);
        listButton.setOnTouchListener(pressEffect);
        profileButton.setOnTouchListener(pressEffect);
        profileTop.setOnTouchListener(pressEffect);
        filterToday.setOnTouchListener(pressEffect);
        filterTomorrow.setOnTouchListener(pressEffect);
        filterMonth.setOnTouchListener(pressEffect);

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
                                    .setDuration(100))
                                    .withEndAction(task);
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

        filterToday.setOnClickListener(v -> {

            filterAnim.accept(v);

            filterToday.setTextColor(Color.parseColor("#FFFFFF"));
            filterTomorrow.setTextColor(Color.parseColor("#000000"));
            filterMonth.setTextColor(Color.parseColor("#000000"));

            filterToday.setBackgroundResource(R.drawable.bg_filter_today);
            filterTomorrow.setBackgroundResource(R.drawable.bg_filter_outline);
            filterMonth.setBackgroundResource(R.drawable.bg_filter_outline);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

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

            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        });

        Context context = this;
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
                                                    .withEndAction(() -> {
                                                        Intent intent = new Intent(context, MainActivity.class);
                                                        startActivity(intent);
                                                    })
                                    )
                    );

        });
        calendarButton.setOnClickListener(v -> bottomButtonsAnimation.accept(v, () -> {}));
        listButton.setOnClickListener(v -> {
            bottomButtonsAnimation.accept(v, () -> {
                Intent intent = new Intent(context, ListActivity.class);
                startActivity(intent);
            });
        });
        profileButton.setOnClickListener(v -> bottomButtonsAnimation.accept(v, () -> {}));
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
    }

    private void startOverdueChecker(){

        Runnable runnable = new Runnable(){
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run(){

                if(adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                handler.postDelayed(this, 30000); // каждые 30 секунд
            }
        };

        handler.post(runnable);
    }

    private void filterTasksForDay(List<Task> tasks, Calendar day) {
        Calendar startCal = (Calendar) day.clone();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        long startOfDay = startCal.getTimeInMillis();

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        long endOfDay = endCal.getTimeInMillis();

        tasks.removeIf(task -> task.date < startOfDay || task.date >= endOfDay);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void taskClick(Task task){

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle(task.name)
                .setItems(new String[]{"Выполнено","Удалить"},(d,which)->{

                    if (which == 0) {
                        task.done = true;
                        DataController.getInstance(this).updateTask(task);
                    }

                    if (which == 1) {
                        DataController.getInstance(this).removeTask(task);
                        localTasks.remove(task);
                    }

                    adapter.notifyDataSetChanged();

                }).create();

        dialog.show();
    }
}