package sample;

import com.oracle.javafx.jmx.json.JSONException;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.*;

public class discoveryUsersList implements Initializable {

    private static FXMLLoader fxmlLoader;
    private static Stage stageMain;
    private final int port = 3000;
    private String password;
    private FileReader reader;
    private boolean fileIsEmpty;
    @FXML private TableView<HostToDisplay> discoveryUsersList;
    @FXML private Button superviseButton;
    @FXML private Button cancelButton;
    @FXML
    private BorderPane discoveryUsersBorderpane;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getSupervisedUserFile();
        fillTableHostsToDiscover();
    }

    private void getSupervisedUserFile(){
        try {
            reader = new FileReader(supervisedUsersList.SUPERVISEDUSERSFILE); // Get supervised users
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray users = (JSONArray) jsonObject.get("users");
            JSONObject userAttributes;
            Iterator i = users.iterator(); // Iterate the game objects
            final ObservableList<HostToDisplay> hostsToNotDisplay = FXCollections.observableArrayList();
            while (i.hasNext()) { // List contains supervised users
                userAttributes = (JSONObject) i.next();
                hostsToNotDisplay.add(new HostToDisplay(userAttributes.get("ip").toString(), userAttributes.get("mac").toString(),
                        userAttributes.get("name").toString(),userAttributes.get("user").toString()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier n'existe pas encore");
        } catch (IOException e) {
            System.out.println("Le fichier n'existe pas encore");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void fillTableHostsToDiscover(){
        List<Host> hosts = null;
       /* try {
                NetworkInfo n = getIPandMask();
                System.out.println(n.ip+" "+n.subnetMask);
                hosts = getMachines(n);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        final ObservableList<HostToDisplay> hostsToDisplay = FXCollections.observableArrayList(new HostToDisplay("160.98.127.214", "D0-7E-35-66-A9-54", "Nico-HP", "Nicolas"),
                new HostToDisplay("160.98.127.215", "D0-00-35-66-A9-54", "Greg-HP", "Grégory"));
        /*HostToDisplay h;
        final ObservableList<HostToDisplay> hostsToNotDisplay = supervisedUsersList.supervisedUserObservable;
        for (Host host : hosts) {
                    for (User user : host.users) {
                        String u = "";
                        if (user.fullname != "") {
                            u = "Nom complet : " + user.fullname;
                        }
                        if (user.name != "") {
                            u += "\nNom : " + user.name;
                        }
                        h = new HostToDisplay(host.ip, host.mac, host.name, u);
                        if(!hostsToNotDisplay.contains(h)){ // Display only not supervised hosts
                            hostsToDisplay.add(h);
                        }
                    }
            };*/
        TableColumn usernameCol = new TableColumn("Nom d'utilisateur");
        usernameCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("user"));
        TableColumn machineCol = new TableColumn("Nom de la machine");
        machineCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("name"));
        TableColumn ipCol = new TableColumn("Adresse IP");
        ipCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("ip"));
        TableColumn macCol = new TableColumn("Adresse MAC");
        macCol.setCellValueFactory(new PropertyValueFactory<HostToDisplay,String>("mac"));
        discoveryUsersList.setItems(hostsToDisplay);
        discoveryUsersList.getColumns().addAll(usernameCol, machineCol, ipCol, macCol);
        discoveryUsersList.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }
    @FXML
    public void supervise() {
        ObservableList<HostToDisplay>existingHostList = null;
        ObservableList<HostToDisplay>hostList;
        try {
            reader = new FileReader(supervisedUsersList.SUPERVISEDUSERSFILE); // Get supervised users
            existingHostList = FXCollections.observableArrayList();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray users = (JSONArray) jsonObject.get("users");
            JSONObject userAttributes;
            Iterator i = users.iterator(); // Iterate the game objects
            while (i.hasNext()) { // List contains supervised users
                userAttributes = (JSONObject) i.next();
                existingHostList.add(new HostToDisplay(userAttributes.get("ip").toString(), userAttributes.get("mac").toString(),
                        userAttributes.get("name").toString(),userAttributes.get("user").toString()));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Le fichier n'existe pas encore");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            hostList = discoveryUsersList.getSelectionModel().getSelectedItems();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject user;
            if(existingHostList!=null){
                for (int i=0; i<existingHostList.size();i++){
                    user = new JSONObject();
                    user.put("name", existingHostList.get(i).getName());
                    user.put("ip", existingHostList.get(i).getIp());
                    user.put("mac", existingHostList.get(i).getMac());
                    user.put("user",existingHostList.get(i).getUser());
                    jsonArray.add(user);
                    jsonObject.put("users",jsonArray);
                }
            }
            for (int i=0; i<hostList.size();i++){
                user = new JSONObject();
                user.put("name", hostList.get(i).getName());
                user.put("ip", hostList.get(i).getIp());
                user.put("mac", hostList.get(i).getMac());
                user.put("user",hostList.get(i).getUser());
                jsonArray.add(user);
                jsonObject.put("users",jsonArray);
            }
            discoveryUsersList.getItems().removeAll(hostList);
            FileWriter jsonWriter = new FileWriter(supervisedUsersList.SUPERVISEDUSERSFILE);
            jsonObject.writeJSONString(jsonWriter);
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel() throws IOException {
        System.out.println("Cancel button clicked");
        this.stageMain = Main.stageMain;
        Parent root = fxmlLoader.load(getClass().getResource("supervisedUsersList.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Décourvrir les utilisateurs du réseau");
        stageMain.show();
    }

    private NetworkInfo getIPandMask() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) continue;
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                String nin = networkInterface.getDisplayName();
                String addr = addresses.nextElement().getHostAddress();
                if (!nin.contains("Adapter") && !nin.contains("Virtual") && (addr.contains("160.98.") ||addr.contains("192.168.") || addr.contains("172.16.") || addr.contains("10."))) {
                    if (networkInterface.getInterfaceAddresses().size() == 1) return new NetworkInfo(addr, networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());
                    else return new NetworkInfo(addr, networkInterface.getInterfaceAddresses().get(3).getNetworkPrefixLength()); //OLD  new NetworkInfo(addr, networkInterface.getInterfaceAddresses().get(1).getNetworkPrefixLength());
                }
            }
        }
        return null;
    }
    private void displayPasswordDialog(){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Mot de passe administrateur");
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> password.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(password.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            this.password = usernamePassword.getValue();
        });
    }

    private List<Host> getMachines(NetworkInfo networkInfo) throws Exception {
        Process p = null;
        if(System.getProperty("os.name").equals("Mac OS X")) { // do a readline because there is an additionnal line with mac os
            displayPasswordDialog();
            String[] command = new String[] {"/bin/bash","-c","echo "+password+" | sudo -S nmap -sP " + networkInfo.ip + "/" + networkInfo.subnetMask};
            p = Runtime.getRuntime().exec(command);

        }else{
            String command = "nmap -sP " + networkInfo.ip + "/" + networkInfo.subnetMask;
            p = Runtime.getRuntime().exec(command);
        }
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        /*String myString = "First line\none two three four internetbox.home\nThird line\none two ThisIsMyMacAddress\none two three four Nico-HP.home (160.98.126.85)\nLast line";
        InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8").encode(myString).array());

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));*/
        String line = "";
        //if(System.getProperty("os.name").equals("Mac OS X")){ // do a readline because there is an additionnal line with mac os
            line = reader.readLine();
        //}
        line = reader.readLine();
        String[] splittedGateway = line.split("\\s+")[4].split("\\.");
        String suffix = "." + splittedGateway[splittedGateway.length-1];
        //System.out.println(suffix);
        List<Host> hosts = new ArrayList<Host>();
        reader.readLine();
        while ((line = reader.readLine())!= null) {
            String mac = line.split("\\s+")[2];
            //System.out.println(mac);
            String nameAndIP = reader.readLine();
            if (nameAndIP == null) break;
            String[] splittedNameAndIP = nameAndIP.split("\\s+");
            String name = splittedNameAndIP[4].replace(suffix, "");
            //System.out.println(name);
            String ip = splittedNameAndIP[5].substring(1, splittedNameAndIP[5].length()-1);
            //System.out.println(ip);
            System.out.println("http://" + ip + ":" + port + "/active");
            if (URLUtils.checkURLExists("http://" + ip + ":" + port + "/active")) {
                List<User> users = getUsers(ip);
                Host host = new Host(ip, mac, name, users);
                System.out.println(ip);
                System.out.println(mac);
                System.out.println(name);
                for (User user : users) {
                    System.out.println(user.fullname);
                    System.out.println(user.name);
                }
                System.out.println();
                hosts.add(host);
            }
            reader.readLine();
        }
        return hosts;
    }

    private List<User> getUsers(String ip) {
        return URLUtils.sendRequest("http://" + ip + ":" + port + "/supervise/users");
    }
}