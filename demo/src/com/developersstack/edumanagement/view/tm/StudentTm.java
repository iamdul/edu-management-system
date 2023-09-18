package com.developersstack.edumanagement.view.tm;

import javafx.scene.control.Button;

public class StudentTm {
    private String id;
    private String fullName;
    private String Dob;
    private String address;
    private Button btn;

    public StudentTm() {
    }

    public StudentTm(String id, String fullName, String dob, String address, Button btn) {
        this.id = id;
        this.fullName = fullName;
        Dob = dob;
        this.address = address;
        this.btn = btn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return "StudentTm{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", Dob='" + Dob + '\'' +
                ", address='" + address + '\'' +
                ", btn=" + btn +
                '}';
    }
}
