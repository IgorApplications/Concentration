package com.iapp.concentration.activities;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.iapp.concentration.R;

import java.util.HashMap;
import java.util.Map;

public class ModeActivity extends AppCompatActivity {

    private EditText nameMode;
    private RadioGroup timeGroup;
    private static final Map<Integer, Long> timeMode = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        timeMode.put(R.id.mode_ac_time_1, 60 * 1000L);
        timeMode.put(R.id.mode_ac_time_2, 5 * 60 * 1000L);
        timeMode.put(R.id.mode_ac_time_3, 10 * 60 * 1000L);
        timeMode.put(R.id.mode_ac_time_4, 30 * 60 * 1000L);

        timeGroup = findViewById(R.id.mode_ac_time_group);
        nameMode = findViewById(R.id.mode_ac_name_mode);
        timeGroup.check(R.id.mode_ac_time_1);
    }

    public void goToPlant(View view) {
        int id = timeGroup.getCheckedRadioButtonId();

        if (!timeMode.containsKey(id)) {
            Toast.makeText(this,"Невозможно определить время выделенное для концентрации", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PlantActivity.class);
        intent.putExtra("title", nameMode.getText().toString());
        intent.putExtra("time", timeMode.get(id));
        startActivity(intent);
    }
}