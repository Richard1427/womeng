package controller;

import helper.DatabaseHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoginController {

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Button loginButton;

    @FXML
    private RadioButton managerRadioButton;

    @FXML
    private ToggleGroup roleToggleGroup;

    @FXML
    private RadioButton userRadioButton;

    @FXML
    private TextField usernameTextField;

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            3,5,10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @FXML
    public void initialize() {
        inputCheck();
    }

    private boolean isValidInput() {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();
        if ("".equals(username) || username.length() > 10) {
            return false;
        }
        if ("".equals(password) || password.length() > 10) {
            return false;
        }
        return true;
    }


    @FXML
    void onMouseClickedRegisterButton() {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/register.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("注册");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onMouseClickedLoginButton() {
        RadioButton radioButton = (RadioButton) roleToggleGroup.getSelectedToggle();
        String string = radioButton.getText();
        if (string.equals("用户")) {
            Stage stage = new Stage();
            /*获取用户信息*/
            String username = usernameTextField.getText();
            String password = passwordPasswordField.getText();

            Runnable loginRunnable = () -> {
                DatabaseHelper databaseHelper = new DatabaseHelper();
                databaseHelper.connectToDatabase();
                boolean isOk = databaseHelper.isValidUsernamePassword(username, password);
                databaseHelper.closeDatabase();
                Runnable runnable = () -> {
                    if (isOk) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/user.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load());
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.setTitle("登录");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        HBox hBox = new HBox();
                        Label label = new Label("登录失败");
                        hBox.getChildren().add(label);
                        Scene scene = new Scene(hBox, 50, 50);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setResizable(false);
                        stage.show();
                    }
                };
                Platform.runLater(runnable);
            };
            threadPool.execute(loginRunnable);
        }
        else if (string.equals("管理员")) {
            Stage stage = new Stage();
            /*获取用户信息*/
            String username = usernameTextField.getText();
            String password = passwordPasswordField.getText();

            Runnable loginRunnable = () -> {
                DatabaseHelper databaseHelper = new DatabaseHelper();
                databaseHelper.connectToDatabase();
                boolean isCorrect = databaseHelper.isValidManager(username, password);
                databaseHelper.closeDatabase();
                Runnable runnable = () -> {
                    if (isCorrect){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/manager.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load());
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.setTitle("登录");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        HBox hBox = new HBox();
                        Label label = new Label("登录失败");
                        hBox.getChildren().add(label);
                        Scene scene = new Scene(hBox, 50, 50);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setResizable(false);
                        stage.show();
                    }
                };
                Platform.runLater(runnable);
            };
            threadPool.execute(loginRunnable);
        }

    }
    private void inputCheck() {
        usernameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                loginButton.setDisable(!isValidInput());
            }
        });
        passwordPasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                loginButton.setDisable(!isValidInput());

            }
        });
    }


}