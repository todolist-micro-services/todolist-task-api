package com.funnyproject.todolisttaskapi.task;

public class LinkUserToTaskRequest {

    private String user;
    private String task;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}

