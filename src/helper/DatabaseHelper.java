package helper;

import object.MyMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    public DatabaseHelper() {

    }

    /** 数据库连接配置信息 */
    private static final String HOSTNAME = "127.0.0.1";
    private static final String PORT = "3306";
    private static final String DATABASE = "personal_ordering_system";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" +
            DATABASE + "?serverTimezone=GMT%2B8&useSSL=false";
    private Connection connection = null;

    /** 连接到数据库 */
    public void connectToDatabase() {
        try {
            /* 设置连接超时为2秒 */
            DriverManager.setLoginTimeout(2);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** 断开与数据库的连接 */
    public void closeDatabase() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**增加菜品*/
    public boolean addMyMenu(MyMenu myMenu) {
        String insetMyMenuSql = "INSERT INTO `menu` (`menu_name`, `menu_price`) VALUES ('"
                + myMenu.getName() + "', '" + myMenu.getPrice() + "')";
        try {
            Statement statement = connection.createStatement();
            statement.execute(insetMyMenuSql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**展示列表*/
    public List<MyMenu> getAllMyMenus() {
        List<MyMenu> myMenuList = new ArrayList<>();
        String getAllMyMenusSql = "select * from menu";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getAllMyMenusSql);
            if (resultSet.first()) {
                do {
                    MyMenu myMenu = new MyMenu(resultSet.getInt("menu_id"),
                            resultSet.getString("menu_name"),
                            resultSet.getDouble("menu_price"));
                    myMenuList.add(myMenu);
                } while (resultSet.next());
            }
            else {
                /* empty table */
                System.out.println("empty table - menu");
            }
            resultSet.first();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return myMenuList;
    }
    /**删除菜品*/
    public boolean remove(MyMenu myMenu) {
        try {
            String removeMenuSql = "DELETE FROM `menu` WHERE  `menu_id`=" + myMenu.getId();
            Statement statement = connection.createStatement();
            statement.execute(removeMenuSql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**注册一个用户名*/
    public boolean registerUsername(String username,String password){
        try{
            String isExitTheSameUsernameSql = "select * from user where username = '" + username + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isExitTheSameUsernameSql);
            if (!resultSet.first()) {
                String registerSql = "INSERT INTO `user` (`username`, `password`) VALUES ('"+ username +"', '"+ password +"');";
                statement.execute(registerSql);
                return true;
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**登录一个用户*/
    public boolean isValidUsernamePassword(String username, String password) {
        try{
            String isValidUserSql = "SELECT * FROM user WHERE username ='"+ username +"' AND password ='"+ password + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isValidUserSql);
            if (resultSet.first()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**登录管理员*/
    public boolean isValidManager(String username, String password){
        try{
            String isValidUserSql = "SELECT * FROM manager WHERE username ='"+ username +"' AND password ='"+ password + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(isValidUserSql);
            if (resultSet.first()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
