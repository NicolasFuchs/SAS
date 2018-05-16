package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class supervisedUsersList implements Initializable {

    private static FXMLLoader fxmlLoader;
    private static Stage stageMain;
    public static final String SUPERVISEDUSERSFILE = "supervisedUsers";
    public static HostToDisplay hostToSend;
    public static ObservableList supervisedUserObservable;
    @FXML
    public TableView<HostToDisplay> supervisedUsersList= new TableView<>();
    @FXML
    private Button plusButton;
    @FXML
    private Button minusButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populateSupervisedUserList();
        supervisedUsersList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                hostToSend = supervisedUsersList.getSelectionModel().getSelectedItem();
                System.out.println(hostToSend.getName());
                this.stageMain = Main.stageMain;
                Parent root = null;
                try {
                    root = fxmlLoader.load(getClass().getResource("UserStat.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root);
                stageMain.setScene(scene);
                stageMain.setTitle("Statistiques de l'utilisateur");
                stageMain.centerOnScreen();
                stageMain.show();

            }
        });
    }

    private void populateSupervisedUserList(){
        try{
            FileReader reader = new FileReader(this.SUPERVISEDUSERSFILE);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            // get the date from the JSON object
            JSONArray users = (JSONArray) jsonObject.get("users");
            JSONObject userAttributes;
            Iterator i = users.iterator(); // Iterate the game objects
            double time;
            final ObservableList<HostToDisplay> hostsToDisplay = FXCollections.observableArrayList();
            TableColumn usernameCol = new TableColumn("Nom d'utilisateur");
            TableColumn machineCol = new TableColumn("Nom de la machine");
            TableColumn ipCol = new TableColumn("Adresse IP");
            TableColumn macCol = new TableColumn("Adresse MAC");
            while (i.hasNext()) { // Affiche les utilisateurs dans la tableview
                userAttributes = (JSONObject) i.next();
                hostsToDisplay.add(new HostToDisplay(userAttributes.get("ip").toString(), userAttributes.get("mac").toString(),
                        userAttributes.get("name").toString(),userAttributes.get("user").toString()));
                usernameCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("user"));
                machineCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("name"));
                ipCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("ip"));
                macCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("mac"));
            }

            supervisedUsersList.setItems(hostsToDisplay);
            supervisedUsersList.getColumns().addAll(usernameCol, machineCol, ipCol, macCol);
            this.supervisedUserObservable = supervisedUsersList.getItems();
        }catch (Exception e){
            System.out.println("Create a new file ");
        }
    }

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
        try {
            ObservableList<HostToDisplay>existingHostList;
            existingHostList = supervisedUsersList.getItems();
            if(existingHostList!=null && !existingHostList.isEmpty()){
                HostToDisplay host = supervisedUsersList.getSelectionModel().getSelectedItem();// Get host to remove
                supervisedUsersList.getItems().remove(host);
                if(!supervisedUsersList.getItems().isEmpty()){
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject user;
                    for (int i=0; i<existingHostList.size();i++){
                        user = new JSONObject();
                        user.put("name", existingHostList.get(i).getName());
                        user.put("ip", existingHostList.get(i).getIp());
                        user.put("mac", existingHostList.get(i).getMac());
                        user.put("user",existingHostList.get(i).getUser());
                        jsonArray.add(user);
                        jsonObject.put("users",jsonArray);
                    }
                    FileWriter jsonWriter = new FileWriter(SUPERVISEDUSERSFILE);
                    jsonObject.writeJSONString(jsonWriter);
                    jsonWriter.close();
                }else{
                    File reader = new File(SUPERVISEDUSERSFILE);
                    reader.delete();
                }
            }
            supervisedUsersList.refresh();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
