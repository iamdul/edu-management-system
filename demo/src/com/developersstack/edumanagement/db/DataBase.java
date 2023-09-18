package com.developersstack.edumanagement.db;

import com.developersstack.edumanagement.model.*;
import com.developersstack.edumanagement.util.security.PassswordManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DataBase {
    public static ArrayList<User> userTable= new ArrayList<>();
    public static ArrayList<Student> studentTable= new ArrayList<>();
    public  static ArrayList<Teacher> teacherTable=new ArrayList<>();
    public  static ArrayList<Program> programTable=new ArrayList<>();
    public  static ArrayList<Intake> intakeTable=new ArrayList<>();
    public static ArrayList<Registration> registrationTable=new ArrayList<>();


    static{
        userTable.add(
                new User(
                        "Dulanga",
                        "Kalhari",
                        "d@gmail.com",
                        new PassswordManager().encrypt("1234")
                )
        );
        teacherTable.add(new Teacher(
                    "T-1",
                    "Kamal",
                    "Colombo",
                    "0778586352"
                )

        );
        programTable.add(new Program(
                "P-1",
                "Developer",
                new String[]{"java", "php"},
                "T-1",
                45000.0
        ));
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse("1996-04-10");
            Date sqlDate = new Date(utilDate.getTime());

            studentTable.add(new Student(
                    "S-1",
                    "Dulanga",
                    sqlDate,
                    "Colombo"
            ));
        } catch (ParseException e) {
            e.printStackTrace(); // Handle or log the exception
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse("1996-04-10");
            Date sqlDate = new Date(utilDate.getTime());

            studentTable.add(new Student(
                    "S-2",
                    "Dukitha",
                    sqlDate,
                    "Colombo"
            ));
        } catch (ParseException e) {
            e.printStackTrace(); // Handle or log the exception
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse("1996-04-10");
            Date sqlDate = new Date(utilDate.getTime());

            studentTable.add(new Student(
                    "S-3",
                    "Milan",
                    sqlDate,
                    "Colombo"
            ));
        } catch (ParseException e) {
            e.printStackTrace(); // Handle or log the exception
        }
    }


}
