package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.Program;
import com.developersstack.edumanagement.model.Registration;
import com.developersstack.edumanagement.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;


public class RegistrationFormController {

    public AnchorPane context;
    public TextField txtId;
    public TextField txtStudent;
    public Button btn;
    public ComboBox<String> cmbProgram;
    public RadioButton rbtnPaid;
    public ToggleGroup paidState;
    String searchText="";

    public void initialize(){
        setRegistrationId();
        setStudentData();
        setProgramData();
        TextFields.bindAutoCompletion(txtStudent, studentArray);
    }


    private void setRegistrationId() {
        try{
            String lastId=getLastId();
            if(null!=lastId){
                String[] splitData=lastId.split("-");
                System.out.println(splitData.length);
                System.out.println(Arrays.toString(splitData));
                String lastIdIntegerAsAString=splitData[1];
                int lastIdIntegerAsAnInt=Integer.parseInt(lastIdIntegerAsAString);
                lastIdIntegerAsAnInt++;
                String generatedRegistrationCode="R-"+lastIdIntegerAsAnInt;
                txtId.setText(generatedRegistrationCode);
            }else{
                txtId.setText("R-1");
            }

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }
    ArrayList<String> studentArray=new ArrayList<>();
    private void setStudentData() {
        try {
            for (Student st : StudentFormController.searchStudent(searchText)) {
                studentArray.add(st.getStudentId() + ": " + st.getFullName());
            }
        } catch (ClassNotFoundException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.toString()).show();
        }
    }


    ArrayList<String> programsArray=new ArrayList<>();
    private void setProgramData() {
        try{
            for(Program p:ProgramsFormController.searchProgram(searchText)){
                programsArray.add(p.getCode()+": "+p.getName());
            }
            ObservableList<String> oblist= FXCollections.observableArrayList(programsArray);
            cmbProgram.setItems(oblist);

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }
    public void registerOnAction(ActionEvent actionEvent) {
        boolean paymentCompleteness= rbtnPaid.isSelected();
        Registration registration=new Registration(
                txtId.getText(),
                new Date(),
                txtStudent.getText().split("\\:")[0],
                cmbProgram.getValue().split("\\:")[0],
                paymentCompleteness
        );
        try{
            if(Registration(registration)){
                System.out.println(Registration(registration));
                setRegistrationId();
                clear();
                new Alert(Alert.AlertType.INFORMATION,"Registration completed").show();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try Again").show();
            }

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
    public void clear(){
        txtStudent.clear();
        cmbProgram.setValue(null);
        rbtnPaid.setSelected(true);
    }
    //===================================================================================
    private boolean Registration(Registration reg) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="INSERT INTO registration VALUES(?,?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, reg.getRegId());
        statement.setObject(2, reg.getRegDate());
        statement.setString(3,reg.getStudent());
        statement.setString(4, reg.getProgram());
        statement.setBoolean(5, reg.isPaymentCompleteness());
        return statement.executeUpdate()>0;
    }

    private String getLastId()throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT registration_id FROM registration ORDER BY CAST(SUBSTRING(registration_id,3)AS UNSIGNED )DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return  null;
    }
}
