package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.User;
import com.developersstack.edumanagement.util.security.PassswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignUpFormController {
    public AnchorPane context;
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtEmail;
    public TextField txtPassword;

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        String email=txtEmail.getText().toLowerCase();
        String firstName=txtFirstName.getText();
        String lastName=txtLastName.getText();
        String password=new PassswordManager().encrypt(txtPassword.getText().trim());
        User createdUser=new User(firstName,lastName,email,password);
        try{
            boolean isSaved=signup(createdUser);
            if(isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Welcome..!").show();
                setUi("LoginForm");
            }else{
                new Alert(Alert.AlertType.WARNING,"Try Again..!").show();
            }
        }catch (ClassNotFoundException|SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }



    }

    public void alreadyHaveAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();

    }
    private boolean signup(User user) throws ClassNotFoundException, SQLException {
        //load the driver
        Class.forName("com.mysql.cj.jdbc.Driver");//com.mysql.jdbc.Driver(deprecated)
        //Create a Connection
        Connection connection=
        DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        //write a SQL
        String sql="INSERT INTO user VALUES(?,?,?,?)";
        //create statement
        PreparedStatement  statement=connection.prepareStatement(sql);
        statement.setString(1,user.getEmail());
        statement.setString(2,user.getFirstname());
        statement.setString(3,user.getLastName());
        statement.setString(4,user.getPassword());
        //set sql into the statement and execute
        return statement.executeUpdate()>0; //INSERT,UPDATE,DELETE

    }
}
