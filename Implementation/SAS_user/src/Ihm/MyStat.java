package Ihm;
import Worker.JsonReader;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Worker.Sector;
import Worker.Bar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MyStat implements Initializable{

    // Link the graphics elements with the fxml
    @FXML
    private PieChart dailyPieChart;
    @FXML
    private Label dateLabel;
    @FXML
    private Label cumuledTimeLabel;
    @FXML
    private TextArea userNoteTextArea;

    private BarChart dailyBarChart;
    @FXML
    private CheckBox displayGamesCheckbox;
    @FXML
    private CheckBox displayNetworksCheckbox;
    @FXML
    private CheckBox displayCumulateTimeCheckbox;
    @FXML
    private BorderPane tabBorderPane;
    @FXML
    private Pane centerPane;
    @FXML
    private ToggleButton activityToggleButton;
    @FXML
    private ToggleButton categoryToggleButton;
    @FXML
    private ImageView usersImageView;
    ToggleGroup group;
    // These 3 variables indicates if the graphics are displayed
    private boolean gamesDisplayed;
    private boolean networkDisplayed;
    double networkTimes = 0;
    double gamesTimes = 0;
    private boolean cumulateTimeDisplayed;
    //The variables contains the values of each series for the barchart
    private XYChart.Series<String,Number> series1;
    XYChart.Series<String,Number> series2;
    //Allow to read the json file with users informations
    private JsonReader jsonReader;

    // Represent the cumulates times
    private double cumulateNetworkTime;
    private double cumulateGamesTime;

    // Contains users infos to display
    ArrayList<Bar>infosBarChart;
    ArrayList<Sector> infosForPieChart;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        CategoryAxis xAxis    = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0,60,5);
        dailyBarChart = new BarChart(xAxis, yAxis);
        dailyBarChart.setBarGap(0.5);
        dailyBarChart.setCategoryGap(5.0);
       // tabBorderPane.setRight(dailyBarChart);
        jsonReader = new JsonReader();
        infosBarChart = jsonReader.readForBarChart();
        infosForPieChart = jsonReader.readForPieChart();
        File file = new File("userProfile.JPG");
        Image image = new Image(file.toURI().toString());
        if(!infosBarChart.isEmpty()&&infosBarChart!=null){ // NEW
            dateLabel.setText("Date: "+infosBarChart.get(0).getDate()); // Get days date
        }
        centerPane.getChildren().addAll(dailyBarChart);
        userNoteTextArea.setText("Les notes concernant l'utilisateur");
        usersImageView.setImage(image);
        group = new ToggleGroup();
        activityToggleButton.setToggleGroup(group);
        activityToggleButton.setSelected(true);
        categoryToggleButton.setToggleGroup(group);
        handleSelectedCheckBox();
        handleSelectedToggle();
        dailyPieChart.setStartAngle(90); // NEW
        dailyPieChart.setClockwise(true); // NEW
    }
    @FXML
    public void handleSelectedToggle() {
        boolean categoryToggle = categoryToggleButton.isSelected();
        boolean activityToggle = activityToggleButton.isSelected();
        if(categoryToggle){
            displayCumulateTimePieChart();
        }else if(activityToggle){
            displayPieChart();
        }
    }

    private void displayCumulateTimePieChart() {

        Sector sector;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i<infosForPieChart.size();i++){
            sector = infosForPieChart.get(i);
            if(sector.getCategory().equals("games")){
                gamesTimes+=sector.getTime();
            }else{
                networkTimes+=sector.getTime();
            }
        }
        pieChartData.add(new PieChart.Data("Réseaux sociaux",networkTimes));
        pieChartData.add(new PieChart.Data("Jeux vidéos",gamesTimes));
        dailyPieChart.setData(pieChartData);
        setTooltip(pieChartData, 1);
    }

    private void setTooltip(ObservableList pieChartData, int cat){ // NEW
        if(cat == 2){
            for (PieChart.Data data : dailyPieChart.getData()) {
                int idSecteur = getSectorIdByName(data.getName());
                if(idSecteur!=-1){
                    data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                            new EventHandler<MouseEvent>() {
                                @Override public void handle(MouseEvent e) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Information du secteur");
                                    alert.setHeaderText("Information du secteur");
                                    double total =(networkTimes+gamesTimes)/60;
                                    double time = (infosForPieChart.get(idSecteur).getTime()/60);
                                    double pourcentage = ((time/total)*100);
                                    new DecimalFormat("#.##").format(pourcentage);
                                    alert.setContentText("Temps passé sur "+data.getName()+" "+(int)time+" minutes\n"
                                            +"Le temps passé correspond à "+pourcentage+"% du temps total qui est "+((int)(total))+" minutes");
                                    alert.showAndWait();
                                }
                            });
                }
            }
            dailyPieChart.setData(pieChartData);
            dailyPieChart.getData().stream().forEach(data -> {
                Tooltip tooltip = new Tooltip();
                tooltip.setText(data.getName()+" "+data.getPieValue());
                Tooltip.install(data.getNode(), tooltip);
            });
        }
    }
    private int getSectorIdByName(String name){
        for(int i = 0;i<infosForPieChart.size();i++){
            if(infosForPieChart.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }


    public void displayPieChart(){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i<infosForPieChart.size();i++){
            pieChartData.add(new PieChart.Data(infosForPieChart.get(i).getName(),infosForPieChart.get(i).getTime()));
        }
        //dailyPieChart.setData(pieChartData); A VOIR SIL FAUT GARDER
    }

    public void displayGamesBarChart(ArrayList<Bar>infosBarChart){
        series1 = new XYChart.Series();
        series1.setName("Jeux vidéos");
        double time;
        for(int i=0; i<infosBarChart.size();i++){
            if(infosBarChart.get(i).getCategory().equals("games")){
                time = (infosBarChart.get(i).getTime()/60);
                series1.getData().add(new XYChart.Data(String.valueOf(infosBarChart.get(i).getHour()), time));
                cumulateGamesTime += time;
            }
        }
        gamesDisplayed = true;
        dailyBarChart.getData().addAll(series1);
    }

    public void displayNetworksBarChart(ArrayList<Bar>infosBarChart){
        series2 = new XYChart.Series();
        series2.setName("Réseaux sociaux");
        double time;
        for(int i=0; i<infosBarChart.size();i++){
            if(infosBarChart.get(i).getCategory().equals("networks")){
                time = (infosBarChart.get(i).getTime()/60);
                series2.getData().add(new XYChart.Data(String.valueOf(infosBarChart.get(i).getHour()), time));
                cumulateNetworkTime += time;
            }
        }
        networkDisplayed = true;
        dailyBarChart.getData().addAll(series2);
    }

    public void displayCumulateTimeBarChart(ArrayList<Bar>infosBarChart){
        displayCumulateTimeCheckbox.setSelected(true);
        if (gamesDisplayed && networkDisplayed) {
            cumuledTimeLabel.setText("Temps cumulé sur les jeux vidéos: " + (int) cumulateGamesTime + " minutes\n" + "Temps cumulé sur les réseaux sociaux: " + (int) cumulateNetworkTime + " minutes");
        } else if (gamesDisplayed || networkDisplayed) {
            if (gamesDisplayed) {
                cumuledTimeLabel.setText("Temps cumulé sur les jeux vidéos: " + (int) cumulateGamesTime + " minutes");
            } else {
                cumuledTimeLabel.setText("Temps cumulé sur les réseaux sociaux: " + (int) cumulateNetworkTime + " minutes");
            }
        }
        cumuledTimeLabel.setWrapText(true);
        cumulateTimeDisplayed = true;
    }
    public void hiddeGamesBarChart(){
        if(displayCumulateTimeCheckbox.isSelected()){
            displayCumulateTimeCheckbox.setSelected(false);
            cumuledTimeLabel.setText("");
        }
        dailyBarChart.getData().removeAll(series1);
        gamesDisplayed = false;
    }

    public void hiddeNetworksBarChart(){
        if(displayCumulateTimeCheckbox.isSelected()){
            displayCumulateTimeCheckbox.setSelected(false);
            cumuledTimeLabel.setText("");
        }
        dailyBarChart.getData().removeAll(series2);
        networkDisplayed = false;
    }

    public void hiddeCumulateTime(){
        cumuledTimeLabel.setText("");
        cumulateTimeDisplayed = false;
    }


    @FXML
    public void handleSelectedCheckBox() {
        boolean isGamesSelected = displayGamesCheckbox.isSelected();
        boolean isNetworksSelected = displayNetworksCheckbox.isSelected();
        boolean isCumulateTimeSelected = displayCumulateTimeCheckbox.isSelected();

        if(isGamesSelected&&!gamesDisplayed){
            displayGamesBarChart(infosBarChart);
        }else if(!isGamesSelected){
            hiddeGamesBarChart();
        }
        if(isNetworksSelected&&!networkDisplayed){
            displayNetworksBarChart(infosBarChart);
        }else if(!isNetworksSelected){
            hiddeNetworksBarChart();
        }
        if(isCumulateTimeSelected&&!cumulateTimeDisplayed){
            displayCumulateTimeBarChart(infosBarChart);
        }else if(!isCumulateTimeSelected){
            hiddeCumulateTime();
        }
    }
}
