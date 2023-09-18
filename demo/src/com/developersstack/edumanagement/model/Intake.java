package com.developersstack.edumanagement.model;

import java.util.Date;

public class Intake {
    private String intakeId;
    private Date startDate;

    private String intakeName;
    private String programId;


    private boolean intakeCompletenesss;

    public Intake() {
    }

    public Intake(String intakeId, Date startDate, String intakeName, String programId, boolean intakeCompletenesss) {
        this.intakeId = intakeId;
        this.startDate = startDate;
        this.intakeName = intakeName;
        this.programId = programId;
        this.intakeCompletenesss = intakeCompletenesss;
    }


    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getIntakeName() {
        return intakeName;
    }

    public void setIntakeName(String intakeName) {
        this.intakeName = intakeName;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public boolean isIntakeCompletenesss() {
        return intakeCompletenesss;
    }

    public void setIntakeCompletenesss(boolean intakeCompletenesss) {
        this.intakeCompletenesss = intakeCompletenesss;
    }

    @Override
    public String toString() {
        return "Intake{" +
                "intakeId='" + intakeId + '\'' +
                ", startDate=" + startDate +
                ", intakeName='" + intakeName + '\'' +
                ", programId='" + programId + '\'' +
                ", intakeCompletenesss=" + intakeCompletenesss +
                '}';
    }
}
