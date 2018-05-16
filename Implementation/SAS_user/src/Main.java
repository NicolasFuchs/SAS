import Worker.JsonReader;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class Main extends Application{
    private FXMLLoader fxmlLoader;
    public static int port=3000;
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
    private static Stage stageMain;
    @FXML
    public void handleButtonAction(ActionEvent actionEvent) {
        try {
           /* fxmlLoader = new FXMLLoader(getClass().getResource("Ihm/MyStat.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();*/
            Parent root = (Parent) fxmlLoader.load(getClass().getResource("Ihm/MyStat.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stageMain.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stageMain = stage;
        Parent root = fxmlLoader.load(getClass().getResource("Ihm/Login.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Login");
        stageMain.setScene(scene);
        stageMain.show();
    }
}
