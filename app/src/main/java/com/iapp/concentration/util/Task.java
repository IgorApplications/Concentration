package com.iapp.concentration.util;

public class Task {

    long id;
    public String name;
    public int hour;
    public int minute;
    public long date;
    public boolean done;

    public Task() {
        id = System.currentTimeMillis();
    }

    private Task(long id, String name, int hour, int minute, long date, boolean done) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.date = date;
        this.done = done;
        id = System.currentTimeMillis();
    }

    public Task getCopy() {
        return new Task(System.currentTimeMillis(), name, hour, minute, date, done);
    }
}
