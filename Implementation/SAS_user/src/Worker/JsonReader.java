package Worker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import Ihm.MyStat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader {
    private static final String filePathPie = "/Users/Gregory/Desktop/hes-so/3 eme/Projet de semestre/PS2/SAS/Implementation/SAS_user/jsonSamplePie";
    private static final String filePathBar = "/Users/Gregory/Desktop/hes-so/3 eme/Projet de semestre/PS2/SAS/Implementation/SAS_user/jsonSampleBar";
   // private ServerConnection serverConnection;
    public JsonReader(){
        //serverConnection = new ServerConnection("url");
    }

    private ArrayList<Sector> readByCategory(JSONArray category, String date, String categoryName){
        ArrayList<Sector>pieChartData = new ArrayList<Sector>();
        Sector s;
        // get games
        JSONObject categoryInnerObj;
        Iterator i = category.iterator(); // Iterate the game objects
        double time;
        while (i.hasNext()) {
            categoryInnerObj = (JSONObject) i.next();
            time = Double.parseDouble(categoryInnerObj.get("totalTime").toString());
            s = new Sector(categoryInnerObj.get("name").toString(),categoryName,time,date);
            pieChartData.add(s);
        }
        return pieChartData;
    }

    public ArrayList<Bar> readForBarChart(){
        ArrayList<Bar>infosBar = new ArrayList<Bar>();
        try {
            FileReader reader = new FileReader(filePathBar);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            int hour = 24;
            String date = (String) jsonObject.get("date");
            Bar barGames;
            Bar barNetworks;
            double gamesCumulateTime=0;
            double networksCumulateTime=0;
            for (int i = 0; i<hour; i++){
                barGames = new Bar("games",Double.parseDouble(((JSONObject)jsonObject.get(String.valueOf(i))).get("games").toString()),gamesCumulateTime,date, i);
                barNetworks = new Bar("networks",Double.parseDouble(((JSONObject)jsonObject.get(String.valueOf(i))).get("networks").toString()),networksCumulateTime,date,i);
                infosBar.add(barGames);
                infosBar.add(barNetworks);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return infosBar;
    }

    public ArrayList<Sector> readForPieChart(){
        ArrayList<Sector>pieChartData = new ArrayList<Sector>();
        ArrayList<Sector>infosNetworks;
        ArrayList<Sector>infosGames;
        try {
            // read the json file
            FileReader reader = new FileReader(filePathPie);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);


            // get the date from the JSON object
            String date = (String) jsonObject.get("date");
            JSONArray games = (JSONArray) jsonObject.get("games");
            infosGames = readByCategory(games, date, "games");
            JSONArray networks = (JSONArray) jsonObject.get("networks");
            infosNetworks=readByCategory(networks, date,"networks");
            for (int i = 0; i<infosGames.size();i++){
                pieChartData.add(infosGames.get(i));
            }
            for (int i = 0; i<infosNetworks.size();i++){
                pieChartData.add(infosNetworks.get(i));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return pieChartData;
    }
}
