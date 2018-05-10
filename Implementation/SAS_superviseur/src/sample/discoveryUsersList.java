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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

public class discoveryUsersList implements Initializable {

    private static FXMLLoader fxmlLoader;
    private static Stage stageMain;
    private final int port = 3000;

    @FXML private TableView<HostToDisplay> discoveryUsersList;
    @FXML private Button superviseButton;
    @FXML private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Host> hosts = null;
        try {
            hosts = getMachines(getIPandMask());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ObservableList<HostToDisplay> hostsToDisplay = FXCollections.observableArrayList(/*new HostToDisplay("ip", "mac", "name", "user")*/);
        for (Host host : hosts) {
            for (User user : host.users) {
                String u = "";
                if (user.fullname != "") {
                    u = "Nom complet : " + user.fullname;
                }
                if (user.name != "") {
                    u += "\nNom : " + user.name;
                }
                hostsToDisplay.add(new HostToDisplay(host.ip, host.mac, host.name, u));
            }
        };
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
    }

    @FXML
    public void supervise() {
        System.out.println("Supervise button clicked");
    }

    @FXML
    public void cancel() throws IOException {
        System.out.println("Cancel button clicked");
        this.stageMain = Main.stageMain;
        Parent root = fxmlLoader.load(getClass().getResource("supervisedUsersList.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Discover network users");
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
                    return new NetworkInfo(addr, networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());
                }
            }
        }
        return null;
    }

    private List<Host> getMachines(NetworkInfo networkInfo) throws Exception {
        String command = "nmap -sP " + networkInfo.ip + "/" + networkInfo.subnetMask;
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        /*String myString = "First line\none two three four internetbox.home\nThird line\none two ThisIsMyMacAddress\none two three four Nico-HP.home (160.98.126.85)\nLast line";
        InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8").encode(myString).array());

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));*/
        String line = ""; reader.readLine();
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