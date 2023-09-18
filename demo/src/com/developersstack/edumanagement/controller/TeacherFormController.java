package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.Student;
import com.developersstack.edumanagement.model.Teacher;
import com.developersstack.edumanagement.view.tm.StudentTm;
import com.developersstack.edumanagement.view.tm.TeacherTm;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherFormController {

    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSearch;
    public Button btn;
    public TableView tblTeacher;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colContact;
    public TableColumn colAddress;
    public TableColumn colOption;
    public TextField txtContact;
    public AnchorPane context;
    String searchText = "";

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        setTeacherId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            setTableData(searchText);
        });

        tblTeacher.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        setData((TeacherTm) newValue);
                    }

                }
        );
    }


    private void setData(TeacherTm tm) {
        txtId.setText(tm.getCode());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtContact.setText(tm.getContact());
        btn.setText("Update Teacher");
    }

    private void setTableData(String searchText) {
        ObservableList<TeacherTm> oblist = FXCollections.observableArrayList();
        try{
            for (Teacher te : searchTeacher(searchText)) {
                Button btn = new Button("Delete");
                TeacherTm tm = new TeacherTm(
                        te.getCode(),
                        te.getName(),
                        te.getContact(),
                        te.getAddress(),
                        btn
                );


                btn.setOnAction((e) -> {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = conf.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        try{
                            if(deleteTeacher(te.getCode())){
                                new Alert(Alert.AlertType.INFORMATION, "Deleted.!").show();
                                setTableData(searchText);
                                setTeacherId();
                            }else{
                                new Alert(Alert.AlertType.WARNING,"Try Again").show();
                            }

                        }catch (ClassNotFoundException |SQLException e1){
                            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
                        }
                    }
                });
                oblist.add(tm);
            }
            tblTeacher.setItems(oblist);

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR, e.toString()).show();
        }


    }

    private void setTeacherId() {
        try{
            String lastId =getLastId();
            if(null!=lastId){
                String splitData[] = lastId.split("-");
                String lastIdIntegerAsAString = splitData[1];
                int lastIdIntegerAsAnInt = Integer.parseInt(lastIdIntegerAsAString);
                lastIdIntegerAsAnInt++;
                String generatedTeacherCode = "T-" + lastIdIntegerAsAnInt;
                txtId.setText(generatedTeacherCode);
            }else{
                txtId.setText("T-1");
            }

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR, e.toString()).show();
        }
    }

    public void saveTeacherOnAction(ActionEvent actionEvent) {
        Teacher teacher = new Teacher(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                txtContact.getText()
        );
        if (btn.getText().equalsIgnoreCase("Save Teacher")) {
            try{
                if(saveTeacher(teacher)){
                    setTeacherId();
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Teacher saved").show();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }

            }catch(ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }
        } else {
            try{
                if(updateTeacher(teacher)){
                    setTableData(searchText);
                    clear();
                    setTeacherId();
                    btn.setText("Save Teacher");
                    new Alert(Alert.AlertType.INFORMATION, "Teacher Updated").show();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }

            }catch(ClassNotFoundException |SQLException e1){
                new Alert(Alert.AlertType.ERROR, e1.toString()).show();
            }
        }
    }

    public void addNewTeacherOnAction(ActionEvent actionEvent) {
        clear();
        setTeacherId();
        btn.setText("Save Teacher");

    }

    public void clear() {
        txtName.clear();
        txtAddress.clear();
        txtContact.clear();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    public void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.centerOnScreen();
    }

    private boolean saveTeacher(Teacher teacher) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO teacher VALUES (?,?,?,?)");
        preparedStatement.setString(1, teacher.getCode());
        preparedStatement.setString(2, teacher.getName());
        preparedStatement.setString(3, teacher.getAddress());
        preparedStatement.setString(4, teacher.getContact());
        return preparedStatement.executeUpdate()>0;
    }
    private String getLastId()throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT code FROM teacher ORDER BY CAST(SUBSTRING(code,3)AS UNSIGNED )DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return  null;
    }
    public static List<Teacher> searchTeacher(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =
                DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms?verifyServerCertificate=false&useSSL=true", "root", "1234");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM teacher WHERE name LIKE ? OR address LIKE ?");
        preparedStatement.setString(1, text);
        preparedStatement.setString(2, text);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Teacher> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Teacher(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            ));
        }
        return list;
    }
    private boolean deleteTeacher(String code) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =
                DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms?verifyServerCertificate=false&useSSL=true", "root", "1234");
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM teacher WHERE code=?");
        preparedStatement.setString(1, code);
        return preparedStatement.executeUpdate()>0;
    }
    private boolean updateTeacher(Teacher teacher) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =
                DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lms?verifyServerCertificate=false&useSSL=true", "root", "1234");
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE teacher SET name=?,address=?,contact=? WHERE code=?");
        preparedStatement.setString(1,teacher.getName());
        preparedStatement.setString(2,teacher.getAddress());
        preparedStatement.setString(3,teacher.getContact());
        preparedStatement.setString(4,teacher.getCode());
        return preparedStatement.executeUpdate()>0;

    }
}
