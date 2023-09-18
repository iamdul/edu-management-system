package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.Student;
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
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class StudentFormController {

    public TextField txtId;
    public TextField txtName;
    public DatePicker txtDob;
    public TextField txtAddress;
    public AnchorPane context;
    public TableView tblStudent;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colDob;
    public TableColumn colAddress;
    public TableColumn colOption;
    public Button btn;
    public Button btnAddNewStudent;
    public TextField txtSearch;
    String searchText="";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("Dob"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        setStudentId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
            setTableData(searchText);
        });


        tblStudent.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(null!=newValue){
                        setData((StudentTm) newValue);
                    }

                }
        );
    }

    private void setData(StudentTm tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getFullName());
        txtDob.setValue(LocalDate.parse(tm.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtAddress.setText(tm.getAddress());
        btn.setText("Update Student");
    }

    private void setTableData(String searchText) {
        ObservableList<StudentTm> oblist= FXCollections.observableArrayList();
        try{
            for(Student st:searchStudent(searchText)) {
                Button btn = new Button("Delete");
                StudentTm tm = new StudentTm(
                        st.getStudentId(),
                        st.getFullName(),
                        new SimpleDateFormat("yyyy-MM-dd").format(st.getDateOfBirth()),
                        st.getAddress(),
                        btn
                );

                btn.setOnAction((e) -> {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = conf.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        try{
                            if(deleteStudent(st.getStudentId())){
                                new Alert(Alert.AlertType.INFORMATION,"Deleted.!").show();
                                setTableData(searchText);
                                setStudentId();
                            }else{
                                new Alert(Alert.AlertType.WARNING,"Try Again").show();
                            }
                        }catch (ClassNotFoundException | SQLException e1) {
                            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
                        }

                    }
                });
                oblist.add(tm);
            }
            tblStudent.setItems(oblist);

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }

    private void setStudentId() {
        try{
            String lastId=getLastId();
            if(null!=lastId) {
                String splitData[] = lastId.split("-");
                String lastIdIntegerAsAString = splitData[1];
                int lastIdIntegerAsAnInt = Integer.parseInt(lastIdIntegerAsAString);
                lastIdIntegerAsAnInt++;
                String generatedStudentId = "S-" + lastIdIntegerAsAnInt;
                txtId.setText(generatedStudentId);
            }else{
                txtId.setText("S-1");
            }
        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }


    public void saveStudentOnAction(ActionEvent actionEvent) {
        Student student=new Student(
                txtId.getText(),
                txtName.getText(),
                Date.from(txtDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtAddress.getText()
        );
        if(btn.getText().equalsIgnoreCase("Save Student")){
            try{
                if(saveStudent(student)){
                    System.out.println(student.getStudentId());
                    setStudentId();
                    System.out.println(txtId.getText());
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Student saved").show();
                }else {
                    new Alert(Alert.AlertType.INFORMATION,"Try Again").show();
                }

            }catch (ClassNotFoundException|SQLException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }
        }else{
            try{
                if(updateStudent(student)){
                    setStudentId();
                    clear();
                    setTableData(searchText);
                    btn.setText("Save Student");
                    new Alert(Alert.AlertType.INFORMATION,"Student Updated").show();
                }else {
                    new Alert(Alert.AlertType.INFORMATION,"Try Again").show();
                }

            }catch (ClassNotFoundException|SQLException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }
        }
    }

    private void clear(){
        txtDob.setValue(null);
        txtName.clear();
        txtAddress.clear();
    }

    public void addNewStudentOnAction(ActionEvent actionEvent) {
        clear();
        setStudentId();
        btn.setText("Save Student");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException{
        setUi("DashBoardForm");
    }
    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
    private boolean saveStudent(Student student) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="INSERT INTO student VALUES(?,?,?,?)";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, student.getStudentId());
        statement.setString(2, student.getFullName());
        statement.setObject(3,student.getDateOfBirth());
        statement.setString(4,student.getAddress());
        return statement.executeUpdate()>0;
    }
    private String getLastId()throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT student_id FROM student ORDER BY CAST(SUBSTRING(student_id,3)AS UNSIGNED )DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return  null;
    }

    public static List<Student> searchStudent(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT * FROM  student WHERE full_name LIKE ? OR address LIKE ?");
        statement.setString(1,text);
        statement.setString(2,text);
        ResultSet resultSet=statement.executeQuery();
        List<Student> list=new ArrayList<>();
        while(resultSet.next()){
            list.add(new Student(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDate(3),
                    resultSet.getString(4)

            ));
        }
        return list;

    }

    private boolean deleteStudent(String id)throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("DELETE FROM student WHERE student_id=?");
        statement.setString(1,id);
        return statement.executeUpdate()>0;

    }
    private boolean updateStudent(Student student) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="UPDATE student SET full_name=?,dob=?,address=? WHERE student_id=?";
        PreparedStatement statement=connection.prepareStatement(sql);

        statement.setString(1, student.getFullName());
        statement.setObject(2,student.getDateOfBirth());
        statement.setString(3,student.getAddress());
        statement.setString(4, student.getStudentId());
        return statement.executeUpdate()>0;
    }
}
