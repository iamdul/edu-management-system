package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.Intake;
import com.developersstack.edumanagement.model.Program;
import com.developersstack.edumanagement.model.Student;
import com.developersstack.edumanagement.model.Teacher;
import com.developersstack.edumanagement.view.tm.IntakeTm;
import com.developersstack.edumanagement.view.tm.StudentTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class IntakeFormController {

    public AnchorPane context;


    public TextField txtId;


    public TextField txtName;


    public TextField txtSearch;


    public Button btn;


    public TableView<IntakeTm> tblIntake;


    public TableColumn<?, ?> colId;


    public TableColumn<?, ?> colIntakeName;


    public TableColumn<?, ?> colStartDate;


    public TableColumn<?, ?> colProgram;


    public TableColumn<?, ?> colCompleteness;


    public TableColumn<?, ?> colOption;


    public DatePicker txtStartDate;


    public ComboBox<String> cmbPrograms;
    String searchText="";

    public void initialize(){
        setIntakeId();
        setProgramData();
        setTableData(searchText);

        colId.setCellValueFactory(new PropertyValueFactory<>("intakeId"));
        colIntakeName.setCellValueFactory(new PropertyValueFactory<>("intakeName"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("intakeStartDate"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("programId"));
        colCompleteness.setCellValueFactory(new PropertyValueFactory<>("completeness"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
            setTableData(searchText);
        });

        tblIntake.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(null!=newValue){
                        setData((IntakeTm) newValue);
                    }

                }
        );
    }

    private void setData(IntakeTm tm) {
        txtId.setText(tm.getIntakeId());
        txtName.setText(tm.getIntakeName());
        txtStartDate.setValue(LocalDate.parse(tm.getIntakeStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        cmbPrograms.setValue(tm.getProgramId());
        btn.setText("Update Intake");
    }

    public void setTableData(String searchText){
        ObservableList<IntakeTm> oblist= FXCollections.observableArrayList();
        try{
            for(Intake intake:searchIntake(searchText)){
                Button btn=new Button("Delete");
                String completeness=intake.isIntakeCompletenesss()?"Finished":"Ongoing";
                IntakeTm tm=new IntakeTm(
                        intake.getIntakeId(),
                        intake.getIntakeName(),
                        new SimpleDateFormat("yyyy-MM-dd").format(intake.getStartDate()),
                        intake.getProgramId(),
                        completeness,
                        btn
                );
                System.out.println(tm.getIntakeStartDate());
                btn.setOnAction((e) -> {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = conf.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        try{
                           if(deleteIntake(intake.getIntakeId())){
                               new Alert(Alert.AlertType.INFORMATION,"Deleted.!").show();
                               setTableData(searchText);
                               setIntakeId();
                           }else{
                               new Alert(Alert.AlertType.WARNING,"Try Again").show();
                           }
                        }catch(ClassNotFoundException | SQLException e1){
                            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
                        }
                    }
                });
                oblist.add(tm);
            }
            tblIntake.setItems(oblist);

        }catch (ClassNotFoundException |SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }

    }


    ArrayList<String> programsArray=new ArrayList<>();
    private void setProgramData() {
        try{
            for(Program p:ProgramsFormController.searchProgram(searchText)){
                programsArray.add(p.getCode()+": "+p.getName());
            }
            ObservableList<String> oblist= FXCollections.observableArrayList(programsArray);
            cmbPrograms.setItems(oblist);

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }

    public void setIntakeId(){
        try{
            String lastId=getLastId();
            if(null!=lastId){
                String[] splitData = lastId.split("-");
                System.out.println(splitData.length);
                System.out.println(Arrays.toString(splitData));
                String lastIdIntegerAsAString = splitData[1];
                int lastIdIntegerAsAnInt = Integer.parseInt(lastIdIntegerAsAString);
                lastIdIntegerAsAnInt++;
                String generatedIntakeId = "I-" + lastIdIntegerAsAnInt;
                txtId.setText(generatedIntakeId);

            }else{
                txtId.setText("I-1");
            }

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }



    public void addNewIntakeOnAction(ActionEvent event) {
        clear();
        setIntakeId();
        btn.setText("Save Intake");
    }


    public void backToHomeOnAction(ActionEvent event) throws IOException {
        setUi("DashBoardForm");

    }


    public void saveIntakeOnAction(ActionEvent event) {
        Intake intake=new Intake(
                txtId.getText(),
                Date.from(txtStartDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtName.getText(),
                cmbPrograms.getValue().split("\\:")[0],
                false

        );
        if(btn.getText().equalsIgnoreCase("Save Intake")){
            try{
                if(saveIntake(intake)){
                    setIntakeId();
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Intake saved").show();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Try Again").show();
                }

            }catch (ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }
        }else{
            try{
                if(updateIntake(intake)){
                    setIntakeId();
                    clear();
                    setTableData(searchText);
                    btn.setText("Save Intake");
                    new Alert(Alert.AlertType.INFORMATION,"Intake Updated").show();
                }else{
                    new Alert(Alert.AlertType.INFORMATION,"Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }
        }
    }

    public void clear(){
        txtStartDate.setValue(null);
        txtName.clear();
        cmbPrograms.setValue(null);
        //setProgramData();
    }
    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    //=========================================
    private boolean saveIntake(Intake intake) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="INSERT INTO intake VALUES(?,?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, intake.getIntakeId());
        statement.setObject(2, intake.getStartDate());
        statement.setObject(3,intake.getIntakeName());
        statement.setString(4, intake.getProgramId());
        statement.setBoolean(5, intake.isIntakeCompletenesss());
        return statement.executeUpdate()>0;
    }

    private String getLastId()throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT intake_id FROM intake ORDER BY CAST(SUBSTRING(intake_id,3)AS UNSIGNED )DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return  null;
    }

    private List<Intake> searchIntake(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT * FROM  intake WHERE intake_name LIKE ?");
        statement.setString(1,text);
        ResultSet resultSet=statement.executeQuery();
        List<Intake> list=new ArrayList<>();
        while(resultSet.next()){
            list.add(new Intake(
                    resultSet.getString(1),
                    resultSet.getDate(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getBoolean(5)

            ));
        }
        return list;

    }
    private boolean deleteIntake(String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("DELETE FROM intake WHERE intake_id=?");
        statement.setString(1,id);
        return statement.executeUpdate()>0;
    }

    private boolean updateIntake(Intake intake) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="UPDATE intake SET start_date=?,intake_name=?,program_code=? WHERE intake_id=?";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setObject(1, intake.getStartDate());
        statement.setString(2,intake.getIntakeName());
        statement.setString(3,intake.getProgramId());
        statement.setString(4, intake.getIntakeId());
        return statement.executeUpdate()>0;
    }

}
