package com.iapp.concentration.util;

import android.annotation.SuppressLint;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iapp.concentration.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Holder>{

    private final List<Task> tasks;
    private final Consumer<Task> click;


    public TaskAdapter(List<Task> tasks, Consumer<Task> click){
        this.tasks = tasks;
        this.click = click;
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView name;
        TextView time;
        TextView status;
        View root;
        View dot;

        public Holder(View v){
            super(v);

            dot = v.findViewById(R.id.taskDot);
            root = v.findViewById(R.id.taskRoot);
            name = v.findViewById(R.id.taskName);
            time = v.findViewById(R.id.taskTime);
            status = v.findViewById(R.id.taskStatus);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task,parent,false);

        return new Holder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(Holder holder,int i){

        Task task = tasks.get(i);

        holder.name.setText(task.name);

        holder.time.setText(
                String.format(Locale.getDefault(),
                        "%02d:%02d",
                        task.hour,
                        task.minute)
        );

        Calendar now = Calendar.getInstance();

        Calendar taskTime = Calendar.getInstance();
        taskTime.setTimeInMillis(task.date);

        taskTime.set(Calendar.HOUR_OF_DAY, task.hour);
        taskTime.set(Calendar.MINUTE, task.minute);
        taskTime.set(Calendar.SECOND, 0);
        taskTime.set(Calendar.MILLISECOND, 0);

        boolean overdue = !task.done && now.after(taskTime);


        if(task.done){

            holder.root.setBackgroundResource(R.drawable.bg_task_done);
            holder.status.setText("Выполнено");
            holder.name.setAlpha(0.5f);

            holder.dot.setBackgroundResource(R.drawable.green_dot);

        }
        else if(overdue){

            holder.root.animate()
                    .alpha(0.85f)
                    .setDuration(300)
                    .start();
            holder.root.setBackgroundResource(R.drawable.bg_task_overdue);
            holder.status.setText("Просрочено");

            holder.dot.setBackgroundResource(R.drawable.red_dot);

        }
        else{

            holder.root.setBackgroundResource(R.drawable.bg_task_item);
            holder.status.setText("В процессе");

            holder.dot.setBackgroundResource(R.drawable.red_dot);
        }

        holder.itemView.setOnClickListener(v -> {

            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            v.animate()
                    .scaleX(0.94f)
                    .scaleY(0.94f)
                    .setDuration(80)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(120)
                                    .withEndAction(() -> click.accept(task))
                    );

        });
        holder.itemView.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                    v.animate().cancel();

                    v.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(80)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                    break;


                case MotionEvent.ACTION_UP:

                    v.animate().cancel();

                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .setInterpolator(new DecelerateInterpolator())
                            .withEndAction(() -> click.accept(task))
                            .start();

                    break;


                case MotionEvent.ACTION_CANCEL:

                    v.animate().cancel();

                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .start();

                    break;
            }

            return true;
        });


    }

    @Override
    public int getItemCount(){
        return tasks.size();
    }
}
