package test;

import java.io.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SniffHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        System.out.println(line);

        // send response
        String response = "OK";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

}
