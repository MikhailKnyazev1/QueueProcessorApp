package com.example.queueprocessorapp;

public class CustomMessage {
    private String firstName;
    private String lastName;
    private Integer age;
    private String profession;

    private long handledTimestamp;
    private String status;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    // Геттер и сеттер для handledTimestamp
    public long getHandledTimestamp() {
        return handledTimestamp;
    }

    public void setHandledTimestamp(long handledTimestamp) {
        this.handledTimestamp = handledTimestamp;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
