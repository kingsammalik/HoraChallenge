package com.samapps.horachallenge.model;

public class BaseTask {
    private static Task newtask;
    private static Task completetask;

    public static Task getNewtask() {
        return newtask;
    }

    public static void setNewtask(Task newtask) {
        BaseTask.newtask = newtask;
    }

    public static Task getCompletetask() {
        return completetask;
    }

    public static void setCompletetask(Task completetask) {
        BaseTask.completetask = completetask;
    }
}
