package com.example.dinacharyaapkdemo.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dinacharyaapkdemo.AddNewTask;
import com.example.dinacharyaapkdemo.MainActivity;
import com.example.dinacharyaapkdemo.Model.ToDoModel;
import com.example.dinacharyaapkdemo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(MainActivity mainActivity, List<ToDoModel> todoList){
        this.todoList = todoList;
        activity = mainActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.show_each_task, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    //to delete task
    public void deleteTask(int position){
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("task").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);

    }

    //context method that returns activity
    public Context getContext(){
        return activity;
    }

    //to update the task
    //to update the task
    public void updateTask(int position){
        ToDoModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task", toDoModel.getTask());
        bundle.putString("due", toDoModel.getDue()); // Use "due" key instead of "task"
        bundle.putString("key", toDoModel.TaskId); // Use "key" key instead of "task"
        bundle.putString("timer", toDoModel.getFormattedTime()); // Use "timer" key
        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag());
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ToDoModel toDoModel= todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());
        holder.mDueDate.setText("Due on: "+toDoModel.getDue());
        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));

        holder.mFormattedTime.setText("Time: " + toDoModel.getFormattedTime());


        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 1);

                }else{
                    firestore.collection("task").document(toDoModel.TaskId).update("status", 0);
                }
            }
        });
        // Update task on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask(position);
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }
    @Override
    public int getItemCount() {

        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mDueDate,  mFormattedTime;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDate = itemView.findViewById(R.id.due_date_tv);
            mCheckBox = itemView.findViewById(R.id.mCheckBox);

            mFormattedTime = itemView.findViewById(R.id.due_timer); // Bind mFormattedTime to the appropriate TextView ID


        }
    }
}
