package sample;

import java.net.NetworkInterface;

public class NetworkInfo {

    public String ip;
    public int subnetMask;

    NetworkInfo(String ip, int sm) {
        this.ip = ip;
        this.subnetMask = sm;
    }

}
