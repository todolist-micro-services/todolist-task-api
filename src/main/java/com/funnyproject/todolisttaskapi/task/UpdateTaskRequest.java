package com.funnyproject.todolisttaskapi.task;

public class UpdateTaskRequest {
    private String id;
    private String name;
    private String description;
    private String creationDate;
    private String creator;
    private String list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getList() {
        return list;
    }
    public void setList(String list) {
        this.list = list;
    }
}
