package com.iapp.concentration.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.iapp.concentration.R;
import com.iapp.concentration.util.DataController;
import com.iapp.concentration.util.Task;
import com.iapp.concentration.views.CalendarAdapter;
import com.iapp.concentration.views.TaskAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CalendarActivity extends AppCompatActivity {

    private final android.os.Handler handler = new android.os.Handler();
    private final Calendar currentCalendar = Calendar.getInstance();
    private CalendarAdapter calendarAdapter;
    private int selectedDay = -1;

    private TextView currentMonth;
    private TextView prevMonth;
    private TextView nextMonth;
    private TaskAdapter taskAdapter;

    private final List<Task> localTasks = new ArrayList<>();

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        LinearLayout header = findViewById(R.id.header);
        header.setAlpha(0f);
        header.animate().alpha(1f).setDuration(500);

        // ------------------------------------------------------------------

        RecyclerView recyclerView = findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        taskAdapter = new TaskAdapter(localTasks, this::taskClick);
        recyclerView.setAdapter(taskAdapter);
        startOverdueChecker();

        // -----------------------------------------------------------------

        AppCompatButton filterToday = findViewById(R.id.filterToday);
        AppCompatButton filterTomorrow = findViewById(R.id.filterTomorrow);
        AppCompatButton filterMonth = findViewById(R.id.filterMonth);

        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton listButton = findViewById(R.id.list);
        ImageButton profileButton = findViewById(R.id.profile);
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

            Intent intent = new Intent(this, TomorrowActivity.class);
            startActivity(intent);
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
        listButton.setOnClickListener(v -> {
            /*
            MotionLayout motion = findViewById(R.id.bottomMenu);
            motion.transitionToEnd();
            v.postDelayed(() -> startActivity(new Intent(context, ListActivity.class)), 350);
             */

            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });
        profileButton.setOnClickListener(bottomButtonsAnimation::accept);

        RecyclerView calendarRecycler = findViewById(R.id.calendarRecycler);
        calendarRecycler.setLayoutManager(new GridLayoutManager(this,7));

        calendarAdapter = new CalendarAdapter(generateCalendar(), day -> {

            selectedDay = day;

            calendarAdapter.setSelectedDay(day);
            calendarAdapter.notifyDataSetChanged();

            Calendar dayFilter = (Calendar) currentCalendar.clone();
            dayFilter.set(Calendar.DAY_OF_MONTH, day);

            List<Task> startTasks = DataController.getInstance(this).getCopyTasks();
            filterTasksForDay(startTasks, dayFilter);

            localTasks.clear();
            localTasks.addAll(startTasks);

            taskAdapter.notifyDataSetChanged();
        });
        calendarRecycler.setAdapter(calendarAdapter);

        currentMonth = findViewById(R.id.currentMonth);
        prevMonth = findViewById(R.id.prevMonth);
        nextMonth = findViewById(R.id.nextMonth);

        updateMonthLabels();

        ImageView arrowLeft = findViewById(R.id.arrowLeft);
        ImageView arrowRight = findViewById(R.id.arrowRight);

        arrowLeft.setOnClickListener(v -> {

            arrowAnimation(v);

            currentCalendar.add(Calendar.MONTH,-1);

            if (selectedDay != -1) {
                Calendar dayFilter = (Calendar) currentCalendar.clone();
                dayFilter.set(Calendar.DAY_OF_MONTH, selectedDay);

                List<Task> startTasks = DataController.getInstance(this).getCopyTasks();
                filterTasksForDay(startTasks, dayFilter);

                localTasks.clear();
                localTasks.addAll(startTasks);
            }
            refreshCalendar();
            taskAdapter.notifyDataSetChanged();

        });

        arrowRight.setOnClickListener(v -> {

            arrowAnimation(v);

            currentCalendar.add(Calendar.MONTH,1);

            if (selectedDay != -1) {
                Calendar dayFilter = (Calendar) currentCalendar.clone();
                dayFilter.set(Calendar.DAY_OF_MONTH, selectedDay);

                List<Task> startTasks = DataController.getInstance(this).getCopyTasks();
                filterTasksForDay(startTasks, dayFilter);

                localTasks.clear();
                localTasks.addAll(startTasks);
            }
            refreshCalendar();
            taskAdapter.notifyDataSetChanged();
        });

        LinearLayout createButton = findViewById(R.id.createButton);

        createButton.setOnTouchListener((v,event)->{

            if(event.getAction()==MotionEvent.ACTION_DOWN){

                v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(80);

            }

            if(event.getAction()==MotionEvent.ACTION_UP ||
                    event.getAction()==MotionEvent.ACTION_CANCEL){

                v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120);

            }

            return false;
        });

        Context context = this;
        createButton.setOnClickListener(v -> {

            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1.05f)
                                    .scaleY(1.05f)
                                    .setDuration(120)
                                    .withEndAction(() ->
                                            v.animate()
                                                    .scaleX(1f)
                                                    .scaleY(1f)
                                                    .setDuration(120)
                                                    .withEndAction(() -> {
                                                        if (selectedDay == -1) {
                                                            Toast.makeText(context, "Выберите день", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        showAddDialog();

                                                    })
                                    )
                    );
        });
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

                    taskAdapter.notifyDataSetChanged();

                }).create();

        dialog.show();
    }

    private void startOverdueChecker(){

        Runnable runnable = new Runnable(){
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run(){

                if(taskAdapter != null) {
                    taskAdapter.notifyDataSetChanged();
                }

                handler.postDelayed(this, 30000); // каждые 30 секунд
            }
        };

        handler.post(runnable);
    }


    private void showAddDialog(){

        EditText input = new EditText(this);
        input.setHint("Название дела");

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle("Новое дело")
                .setView(input)
                .setPositiveButton("Далее",(d,w)->{

                    String name = input.getText().toString();

                    if(name.isEmpty()){
                        Toast.makeText(this,"Введите название", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showTimePicker(name);

                })
                .setNegativeButton("Отмена",null)
                .create();

        dialog.show();
    }

    private void showTimePicker(String name){

        Calendar now = Calendar.getInstance();

        @SuppressLint("NotifyDataSetChanged")
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                R.style.CustomTimePicker,
                (view,hour,minute)->{

                    Task task = new Task();

                    task.name = name;
                    task.hour = hour;
                    task.minute = minute;
                    task.done = false;

                    Calendar calendar = (Calendar) currentCalendar.clone();
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    task.date = calendar.getTimeInMillis();

                    DataController.getInstance(this).addNewTask(task);
                    localTasks.add(task);

                    taskAdapter.notifyDataSetChanged();

                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshCalendar(){

        RecyclerView recycler = findViewById(R.id.calendarRecycler);

        recycler.animate()
                .alpha(0f)
                .translationX(-40f)
                .setDuration(120)
                .withEndAction(() -> {

                    calendarAdapter.setDays(generateCalendar());
                    calendarAdapter.notifyDataSetChanged();
                    updateMonthLabels();

                    recycler.setTranslationX(40f);

                    recycler.animate()
                            .alpha(1f)
                            .translationX(0)
                            .setDuration(200);

                });
    }

    private void arrowAnimation(View v){

        v.animate().cancel();

        float direction = v.getId() == R.id.arrowLeft ? -25f : 25f;

        v.animate()
                .translationX(direction)
                .scaleX(0.75f)
                .scaleY(0.75f)
                .alpha(0.6f)
                .setDuration(100)
                .withEndAction(() ->
                        v.animate()
                                .translationX(0)
                                .scaleX(1.1f)
                                .scaleY(1.1f)
                                .alpha(1f)
                                .setDuration(140)
                                .withEndAction(() ->
                                        v.animate()
                                                .scaleX(1f)
                                                .scaleY(1f)
                                                .setDuration(80)
                                )
                );
    }

    private List<String> generateCalendar(){

        List<String> days = new ArrayList<>();

        Calendar calendar = (Calendar) currentCalendar.clone();

        calendar.set(Calendar.DAY_OF_MONTH,1);

        int firstDay = calendar.get(Calendar.DAY_OF_WEEK)-1;

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i=0;i<firstDay;i++)
            days.add("");

        for(int i=1;i<=daysInMonth;i++)
            days.add(String.valueOf(i));

        while(days.size()<42)
            days.add("");

        return days;
    }

    private void updateMonthLabels(){

        String[] months = {
                "ЯНВАРЬ","ФЕВРАЛЬ","МАРТ","АПРЕЛЬ","МАЙ","ИЮНЬ",
                "ИЮЛЬ","АВГУСТ","СЕНТЯБРЬ","ОКТЯБРЬ","НОЯБРЬ","ДЕКАБРЬ"
        };

        int current = currentCalendar.get(Calendar.MONTH);

        currentMonth.setText(months[current]);

        prevMonth.setText(months[(current+11)%12]);

        nextMonth.setText(months[(current+1)%12]);

    }
}