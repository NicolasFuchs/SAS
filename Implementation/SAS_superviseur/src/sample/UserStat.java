package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserStat implements Initializable {
    @FXML
    private PieChart dailyPieChart;
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
    @FXML
    private ImageView backImageView;
    @FXML
    private Button goBackButton;
    @FXML
    private Button editButton;
    @FXML
    private Label userActivityLabel;
    private String supervisedUserIP;
    private static Stage stageMain;
    private static FXMLLoader fxmlLoader;
    ToggleGroup group;
    // These 3 variables indicates if the graphics are displayed
    private boolean gamesDisplayed;
    private boolean networkDisplayed;
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
    ArrayList<Bar> infosBarChart;
    ArrayList<Sector> infosForPieChart;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("/Users/Gregory/Desktop/hes-so/3 eme/Projet de semestre/PS2/SAS/Implementation/SAS_superviseur/userProfile.JPG");
        Image image = new Image(file.toURI().toString());
        usersImageView.setImage(image);
        file = new File("/Users/Gregory/Desktop/hes-so/3 eme/Projet de semestre/PS2/SAS/Implementation/SAS_superviseur/retour.png");
        image = new Image(file.toURI().toString());
        backImageView.setImage(image);
        userNoteTextArea.setEditable(false);
        userActivityLabel.setText("Activités de "+Main.supervisedUserName);
        CategoryAxis xAxis    = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0,60,5);
        dailyBarChart = new BarChart(xAxis, yAxis);
        dailyBarChart.setBarGap(0.5);
        dailyBarChart.setCategoryGap(5.0);
        jsonReader = new JsonReader();
        infosBarChart = jsonReader.readForBarChart();
        infosForPieChart = jsonReader.readForPieChart();
        centerPane.getChildren().addAll(dailyBarChart);
        group = new ToggleGroup();
        activityToggleButton.setToggleGroup(group);
        activityToggleButton.setSelected(true);
        categoryToggleButton.setToggleGroup(group);
        handleSelectedCheckBox();
        handleSelectedToggle();
     }
    @FXML
     private void goBack(){
        this.stageMain = Main.stageMain;
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("supervisedUsersList.fxml"));
        Scene scene = new Scene(root);
        stageMain.setScene(scene);
        stageMain.setTitle("Supervised users list");
        stageMain.setScene(scene);
        stageMain.show();
        } catch (IOException e) {
        e.printStackTrace();
    }
     }
     @FXML
     private void edit(){
        if(editButton.getText().equals("Editer")){
            userNoteTextArea.setEditable(true);
            editButton.setText("Enregistrer");
        }else if(editButton.getText().equals("Enregistrer")) {
            userNoteTextArea.setEditable(true);
            String text = userNoteTextArea.getText();
            editButton.setText("Editer");
            userNoteTextArea.setEditable(false);
            System.out.println(text);
        }
     }

    @FXML
    public void handleSelectedToggle() {
        boolean categoryToggle = categoryToggleButton.isSelected();
        boolean activityToggle = activityToggleButton.isSelected();
        System.out.println("TOOGLLE");
        if(categoryToggle){
            displayCumulateTimePieChart();
        }else if(activityToggle){
            displayPieChart();
        }
    }

    private void displayCumulateTimePieChart() {
        double networkTimes = 0;
        double gamesTimes = 0;
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
    }

    public void displayPieChart(){
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i<infosForPieChart.size();i++){
            pieChartData.add(new PieChart.Data(infosForPieChart.get(i).getName(),infosForPieChart.get(i).getTime()));
        }
        dailyPieChart.setData(pieChartData);
    }

    public void displayGamesBarChart(ArrayList<Bar>infosBarChart){
        series1 = new XYChart.Series();
        series1.setName("Games");
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
        series2.setName("Networks");
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
        System.out.println("Games cumuled "+cumulateGamesTime);
        System.out.println("Networks cumuled "+cumulateNetworkTime);
        cumulateTimeDisplayed = true;
    }

    public void hiddeGamesBarChart(){
        dailyBarChart.getData().removeAll(series1);
        gamesDisplayed = false;
    }

    public void hiddeNetworksBarChart(){
        dailyBarChart.getData().removeAll(series2);
        networkDisplayed = false;
    }

    public void hiddeCumulateTime(){
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
