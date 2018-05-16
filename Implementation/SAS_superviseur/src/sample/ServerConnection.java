package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServerConnection {
    private String targetUrl;
    private URL url;
    private HttpURLConnection connection;

   /* public ServerConnection(String targetUrl) {
        this.targetUrl = targetUrl;
        createConnectionForPie(targetUrl);

    }*/

    public ServerConnection() {


    }

    public String getUserInfo(String targetURL){
        connection = null;
        String userInfos="";
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            userInfos = br.readLine();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfos;
    }
}
