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
import java.util.Optional;

public class LoginFormController {
    public AnchorPane context;
    public TextField txtEmail;
    public TextField txtPassword;


    public void loginOnAction(ActionEvent actionEvent) throws IOException,ClassNotFoundException,SQLException {
        String email=txtEmail.getText().toLowerCase();
        String password=txtPassword.getText().trim();
        try{
            User selectedUser=login(email);
            if(null!=selectedUser) {
                if (new PassswordManager().checkPassword(password, selectedUser.getPassword())) {
                    new Alert(Alert.AlertType.INFORMATION, "Login Successful..!").show();
                    setUi("DashBoardForm");

                } else {
                    new Alert(Alert.AlertType.ERROR, "Wrong Password.!").show();
                }
            }else{
                new Alert(Alert.AlertType.WARNING, "User Not Found").show();
            }
        }catch (ClassNotFoundException|SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
        /*for(User user: DataBase.userTable){
            if(user.getEmail().equals(email)){
                if(user.getPassword().equals(password)) {
                    new Alert(Alert.AlertType.INFORMATION, "Login Successful..!").show();
                    System.out.println(user.toString());
                    return;
                }else{
                    new Alert(Alert.AlertType.ERROR, "Wrong Password.!").show();
                    return;
                }
            }
        }
        new Alert(Alert.AlertType.WARNING,String.format("Can't find any user for %s",email)).show();*/

        /*Optional<User> selectedUser =DataBase.userTable.stream().filter(e->e.getEmail().equals(email)).findFirst();
        if(selectedUser.isPresent()){
            if(new PassswordManager().checkPassword(password,selectedUser.get().getPassword())){
                new ArtType.INFORMATION, "Login Successful..!").show();
                setUi("DashBoardForm");
            }else{
                new Alert(Alert.AlertType.ERROR, "Wrong Password.!").show();
            }
        }else{
            new Alert(Alert.AlertType.ERROR, "Wrong Password.!").show();
        }*/
    }

    public void createAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("SignUpForm");
    }

    public void forgotPasswordOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ForgotPasswordForm");
    }
    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
    private User login(String email) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");//com.mysql.jdbc.Driver(deprecated)
        //Create a Connection
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        //write a SQL
        String sql="SELECT * FROM user WHERE email=?";
        PreparedStatement statement= connection.prepareStatement(sql);
        statement.setString(1,email);
        ResultSet resultSet= statement.executeQuery();
        if(resultSet.next()){
            User user=new User(
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("password")
            );
            System.out.println(user);
            return user;
        }
        return null;
    }
}
