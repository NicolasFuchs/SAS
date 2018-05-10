package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class supervisedUsersList {

    private static FXMLLoader fxmlLoader;
    private static Stage stageMain;
    private final int port = 3000;

    @FXML private Button plusButton;
    @FXML private Button minusButton;

    @FXML
    public void addUser() throws IOException {
        System.out.println("plus button clicked");
        this.stageMain = Main.stageMain;
        Parent root = fxmlLoader.load(getClass().getResource("discoveryUsersList.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Discover network users");
        stageMain.show();
    }

    @FXML
    public void deleteUser() {
        System.out.println("minus button clicked");
    }

}
