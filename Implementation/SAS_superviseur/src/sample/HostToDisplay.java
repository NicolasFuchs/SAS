package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HostToDisplay {

    public SimpleStringProperty ip;
    public SimpleStringProperty mac;
    public SimpleStringProperty name;
    public SimpleStringProperty user;

    HostToDisplay(String ip, String mac, String name, String user) {
        this.ip = new SimpleStringProperty(ip);
        this.mac = new SimpleStringProperty(mac);
        this.name = new SimpleStringProperty(name);
        this.user = new SimpleStringProperty(user);
    }

    public String getIp() {
        return ip.get();
    }
    public StringProperty ipProperty() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public String getMac() {
        return mac.get();
    }
    public StringProperty macProperty() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac.set(mac);
    }

    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public String getUser() {
        return user.get();
    }
    public StringProperty userProperty() {
        return user;
    }
    public void setUser(String user) {
        this.user.set(user);
    }

}
