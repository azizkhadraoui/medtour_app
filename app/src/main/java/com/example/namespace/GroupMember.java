package com.example.namespace;

public class GroupMember {
    private String name;
    private String passportNumber;

    public GroupMember(String name, String passportNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
    }

    public String getName() {
        return name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }
}
