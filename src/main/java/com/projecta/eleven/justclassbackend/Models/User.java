package com.projecta.eleven.justclassbackend.Models;

public class User {
    private final String uuid;

    private String name;

    private long age;

    private String address;

    public User(String uuid, String name, long age, String address) {
        this.uuid = uuid;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }
}
