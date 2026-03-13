package com.iapp.concentration.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iapp.concentration.R;
import com.iapp.concentration.util.Task;
import com.iapp.concentration.util.TaskAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ListActivity extends AppCompatActivity {

    private final android.os.Handler handler = new android.os.Handler();
    private static final String[] MONTH = new String[] {"ЯНВАРЯ", "ФЕВРАЛЯ", "МАРТА", "АПРЕЛЯ", "МАЯ", "ИЮНЯ", "ИЮЛЯ", "АВГУСТА", "СЕНТЯБРЯ", "ОКТЯБРЯ", "НОЯБРЯ", "ДЕКАБРЯ"};
    private static final String[] DAY_WEEK = new String[]{"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    private RecyclerView recyclerView;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private SharedPreferences prefs;
    private static final String KEY = "TASKS";

    @Override
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // -----------------------------------------------------------------------
        RecyclerView recyclerView = findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        prefs = getSharedPreferences("tasks", MODE_PRIVATE);
        loadTasks();
        removeOldTasks();
        adapter = new TaskAdapter(tasks, this::taskClick);
        recyclerView.setAdapter(adapter);
        startOverdueChecker();

        // -----------------------------------------------------------------------

        ImageButton calendarButton = findViewById(R.id.calendar);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton listButton = findViewById(R.id.list);
        ImageButton profileButton = findViewById(R.id.profile);
        ImageButton addTaskButton = findViewById(R.id.addTask);
        TextView dayAndMonth = findViewById(R.id.dayAndMonth);

        Calendar calendar = new GregorianCalendar();
        dayAndMonth.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + MONTH[calendar.get(Calendar.MONTH)]);
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
        addTaskButton.setOnTouchListener(pressEffect);

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

        Context context = this;
        addTaskButton.setOnClickListener(v -> circleButtonAnim.accept(v, this::showAddDialog));
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
                                                    .withEndAction(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            Intent intent = new Intent(context, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                    )
                    );

        });
        calendarButton.setOnClickListener(bottomButtonsAnimation::accept);
        listButton.setOnClickListener(v -> {});
        profileButton.setOnClickListener(bottomButtonsAnimation::accept);
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
            @Override
            public void run(){

                if(adapter != null){
                    adapter.notifyDataSetChanged();
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

                    task.date = System.currentTimeMillis();


                    tasks.add(task);

                    saveTasks();

                    adapter.notifyDataSetChanged();

                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void removeOldTasks(){

        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);

        long startOfToday = today.getTimeInMillis();

        List<Task> filtered = new ArrayList<>();

        for(Task task : tasks){
            if(task.date >= startOfToday){
                filtered.add(task);
            }
        }

        tasks.clear();
        tasks.addAll(filtered);
    }




    @SuppressLint("NotifyDataSetChanged")
    private void taskClick(Task task){

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle(task.name)
                .setItems(new String[]{"Выполнено","Удалить"},(d,which)->{

                    if(which==0){
                        task.done = true;
                    }

                    if(which==1){
                        tasks.remove(task);
                    }

                    saveTasks();
                    adapter.notifyDataSetChanged();

                }).create();

        dialog.show();
    }


    private void saveTasks(){
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(KEY,json);
        editor.apply();
    }

    private void loadTasks(){
        Gson gson = new Gson();
        String json = prefs.getString(KEY,null);
        if (json!=null) {
            Type type = new TypeToken<ArrayList<Task>>(){}.getType();
            tasks = gson.fromJson(json,type);
        }
    }
}