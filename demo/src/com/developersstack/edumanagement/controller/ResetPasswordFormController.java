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
import java.util.Optional;

public class ResetPasswordFormController {
    public AnchorPane context;
    public TextField txtPassword;
    String selectedEmail="";
    public void setUserData(String email){
        selectedEmail=email;
        System.out.println(selectedEmail);
    }

    public void changePasswordOnAction(ActionEvent actionEvent) throws IOException {
        Optional<User> selectedUser=DataBase.userTable.stream().filter(e->e.getEmail().equals(selectedEmail)).findFirst();
        if(selectedUser.isPresent()){
            selectedUser.get().setPassword(new PassswordManager().encrypt(txtPassword.getText()));
            setUi("LoginForm");
        }else{
            new Alert(Alert.AlertType.ERROR,"Not Found").show();
        }
    }
    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
}
