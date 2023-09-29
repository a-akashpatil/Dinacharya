package com.example.dinacharyaapkdemo.Model;

public class ToDoModel extends TaskId{
    public  String task, due, timer;


    public int status;


    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public int getStatus() {
        return status;
    }


    public String getFormattedTime() {
        return  timer;
    }
}
