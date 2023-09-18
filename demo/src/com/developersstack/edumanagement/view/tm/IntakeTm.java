package com.developersstack.edumanagement.view.tm;

import javafx.scene.control.Button;

import java.util.Date;

public class IntakeTm {
    private String intakeId;
    private String intakeName;
    private String intakeStartDate;
    private String programId;
    private String completeness;
    private Button btn;

    public IntakeTm() {
    }

    public IntakeTm(String intakeId, String intakeName, String intakeStartDate, String programId, String completeness, Button btn) {
        this.intakeId = intakeId;
        this.intakeName = intakeName;
        this.intakeStartDate = intakeStartDate;
        this.programId = programId;
        this.completeness = completeness;
        this.btn = btn;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public String getIntakeStartDate() {
        return intakeStartDate;
    }

    public void setIntakeStartDate(String intakeStartDate) {
        this.intakeStartDate = intakeStartDate;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getCompleteness() {
        return completeness;
    }

    public void setCompleteness(String completeness) {
        this.completeness = completeness;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return "IntakeTm{" +
                "intakeId='" + intakeId + '\'' +
                ", intakeName='" + intakeName + '\'' +
                ", intakeStartDate=" + intakeStartDate +
                ", programId='" + programId + '\'' +
                ", completeness='" + completeness + '\'' +
                ", btn=" + btn +
                '}';
    }
}
