package com.developersstack.edumanagement.controller;

import com.developersstack.edumanagement.db.DataBase;
import com.developersstack.edumanagement.model.Program;
import com.developersstack.edumanagement.model.Student;
import com.developersstack.edumanagement.model.Teacher;
import com.developersstack.edumanagement.view.tm.ProgramTm;
import com.developersstack.edumanagement.view.tm.TeacherTm;
import com.developersstack.edumanagement.view.tm.TechAddTm;
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
import java.util.*;

public class ProgramsFormController {

    public AnchorPane context;


    public TextField txtId;


    public TextField txtName;


    public TextField txtSearch;


    public Button btnSaveProgram;


    public TableView<ProgramTm> tblProgram;


    public TableColumn<?, ?> colId;


    public TableColumn<?, ?> colName;


    public TableColumn<?, ?> colTeacher;


    public TableColumn<?, ?> colTech;


    public TableColumn<?, ?> colCost;


    public TableColumn<?, ?> colOption;


    public TextField txtCost;


    public ComboBox<String> cmdTeacher;


    public TextField txtTechnology;


    public TableView<TechAddTm> tblTechnologies;


    public TableColumn<?, ?> colTId;


    public TableColumn<?, ?> colTTechnology;


    public TableColumn<?, ?> colTRemove;
    String searchText="";
    public void initialize(){
        setProgramCode();
        setTeachers();
        setTableData(searchText);

        colTId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colTTechnology.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTRemove.setCellValueFactory(new PropertyValueFactory<>("btn"));

        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        colTech.setCellValueFactory(new PropertyValueFactory<>("btnTech"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
            setTableData(searchText);
        });

        tblProgram.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        setData((ProgramTm) newValue);
                    }

                }
        );
    }
    private void setData(ProgramTm tm) {
        tmList.clear();
        txtId.setText(tm.getCode());
        txtName.setText(tm.getName());
        txtCost.setText(String.valueOf(tm.getCost()));
        cmdTeacher.setValue(tm.getTeacher());

        Program selectedProgram = null;
        try{
            for (Program p : searchProgram(searchText)) {
                if (tm.getCode().equals(p.getCode())) {
                    selectedProgram = p;
                    break;
                }
            }

            if (selectedProgram != null) {
                int pointer=1;
                for (String technology : selectedProgram.getTechnologies()) {
                    Button removeButton = new Button("Remove");
                    TechAddTm techTm = new TechAddTm(pointer++, technology, removeButton);

                    removeButton.setOnAction(e -> {
                        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                                "Are you sure?", ButtonType.YES, ButtonType.NO);
                        Optional<ButtonType> buttonType = conf.showAndWait();
                        if (buttonType.get() == ButtonType.YES) {
                            TechAddTm techToRemove=techTm;
                            tmList.remove(techToRemove);
                            new Alert(Alert.AlertType.INFORMATION,"Deleted.!").show();

                        }
                    });
                    tmList.add(techTm);
                }
                tblTechnologies.setItems(tmList);
            }
            btnSaveProgram.setText("Update Program");

        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }

    }
    ArrayList<String> teachersArray=new ArrayList<>();
    private void setTeachers() {
        try{
            for(Teacher te:TeacherFormController.searchTeacher(searchText)){
                teachersArray.add(te.getCode()+": "+te.getName());
            }
            ObservableList<String> oblist= FXCollections.observableArrayList(teachersArray);
            cmdTeacher.setItems(oblist);

        }catch(ClassNotFoundException |SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }

    }

    public void setProgramCode() {
        try{
            String lastId=getLastCode();
            if(null!=lastId){
                String splitData[]=lastId.split("-");
                String lastIdIntegerAsAString=splitData[1];
                int lastIdIntegerAsAnInt=Integer.parseInt(lastIdIntegerAsAString);
                lastIdIntegerAsAnInt++;
                String generatedProgramCode="P-"+lastIdIntegerAsAnInt;
                txtId.setText(generatedProgramCode);
            }else{
                txtId.setText("P-1");
            }
        }catch(ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }
    }

    public void setTableData(String searchText){
        ObservableList<ProgramTm> programsTmList=FXCollections.observableArrayList();
        try{
            for(Program p:searchProgram(searchText)){
                Button techButton=new Button("Show Technologies");
                Button removeButton=new Button("Delete");
                ProgramTm tm=new ProgramTm(
                        p.getCode(),
                        p.getName(),
                        p.getTeacherId(),
                        techButton,
                        p.getCost(),
                        removeButton
                );
                removeButton.setOnAction((e) -> {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = conf.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        try{
                            if(deleteProgram(p.getCode())){
                                new Alert(Alert.AlertType.INFORMATION,"Deleted.!").show();
                                setTableData(searchText);
                                setProgramCode();
                            }else{
                                new Alert(Alert.AlertType.WARNING,"Try Again").show();
                            }

                        }catch (ClassNotFoundException | SQLException e1){
                            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
                        }
                    }
                });
                ContextMenu contextMenu = new ContextMenu();
                for(String tech:p.getTechnologies()){
                    contextMenu.getItems().add(new MenuItem(tech));
                }
                techButton.setOnAction(event -> {
                    double screenX = techButton.localToScreen(0, techButton.getHeight()).getX();
                    double screenY = techButton.localToScreen(0, techButton.getHeight()).getY();
                    contextMenu.show(techButton, screenX, screenY);
                });
                programsTmList.add(tm);
            }
            tblProgram.setItems(programsTmList);

        }catch (ClassNotFoundException |SQLException e){
            new Alert(Alert.AlertType.ERROR,e.toString()).show();
        }

    }
    public void addNewProgramOnAction(ActionEvent event) {
        clear();
        setProgramCode();
        btnSaveProgram.setText("Save Program");
    }


    public void backToHomeOnAction(ActionEvent event) throws IOException {
        setUi("DashBoardForm");
    }


    public void saveProgramOnAction(ActionEvent event) {
        String[] selectedTechs=new String[tmList.size()];
        int pointer=0;
        for(TechAddTm t:tmList){
            selectedTechs[pointer]=t.getName();
            pointer++;
        }
        Program program=new Program(
                txtId.getText(),
                txtName.getText(),
                selectedTechs,
                cmdTeacher.getValue().split("\\:")[0],
                Double.parseDouble(txtCost.getText())
        );
        if(btnSaveProgram.getText().equals("Save Program")){
            try{
                if(saveProgram(program)){
                    clear();
                    setProgramCode();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Program saved").show();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }

            }catch (ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR,e.toString()).show();
            }


        }else{
            try{
                if(updateProgram(program)){
                    setProgramCode();
                    clear();
                    setTableData(searchText);
                    btnSaveProgram.setText("Save Program");
                    new Alert(Alert.AlertType.INFORMATION,"Program Updated").show();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e1){
                new Alert(Alert.AlertType.ERROR,e1.toString()).show();
            }
        }
    }
    public void setUi(String location) throws IOException {
        Stage stage=(Stage)context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
    ObservableList<TechAddTm> tmList=FXCollections.observableArrayList();
    public void addTechOnAction(ActionEvent actionEvent) {

        if(!isExists(txtTechnology.getText().trim())){
            Button btn =new Button("Remove");
            TechAddTm tm=new TechAddTm(
                    tmList.size()+1,txtTechnology.getText().trim(),btn
            );
            btn.setOnAction((e) -> {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> buttonType = conf.showAndWait();
                if (buttonType.get() == ButtonType.YES) {
                    TechAddTm techToRemove=tm;
                    tmList.remove(techToRemove);
                    new Alert(Alert.AlertType.INFORMATION,"Deleted.!").show();

                }
            });
            tmList.add(tm);
            tblTechnologies.setItems(tmList);
            txtTechnology.clear();
        }else{
            txtTechnology.selectAll();
            new Alert(Alert.AlertType.WARNING,"Already exists").show();
        }

    }
    public boolean isExists(String tech){
        for(TechAddTm t:tmList){
            if(t.getName().equalsIgnoreCase(tech)){
                return true;
            }
        }
        return false;
    }
    private void clear(){
        txtName.clear();
        txtCost.clear();
        cmdTeacher.setValue(null);
        tmList.clear();
        tblTechnologies.getItems().clear();
        setTeachers();
    }
    //=============================================
    public String getTechString(Program program){
        StringBuilder techStringBuilder = new StringBuilder();
        String[] technologies = program.getTechnologies();

        for (int i = 0; i < technologies.length; i++) {
            techStringBuilder.append(technologies[i]);
            if (i < technologies.length - 1) {
                techStringBuilder.append(",");
            }
        }

        return techStringBuilder.toString();
    }
    private boolean saveProgram(Program program) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");

        PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO program VALUES(?,?,?,?,?)");
        preparedStatement.setString(1, program.getCode());
        preparedStatement.setString(2, program.getName());
        preparedStatement.setDouble(3, program.getCost());
        preparedStatement.setString(4, program.getTeacherId());
        preparedStatement.setString(5, getTechString(program));
        return preparedStatement.executeUpdate()>0;
    }
    private String getLastCode() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");

        PreparedStatement preparedStatement=connection.prepareStatement("SELECT program_code FROM program ORDER BY CAST(SUBSTRING(program_code,3)AS UNSIGNED)DESC LIMIT 1");
        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next()){
            return resultSet.getString(1);
        }
        return  null;
    }

    public static String[] getTechArray(String technologies){
        String[] techArray= technologies.split(",");
        return techArray;
    }

    public static List<Program> searchProgram(String text) throws ClassNotFoundException, SQLException {
        text="%"+text+"%";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("SELECT * FROM  program WHERE name LIKE ?");
        statement.setString(1,text);
        ResultSet resultSet=statement.executeQuery();
        List<Program> list=new ArrayList<>();
        while(resultSet.next()){
            list.add(new Program(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    getTechArray(resultSet.getString(5)),
                    resultSet.getString(4),
                    resultSet.getDouble(3)

            ));
        }
        return list;
    }
    private boolean deleteProgram(String code) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        PreparedStatement statement=connection.prepareStatement("DELETE FROM program WHERE program_code=?");
        statement.setString(1,code);
        return statement.executeUpdate()>0;
    }
    private boolean updateProgram(Program program) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection=
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms?verifyServerCertificate=false&useSSL=true","root","1234");
        String sql="UPDATE program SET name=?,cost=?,teacher_code=?,technologies=? WHERE program_code=?";
        PreparedStatement statement=connection.prepareStatement(sql);
        statement.setString(1, program.getName());
        statement.setDouble(2,program.getCost());
        statement.setString(3, program.getTeacherId());
        statement.setString(4, getTechString(program));
        statement.setString(5, program.getCode());
        return statement.executeUpdate()>0;
    }

}
