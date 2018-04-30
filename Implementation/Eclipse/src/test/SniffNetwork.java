package test;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Http.Request;
import org.jnetpcap.protocol.tcpip.Tcp;

public class SniffNetwork {

    public static void main (String[] args) {
        
        List<PcapIf> alldevs = new ArrayList<PcapIf>();     // List of all capture interfaces
        StringBuilder errbuff = new StringBuilder();        // Used for error messages
        
        int statusDevs = Pcap.findAllDevs(alldevs, errbuff); 
        if (statusDevs == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.println("Network devices list issue");
            return;
        }
        
        for (PcapIf device : alldevs) {
            System.out.println("Name : " + device.getName());
            System.out.println("Description : " + device.getDescription() + "\n");
        }
        
        //PcapIf device = alldevs.get(1); //NO
        PcapIf device = alldevs.get(2); //YES
        //PcapIf device = alldevs.get(3); //NO
        //PcapIf device = alldevs.get(4); //YES

        
        int snaplen = 64 * 1024;            // Capture all packets, no truncation
        int flags = Pcap.MODE_PROMISCUOUS;  // Capture all packets
        int timeout = 10 * 1000;            // 10 seconds in milliseconds
        
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuff);  // Open the device for sniffing
        if (pcap == null) {
            System.err.println("Opening sniffing device issue : " + errbuff);
            return;
        }
        
        PcapPacketHandler<String> packethandler = new PcapPacketHandler<String>() {
            private final Tcp tcp = new Tcp();
            private final Http http = new Http();
            @Override
            public void nextPacket(PcapPacket packet, String user) {
                if (!packet.hasHeader(tcp) || !packet.hasHeader(http) || http.isResponse()) return; //ISSUE -> should be able to capture https packets
                String host = http.fieldValue(Request.Host);
                if (host != null) System.out.println("Host : " + host);
            }
        };
        
        pcap.loop(-1, packethandler, "jNetPcap rocks!");
        pcap.close();
        
    }
    
}
