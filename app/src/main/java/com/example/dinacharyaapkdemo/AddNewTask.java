package com.example.dinacharyaapkdemo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "AddNewTask";

    private TextView setDueDate, setDueTime;
    private TextView userName;
    private EditText taskEdit;
    private Button saveTask;
    private FirebaseFirestore firestore;
    private String dueDate = "";
    private Context context;
    private String id = "";
    private String dueDateUpdate;
    private String formattedTime = "";

    private int notificationId = 1; // Unique ID for each notification
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123; // You can choose any value



    public static AddNewTask newInstance() {
        return new AddNewTask();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView dueTimerTextView = view.findViewById(R.id.due_timer);


        setDueDate = view.findViewById(R.id.set_dueDate);
        setDueTime = view.findViewById(R.id.set_time);
        saveTask = view.findViewById(R.id.saveButton);
        taskEdit = view.findViewById(R.id.taskEditText);
        firestore = FirebaseFirestore.getInstance();








        // Set up the notification channel
        createNotificationChannel();

        //save updated task
        boolean isUpdate = false;
        final Bundle bundle = getArguments();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("key");
            dueDateUpdate = bundle.getString("due");
            formattedTime = bundle.getString("timer"); // Add this line to retrieve the time
            taskEdit.setText(task);
            setDueTime.setText(formattedTime);
            setDueDate.setText(dueDate);


            if (task.length() > 0) {
                saveTask.setEnabled(false);
                saveTask.setBackgroundColor(Color.GRAY);
            }
        }

        taskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    saveTask.setEnabled(false);
                    saveTask.setBackgroundColor(Color.GRAY);
                } else {
                    saveTask.setEnabled(true);
                    saveTask.setBackgroundColor(getResources().getColor(R.color.purple_500));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //set due date

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, day);

                        Calendar currentCalendar = Calendar.getInstance();

                        if (selectedCalendar.getTimeInMillis() >= currentCalendar.getTimeInMillis()) {
                            setDueDate.setText(day + "-" + (month + 1) + "-" + year);
                            dueDate = day + "-" + (month + 1) + "-" + year;
                        } else {
                            Toast.makeText(context, "Please select a date equal to or later than the current date.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });


        setDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, i);
                        selectedTime.set(Calendar.MINUTE, i1);

                        Calendar currentTime = Calendar.getInstance();

                        if (selectedTime.getTimeInMillis() >= currentTime.getTimeInMillis()) {
                            formattedTime = i + ":" + i1;
                            setDueTime.setText(FormatTime(i, i1));
                        } else {
                            Toast.makeText(context, "Please select a time equal to or later than the current time.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, false);

                timePickerDialog.show();
            }
        });


        //        boolean finalIsUpdate = isUpdate;
        boolean finalIsUpdate = isUpdate;
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = taskEdit.getText().toString();


                if (finalIsUpdate) {
                    firestore.collection("task").document(id)
                            .update("task", task, "due", dueDate, "timer", formattedTime)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Task Updated Successfully", Toast.LENGTH_SHORT).show();

                                        Calendar currentCalendar = Calendar.getInstance();
                                        int currentYear = currentCalendar.get(Calendar.YEAR);
                                        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
                                        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
                                        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                                        int currentMinute = currentCalendar.get(Calendar.MINUTE);

                                        // Convert the selected notifyDate and notifyTime to integers
                                        String[] dateParts = dueDate.split("-");
                                        int notifyYear = Integer.parseInt(dateParts[2]);
                                        int notifyMonth = Integer.parseInt(dateParts[1]);
                                        int notifyDay = Integer.parseInt(dateParts[0]);

                                        String[] timeParts = formattedTime.split(":");
                                        int notifyHour = Integer.parseInt(timeParts[0]);
                                        int notifyMinute = Integer.parseInt(timeParts[1]);

                                        // Subtract 15 minutes from the notifyHour and notifyMinute
                                        notifyMinute -= 15;
                                        if (notifyMinute < 0) {
                                            notifyMinute += 60;
                                            notifyHour -= 1;
                                        }

                                        // Check if the current time matches the calculated notification time
                                        if (currentYear == notifyYear &&
                                                currentMonth == notifyMonth &&
                                                currentDay == notifyDay &&
                                                currentHour == notifyHour &&
                                                currentMinute >= notifyMinute) {


                                                // Permission is already granted, so you can proceed to show the notification.
                                                showNotification(context, "Hurry up..! Your task dues Now..", dueDate, formattedTime);


                                        }

                                    } else {
                                        Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    if (task.isEmpty()) {
                        Toast.makeText(context, "Empty task is not allowed!", Toast.LENGTH_SHORT).show();


                    } else {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("task", task);
                        taskMap.put("due", dueDate);
                        taskMap.put("status", 0);
                        taskMap.put("timer", formattedTime); // Make sure formattedTime is not null
                        taskMap.put("time", FieldValue.serverTimestamp());
                        taskMap.put("userId", userId);



                        firestore.collection("task").add(taskMap)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show();



                                            Calendar currentCalendar = Calendar.getInstance();
                                            int currentYear = currentCalendar.get(Calendar.YEAR);
                                            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // Month is 0-based, so add 1
                                            int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
                                            int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                                            int currentMinute = currentCalendar.get(Calendar.MINUTE);

                                            // Convert the selected notifyDate and notifyTime to integers
                                            String[] dateParts = dueDate.split("-");
                                            int notifyYear = Integer.parseInt(dateParts[2]);
                                            int notifyMonth = Integer.parseInt(dateParts[1]);
                                            int notifyDay = Integer.parseInt(dateParts[0]);

                                            String[] timeParts = formattedTime.split(":");
                                            int notifyHour = Integer.parseInt(timeParts[0]);
                                            int notifyMinute = Integer.parseInt(timeParts[1]);

                                            // Subtract 15 minutes from the notifyHour and notifyMinute
                                            notifyMinute -= 15;
                                            if (notifyMinute < 0) {
                                                notifyMinute += 60;
                                                notifyHour -= 1;
                                            }

                                            // Check if the current time matches the calculated notification time
                                            if (currentYear == notifyYear &&
                                                    currentMonth == notifyMonth &&
                                                    currentDay == notifyDay &&
                                                    currentHour == notifyHour &&
                                                    currentMinute >= notifyMinute) {
                                                // The current date and time match the notification time, so show the notification


                                                // Permission is already granted, so you can proceed to show the notification.
                                                showNotification(context, "Hurry up..! Your task dues Now..", dueDate, formattedTime);


                                            }

                                        } else {
                                            Toast.makeText(context, "Failed to save task", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                dismiss();
            }
        });


    }

    public String FormatTime(int hour, int minute) {
        String time;
        String amPm;

        if (hour >= 0 && hour < 12) {
            amPm = "AM";
            if (hour == 0) {
                hour = 12;
            }
        } else {
            amPm = "PM";
            if (hour > 12) {
                hour -= 12;
            }
        }

        String formattedHour = (hour < 10) ? "0" + hour : String.valueOf(hour);
        String formattedMinute = (minute < 10) ? "0" + minute : String.valueOf(minute);

        time = formattedHour + ":" + formattedMinute + " " + amPm;
        return time;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private static void showNotification(Context context, String event, String date, String time) {
        // Create an intent for when the notification is clicked
        Intent intent = new Intent(context, AlarmBroadcast.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", event);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.enableVibration(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Hurry up..! Your task dues now..!")
                .setContentText( "\nDate: " + date + ", Time: " + time) // Include task text in content
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification(context, "Hurry up..! Your task dues Now..", dueDate, formattedTime);
            } else {
                Toast.makeText(context, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(TAG, "Context attached: " + context);
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof  OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

