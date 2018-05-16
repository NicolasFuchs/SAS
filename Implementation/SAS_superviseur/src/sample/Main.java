package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Main extends Application{

    private static FXMLLoader fxmlLoader;
    public static Stage stageMain;
    public static final int port = 3000;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stageMain = stage;
        Parent root = fxmlLoader.load(getClass().getResource("supervisedUsersList.fxml"));
        // Vérifier si le Json qui contient les utilisateurs existe
        // Si c'est le cas, afficher les utilisateurs supervisés
        // Sinon, créer un Json vide.
        // Permettre le clic sur un item et setter les variable static superviseurIP et supervisedUsername
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
