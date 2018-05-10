package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Main extends Application{

    private static FXMLLoader fxmlLoader;
    public static Stage stageMain;

    private final int port = 3000;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stageMain = stage;
        Parent root = fxmlLoader.load(getClass().getResource("supervisedUsersList.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Supervised users list");
        stageMain.setScene(scene);
        stageMain.show();
    }

    public void addUser() {

    }

    public void deleteUser() {

    }
}
