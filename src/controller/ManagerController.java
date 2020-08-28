package controller;

import helper.DatabaseHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import object.MyMenu;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ManagerController {

    @FXML
    private TableView<MyMenu> menuTableView;

    @FXML
    private TableColumn<MyMenu, String> menuNameTableColumn;

    @FXML
    private TableColumn<MyMenu, Integer> menuIdTableColumn;

    @FXML
    private TableColumn<MyMenu, Double> menuPriceTableColumn;

    @FXML
    private Button removeMenuButton;

    @FXML
    private Button addMenuButton;

    @FXML
    private TextField addMenuNameTextField;

    @FXML
    private TextField addMenuPriceTextField;

    @FXML
    private Label addMenuTipsLabel;

    private MyMenu currentlySelectedMenu;

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            3,5,10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardOldestPolicy());


    @FXML
    public void initialize() {
        showMenuCenter();
        addMenuInputValidationCheck();
        checkTheMenuLineExecutionEvent();
    }

    private void checkTheMenuLineExecutionEvent() {
        menuTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyMenu>() {
            @Override
            public void changed(ObservableValue<? extends MyMenu> observableValue, MyMenu myMenu, MyMenu t1) {
                if (t1 != null) {
                    currentlySelectedMenu = t1;
                    removeMenuButton.setDisable(false);
                }
            }
        });
    }

    @FXML
    private void onMouseClickedAddMenuButton() {
        addMenuButton.setText("正在添加...");
        addMenuButton.setDisable(true);
        String name = addMenuNameTextField.getText();
        double price = Double.parseDouble(addMenuPriceTextField.getText());
        MyMenu myMenu = new MyMenu(0, name, price);
        /* 添加菜品 */
        Runnable addMenuRunnable = ()-> {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            boolean isOk = databaseHelper.addMyMenu(myMenu);
            databaseHelper.closeDatabase();
            Runnable runnable = () -> {
                String tips;
                if (isOk) {
                    tips = "添加成功";
                    showMenuCenter();
                }
                else {
                    tips = "添加失败";
                }
                addMenuButton.setText("添加菜品");
                addMenuButton.setDisable(false);
                addMenuTipsLabel.setText(tips);
            };
            Platform.runLater(runnable);
        };
        threadPool.execute(addMenuRunnable);
     }

    private void showMenuCenter() {
        menuIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        menuNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ObservableList<MyMenu> myMenuObservableList = FXCollections.observableArrayList();
        Runnable getAllMenusRunnable = new Runnable() {
            @Override
            public void run() {
                DatabaseHelper databaseHelper = new DatabaseHelper();
                databaseHelper.connectToDatabase();
                List<MyMenu> myMenuArrayList = databaseHelper.getAllMyMenus();
                databaseHelper.closeDatabase();
                myMenuObservableList.addAll(myMenuArrayList);
                menuTableView.setItems(myMenuObservableList);
            }
        };
        threadPool.execute(getAllMenusRunnable);
    }

    @FXML
    public void onMouseClickedRemoveMenuButton() {
        removeMenuButton.setText("正在删除...");
        removeMenuButton.setDisable(true);
        Runnable removeMenuRunnable = () -> {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            boolean isOK = databaseHelper.remove(currentlySelectedMenu);
            databaseHelper.closeDatabase();
            Runnable runnable = () -> {
                removeMenuButton.setText("删除");
                removeMenuButton.setDisable(false);
                if (isOK) {
                    showMenuCenter();
                    removeMenuButton.setDisable(true);
                }
                else {
                    //TODO
                }
            };
            Platform.runLater(runnable);
        };
        threadPool.execute(removeMenuRunnable);
    }

    private void addMenuInputValidationCheck() {
        addMenuNameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                addMenuButton.setDisable(!isValidInputAddMenu());
            }
        });
        addMenuPriceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                addMenuButton.setDisable(!isValidInputAddMenu());
            }
        });
    }

    private boolean isValidInputAddMenu() {
        String name = addMenuNameTextField.getText();
        String price = addMenuPriceTextField.getText();
        if ("".equals(name) || name.length() > 10) {
            return false;
        }
        if ("".equals(price) || price.length() > 10) {
            return false;
        }
        int theNumberOfPoint = 0;
        for (int i = 0; i < price.length(); i++) {
            if (price.charAt(i) > '9' || price.charAt(i) < '0') {
                if (price.charAt(i) == '.' && theNumberOfPoint == 0) {
                    theNumberOfPoint++;
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }
}

