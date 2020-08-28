package controller;


import helper.DatabaseHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RegisterController {

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private TextField registerUserTextField;

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            3,5,10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @FXML
    public void initialize() {

       inputCheck();
    }

    @FXML
    void onMouseClickedRegisterButton() {
        /*获取注册信息*/
        String username = registerUserTextField.getText();
        String password = registerPasswordField.getText();

        registerButton.setDisable(true);
        registerButton.setText("正在注册...");
        /*进程*/
        Runnable registerRunnable = () -> {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            boolean isOk = databaseHelper.registerUsername(username, password);
            databaseHelper.closeDatabase();
            Runnable runnable = () -> {
                registerButton.setText("注册");
                if (isOk) {
                    HBox hBox = new HBox();
                    Label label = new Label("注册成功");
                    hBox.getChildren().add(label);
                    Scene scene = new Scene(hBox, 50, 50);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.show();
                    registerUserTextField.setText("");
                    registerPasswordField.setText("");
                }
                else {
                    HBox hBox = new HBox();
                    registerButton.setDisable(false);
                    Label label = new Label("注册失败");
                    hBox.getChildren().add(label);
                    Scene scene = new Scene(hBox, 50, 50);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.show();
                }
            };
            Platform.runLater(runnable);
        };
        threadPool.execute(registerRunnable);

    }
    private void inputCheck() {
        registerUserTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                registerButton.setDisable(!isValidInput());
            }
        });
        registerPasswordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                registerButton.setDisable(!isValidInput());

            }
        });
    }

    private boolean isValidInput() {
        String username = registerUserTextField.getText();
        String password = registerPasswordField.getText();
        if ("".equals(username) || username.length() > 10) {
            return false;
        }
        if ("".equals(password) || password.length() > 10) {
            return false;
        }
        return true;
    }
}