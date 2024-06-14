package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    private TextView taskTitleTextView;
    private TextView taskDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_calendar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_notes) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_calendar) {
                return true;
            }

            return false;
        });

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handle date selection
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();

                // Fetch tasks for selectedDate from Firestore
                fetchTasksForDate(selectedDate);
            }
        });
    }

    private void fetchTasksForDate(Date selectedDate) {
        CollectionReference tasksRef = Utility.getCollectionReferenceForTasks();

        // Convert selectedDate to start and end of the day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        tasksRef.whereGreaterThanOrEqualTo("deadline", new Timestamp(startOfDay))
                .whereLessThanOrEqualTo("deadline", new Timestamp(endOfDay))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder taskTitles = new StringBuilder();
                        StringBuilder taskDescriptions = new StringBuilder();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String taskTitle = document.getString("title"); // Assuming you have a field "title"
                            String taskContent = document.getString("content"); // Assuming you have a field "content"
                            taskTitles.append(taskTitle).append("\n");
                            taskDescriptions.append(taskContent).append("\n");
                        }
                        // Update UI with tasks
                        taskTitleTextView.setText(taskTitles.toString());
                        taskDescriptionTextView.setText(taskDescriptions.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Utility.showToast(CalendarActivity.this, "Failed to fetch tasks: " + e.getMessage());
                    }
                });
    }
}
