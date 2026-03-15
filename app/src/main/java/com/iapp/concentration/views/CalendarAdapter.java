package com.iapp.concentration.views;

import android.graphics.Color;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.iapp.concentration.R;

import java.util.List;
import java.util.function.Consumer;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayHolder>{

    private List<String> days;
    private int selectedDay = -1;
    private Consumer<Integer> clickListener;

    public CalendarAdapter(List<String> days, Consumer<Integer> clickListener){
        this.days = days;
        this.clickListener = clickListener;
    }

    public void setDays(List<String> days){
        this.days = days;
    }

    public void setSelectedDay(int day){
        selectedDay = day;
    }

    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day,parent,false);

        return new DayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder holder, int position){

        String day = days.get(position);

        holder.dayText.setText(day);

        if(day.equals("")){
            holder.dayText.setText("");
            holder.itemView.setClickable(false);
            holder.dayText.setBackground(null);
            return;
        }

        holder.itemView.setClickable(true);

        int dayInt = Integer.parseInt(day);
        if(dayInt == selectedDay){
            holder.dayText.setBackgroundResource(R.drawable.calendar_selected);
            holder.dayText.setTextColor(Color.WHITE);

            holder.itemView.setAlpha(0.6f);
            holder.itemView.setScaleX(0.8f);
            holder.itemView.setScaleY(0.8f);

            holder.itemView.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.6f));
        } else {

            holder.dayText.setTextColor(Color.BLACK);
            holder.dayText.setBackground(null);

        }

        holder.itemView.setOnClickListener(v -> {

            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            // press animation
            v.animate()
                    .scaleX(0.88f)
                    .scaleY(0.88f)
                    .setDuration(80)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(120)

                    );

            clickListener.accept(dayInt);

        });
    }

    @Override
    public int getItemCount(){
        return days.size();
    }

    static class DayHolder extends RecyclerView.ViewHolder{

        TextView dayText;

        DayHolder(View v){
            super(v);
            dayText = v.findViewById(R.id.dayText);
        }
    }
}