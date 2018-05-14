package sample;

import java.util.List;

public class Host {

    public String ip;
    public String mac;
    public String name;
    public List<User> users;

    Host(String ip, String mac, String name, List<User> users) {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
        this.users = users;
    }

}
