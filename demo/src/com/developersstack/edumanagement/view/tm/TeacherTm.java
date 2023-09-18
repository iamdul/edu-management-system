package com.developersstack.edumanagement.view.tm;

import javafx.scene.control.Button;

public class TeacherTm {
    private String code;
    private String name;
    private String contact;
    private String address;
    private Button btn;

    public TeacherTm() {
    }

    public TeacherTm(String code, String name, String contact, String address, Button btn) {
        this.code = code;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.btn = btn;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
        return "TeacherTm{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", btn=" + btn +
                '}';
    }
}
