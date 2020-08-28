package controller;


import helper.DatabaseHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import object.MyMenu;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserController {

    public Button payButton;
    @FXML
    private TableView<MyMenu> menuTableView;

    @FXML
    private TableColumn<MyMenu, String> menuIdTableColumn;

    @FXML
    private TableColumn<MyMenu, String> menuNameTableColumn;

    @FXML
    private TableColumn<MyMenu, Double> menuPriceTableColumn;

    @FXML
    private Button addChartButton;

    @FXML
    private Button removeButton;

    @FXML
    private TableView<MyMenu> shopTableView;

    @FXML
    private TableColumn<MyMenu, Integer> shopIdTableColumn;

    @FXML
    private TableColumn<MyMenu, String> shopNameTableColumn;

    @FXML
    private TableColumn<MyMenu, Integer> shopCountTableColumn;


    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            3,5,10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private MyMenu myMenuNow;
    private MyMenu myShopNow;

    @FXML
    public void initialize() {
        showMenuCenter();
        showShopTableView();
        monitorSelectedDishes();

    }

    private void showShopTableView() {
        shopIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        shopNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        shopCountTableColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        ObservableList<MyMenu> shopObservableList = FXCollections.observableArrayList();
        shopTableView.setItems(shopObservableList);
    }

    private void showMenuCenter(){
        menuIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        menuNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ObservableList<MyMenu> myMenuObservableList = FXCollections.observableArrayList();
        Runnable getMenuRunnable = () -> {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            List<MyMenu> myMenuList = databaseHelper.getAllMyMenus();
            databaseHelper.closeDatabase();
            Runnable updateUiRunnable = () -> {
                myMenuObservableList.addAll(myMenuList);
                menuTableView.setItems(myMenuObservableList);
            };
            Platform.runLater(updateUiRunnable);
        };
        threadPool.execute(getMenuRunnable);
    }

    @FXML
    private void onMouseClickedAddChartButton() {
        /* 获取选中的菜品 */
        boolean isExistMenu = false;
        for (MyMenu myMenu : shopTableView.getItems()) {
            if (myMenu.getId() == myMenuNow.getId()) {
                /* 表中已有该菜品 */
                isExistMenu = true;
                myMenu.addCount();
                break;
            }
        }
        /* 表中没有该菜品 */
        if (!isExistMenu) {
            myMenuNow.setCount(1);
            shopTableView.getItems().add(myMenuNow);
        }
        shopTableView.refresh();
        if (shopTableView.getItems().size() != 0) {
            payButton.setDisable(false);
        }
    }

    private void monitorSelectedDishes() {
        menuTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyMenu>() {
            @Override
            public void changed(ObservableValue<? extends MyMenu> observableValue, MyMenu myMenu, MyMenu t1) {
                myMenuNow = t1;
                if (t1 != null) {
                    addChartButton.setDisable(false);
                }
            }
        });
        shopTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyMenu>() {
            @Override
            public void changed(ObservableValue<? extends MyMenu> observableValue, MyMenu myMenu, MyMenu t1) {
                myShopNow = t1;
                if (t1 != null) {
                    removeButton.setDisable(false);
                }
            }
        });
    }

    @FXML
    void onMouseClickedRemoveButton() {
        for (MyMenu myMenu : shopTableView.getItems()) {
            if (myMenu.getId() == myShopNow.getId()) {
               shopTableView.getItems().remove(myMenu);
               break;
            }
        }
        shopTableView.refresh();
        if (shopTableView.getItems().size() == 0) {
            removeButton.setDisable(true);
            payButton.setDisable(true);
        }
    }


    @FXML
    private void onMouseClickedPayButton() {
        double price = 0;
        for (MyMenu myMenu : shopTableView.getItems()) {
            price += myMenu.getPrice() * myMenu.getCount();
        }
        shopTableView.getItems().clear();
        removeButton.setDisable(true);
        payButton.setDisable(true);
        HBox hBox = new HBox();
        Label label = new Label("结算成功：" + price + "元");
        hBox.getChildren().add(label);
        Scene scene = new Scene(hBox, 50, 50);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
