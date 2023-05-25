package lottomasodik;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lottohist.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
public class HelloController {
    FinalResult fr;
    List<Huzas> huzasok = new ArrayList<>(); // EZ TARTALMAZZA AZ ÖSSZES SORSOLAST
    List<AnchorPane> anchorPaneList = new ArrayList<>(); // lista a Pane-ek kapcsolgatásához
    Popup popup = new Popup();
    Integer actualWeek;
    ObservableList<String> custom_names = FXCollections.observableArrayList(); // Lista a comboboxba töltéshez
    List<SaveData> savedata = new ArrayList<>();
    ComboBox comboSaveData = new ComboBox();
    @FXML
    private VBox resultVbox, menuVBox;

    @FXML
    ListView gyakorisagListView,gyakorisagActualListView;
    @FXML
    private Label welcomeText, talalatDarabLabel, actualWeekLabel;
    @FXML
    private Button startButton, sajatTippButton, altSzamNovButton;

    @FXML
    private AnchorPane menuAnchor, actualAnchor, tippAnchor, statAnchor, mainAnchor, comboPane;

    @FXML
    private TextField textNum1,textNum2,textNum3,textNum4,textNum5,tippNameText;
    @FXML
    private RadioButton radio1Button, radio2Button, radio3Button, radio4Button, radio5Button;

    @FXML
    protected void saveTipp() throws IOException {

       SaveData newSd = new SaveData(tippNameText.getText(),Arrays.asList(Integer.parseInt(textNum1.getText()),Integer.parseInt(textNum2.getText()),Integer.parseInt(textNum3.getText()),Integer.parseInt(textNum4.getText()),Integer.parseInt(textNum5.getText())));
        if (newSd.nameLengthCheck() && newSd.numbersEqualityCheck() && newSd.numbersRangeCheck() && newSd.numbersFormatCheck()) {

            savedata.add(newSd);
            var objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            //var sd = new SaveData("kattra", Arrays.asList(10,22,33,51,90));

            try (var writer = new FileWriter("movie.json")) {
                objectMapper.writeValue(writer, savedata);
                comboSaveData.getItems().add(newSd.getName());
                popup.setLabel("Adatok mentve","Siker");
                popup.display();
            }
        } else { popup.setLabel("Legalább 3 betű a név, valamint 1-90ig különböző számok","Formátum hiba"); popup.display();}  // MENTÉSKORI HIBA




        /*Torol torol = new Torol();
        torol.setName("Valami");
        torol.setAge(12);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(torol);
            System.out.println("ResultingJSONstring = " + json);
            //System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

    }
    @FXML

    protected void onStartButtonClick() throws IOException, URISyntaxException {
        Calendar cal = Calendar.getInstance();
        actualWeek = cal.get(Calendar.WEEK_OF_YEAR);
        actualWeekLabel.setText("Aktuális hét: "+actualWeek);
        generateFakeTips();

        comboSaveData.setOnAction(event);

        System.out.println("Sajat tipp méret: "+savedata.size());
        try {
            File myObj = new File("src/main/resources/otos.csv");
            Scanner myReader = new Scanner(myObj);
            huzasokTombbeToltese(myReader);  // SORSOLÁSOK BETÖLTÉSE a huzasok LISTÁBA
            myReader.close();
            startButton.setVisible(false); // START BUTTON KIKAPCSOLÁS
            anchorPaneList.add(tippAnchor);anchorPaneList.add(statAnchor);anchorPaneList.add(mainAnchor);anchorPaneList.add(actualAnchor);

            menuAnchor.setVisible(true);


        } catch (FileNotFoundException e) {
            welcomeText.setText("Nem létező vagy hibás fájl");
            e.printStackTrace();
        }


    }


    @FXML private void randomNumber(){  // A RANDOM TIPPEKET GENERÁLJA
        radio1Button.setVisible(false);  // ELTŰNTETEM AZ ELŐZŐ EREDMÉNYEKET
        radio2Button.setVisible(false);
        radio3Button.setVisible(false);
        radio4Button.setVisible(false);
        radio5Button.setVisible(false);

        resultVbox.getChildren().clear();
        int nums[] = new int[5];
        int db=0;
        boolean exists;
        Random rng = new Random();
        while (db<5){
            exists = false;
            int rngTipp = rng.nextInt(90)+1;
            if (db==0) {
                nums[0] = rngTipp;
                db+=1;
            } else
            {
                for (int i = 0; i < db; i++) {
                    if (nums[i]==rngTipp){
                        exists = true;
                        break;
                    }
                }
                if (!exists){
                    nums[db] = rngTipp;
                    db+=1;
                }
            }

        }
        textNum1.setText(nums[0]+"");textNum2.setText(nums[1]+"");textNum3.setText(nums[2]+"");textNum4.setText(nums[3]+"");textNum5.setText(nums[4]+"");
    } // RANDOM TIPPEKET GENERÁLJA



    @FXML
    private void onResultButtonClick(){
        radio1Button.setVisible(false);  // ELTŰNTETEM AZ ELŐZŐ EREDMÉNYEKET
        radio2Button.setVisible(false);
        radio3Button.setVisible(false);
        radio4Button.setVisible(false);
        radio5Button.setVisible(false);

        resultVbox.getChildren().clear();
        radio1Button.setSelected(false);
        radio2Button.setSelected(false);
        radio3Button.setSelected(false);
        radio4Button.setSelected(false);
        radio5Button.setSelected(false);


        Set<Integer> tippek = new HashSet<>();
        if (isNumberCheck(textNum1.getText())) tippek.add(Integer.parseInt(textNum1.getText()));
        if (isNumberCheck(textNum2.getText())) tippek.add(Integer.parseInt(textNum2.getText()));
        if (isNumberCheck(textNum3.getText())) tippek.add(Integer.parseInt(textNum3.getText()));
        if (isNumberCheck(textNum4.getText())) tippek.add(Integer.parseInt(textNum4.getText()));
        if (isNumberCheck(textNum5.getText())) tippek.add(Integer.parseInt(textNum5.getText()));

        if (tippek.size()==5) {
            List<Integer> tippek2 = new ArrayList<>(tippek);
            // resultVbox.getChildren().removeAll();
            fr = new FinalResult();
            int talalat = 0;
            for (Huzas x : huzasok) {

                talalat = 0;
                for (Integer y : x.getNumbers()) {
                    for (int i = 0; i < tippek.size(); i++) {
                        if (y == tippek2.get(i)) {
                            talalat++;
                            break;
                        }
                    }
                }

                if (talalat == 0) fr.getTalalatok0().add(x);
                else if (talalat == 1) fr.getTalalatok1().add(x);
                else if (talalat == 2) fr.getTalalatok2().add(x);
                else if (talalat == 3) fr.getTalalatok3().add(x);
                else if (talalat == 4) fr.getTalalatok4().add(x);
                else fr.getTalalatok5().add(x);

            }

            visibleRadio(fr);
            System.out.println("Ötösök: "+fr.getTalalatok5().size());
            System.out.println("Négyesek: "+fr.getTalalatok4().size());
            System.out.println("Hármasok: "+fr.getTalalatok3().size());
            System.out.println("Kettesek: "+fr.getTalalatok2().size());
            System.out.println("Egyesek: "+fr.getTalalatok1().size());
            System.out.println("Nullások: "+fr.getTalalatok0().size());
        } else {
            popup.setLabel("Csak klönböző számokkal tippelhetsz","Azonos számok!");
            popup.display();
        }
    }


    public void huzasokTombbeToltese(Scanner myReader){
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            Huzas huzas = new Huzas();
            String[] darabok = data.split(";");
            huzas.setYear(Integer.parseInt(darabok[0])); // ÉV
            huzas.setWeek(Integer.parseInt(darabok[1])); // HÉT

            if (darabok[2].length()>0) { // Dátum
                String tep = darabok[2].substring(0,darabok[2].length()-1).replace(".","-");
                LocalDate date1 = LocalDate.parse(tep);
                huzas.setDate(date1);
            } else huzas.setDate(null);

            huzas.setTalalat(4, Integer.parseInt(darabok[3]));
            huzas.setNyeremeny(4, darabok[4]);

            huzas.setTalalat(3, Integer.parseInt(darabok[5]));
            huzas.setNyeremeny(3, darabok[6]);

            huzas.setTalalat(2, Integer.parseInt(darabok[7]));
            huzas.setNyeremeny(2, darabok[8]);

            huzas.setTalalat(1, Integer.parseInt(darabok[9]));
            huzas.setNyeremeny(1, darabok[10]);

            huzas.setNumbers(0,Integer.parseInt(darabok[11]));
            huzas.setNumbers(1,Integer.parseInt(darabok[12]));
            huzas.setNumbers(2,Integer.parseInt(darabok[13]));
            huzas.setNumbers(3,Integer.parseInt(darabok[14]));
            huzas.setNumbers(4,Integer.parseInt(darabok[15]));

            huzasok.add(huzas);
        }
        welcomeText.setText("Az adatbázis "+huzasok.size()+" sorsolást tartalmaz");
        myReader.close();


    }


    public Boolean isNumberCheck(String tipp){
        boolean result = true;
        Integer szam=0;
        try {
            szam = Integer.parseInt(tipp);
        } catch (IllegalFormatException e){
            popup.setLabel("Karakterhiba","HIBA");
            result = false;
        };

        if (szam<1 || szam>90) result = false;
        System.out.println(result);

        return result;
    }
    void visibleRadio(FinalResult fr){
        if (fr.getTalalatok1().size()>0) radio1Button.setVisible(true);
        if (fr.getTalalatok2().size()>0) radio2Button.setVisible(true);
        if (fr.getTalalatok3().size()>0) radio3Button.setVisible(true);
        if (fr.getTalalatok4().size()>0) radio4Button.setVisible(true);
        if (fr.getTalalatok5().size()>0) radio5Button.setVisible(true);
    }  // HA VAN TALÁLAT, A MEGFELELŐ RADIÓ MEGJELENIK

    void activatePane(String pane){
        for (AnchorPane x : anchorPaneList) if (x.getId().equals(pane)) x.setVisible(true); else x.setVisible(false);
    }

    @FXML
    protected void resultFromRadio(ActionEvent e){
        Node node = (Node) e.getSource();
        if (node.getId().equals("radio1Button")){
            eredmenyKiir(fr.getTalalatok1());
            talalatDarabLabel.setText("Találatok száma: "+fr.getTalalatok1().size());
        } else if (node.getId().equals("radio2Button")){
            eredmenyKiir(fr.getTalalatok2());
            talalatDarabLabel.setText("Találatok száma: "+fr.getTalalatok2().size());
        } else if (node.getId().equals("radio3Button")){
            eredmenyKiir(fr.getTalalatok3());
            talalatDarabLabel.setText("Találatok száma: "+fr.getTalalatok3().size());
        } else if (node.getId().equals("radio4Button")){
            eredmenyKiir(fr.getTalalatok4());
            talalatDarabLabel.setText("Találatok száma: "+fr.getTalalatok4().size());
        } else {
            eredmenyKiir(fr.getTalalatok5());
            talalatDarabLabel.setText("Találatok száma: "+fr.getTalalatok5().size());

        }
    }
    private void eredmenyKiir(List<Huzas> talalatok) {
        resultVbox.getChildren().clear();

        System.out.println("Darabszám: "+talalatok.size());
        for (Huzas x:talalatok){
            TextField textDate = new TextField(x.getYear()+" - "+x.getWeek()+". hét:    "+x.numbersKiir());
            textDate.setEditable(false);
            resultVbox.getChildren().add(textDate);
        }
    }
    public void generateFakeTips() throws IOException {

        try {
            // create object mapper instance
            ObjectMapper mapper = new ObjectMapper();

            // convert a JSON string to a Book object
            savedata = mapper.readValue(Paths.get("movie.json").toFile(), new TypeReference<List<SaveData>>(){});

            // print book
            savedata.forEach(System.out::println );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (savedata.size()==0) {
            savedata.add(new SaveData("szülinapok", Arrays.asList(10, 22, 33, 51, 90)));
            savedata.add(new SaveData("szerencse", Arrays.asList(12, 24, 13, 81, 88)));
            var objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            //var sd = new SaveData("kattra", Arrays.asList(10,22,33,51,90));


            try (var writer = new FileWriter("movie.json")) {
                objectMapper.writeValue(writer, savedata);
            }


        }
    }
    @FXML
    private void getSelectedOrder(){

    }






    @FXML
    protected void sajatTippButton(){
        activatePane("tippAnchor");
        comboPane.getChildren().removeAll();
        comboSaveData.getItems().removeAll();
        ComboboxCreate();


    }

   // ÁLTALÁNOS STATISZTIKA GOMB ESEMÉNYEK
    @FXML
    protected void altSzamNov(){
        activatePane("statAnchor");
        gyakorisagListView.getItems().clear();
        Analyzer gyakorisag = new Analyzer(huzasok);
        for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByKey()){
            gyakorisagListView.getItems().add(x);
        }
    }
    @FXML
    protected void altSzamCsokk(){
        activatePane("statAnchor");
        gyakorisagListView.getItems().clear();
        Analyzer gyakorisag = new Analyzer(huzasok);
        for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByKeyRev()){
            gyakorisagListView.getItems().add(x);
        }
    }
    @FXML
    protected void altElofNov(){
        activatePane("statAnchor");
        gyakorisagListView.getItems().clear();
        Analyzer gyakorisag = new Analyzer(huzasok);
        for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByValue()){
            gyakorisagListView.getItems().add(x);
        }
    }
    @FXML
    protected void altElofCsokk(){
        activatePane("statAnchor");
        gyakorisagListView.getItems().clear();
        Analyzer gyakorisag = new Analyzer(huzasok);
        for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByValueRev()){
            gyakorisagListView.getItems().add(x);
        }
    }

    // ÁLTALÁNOS STATISZTIKA GOMB ESEMÉNYEK VÉGE

    // AKTUÁLIS HETI STATISZTIKA GOMB ESEMÉNYEK
    @FXML
    protected void actSzamNov(){
        activatePane("actualAnchor");
        List<Huzas> actualHuzasok = new ArrayList<>();
     //   actualWeekSuggestion(actualHuzasok);
        gyakorisagActualListView.getItems().clear();

        for (Huzas x:huzasok){  // az aktuális hét húzásait külön LIST-be teszi
            if (x.getWeek()==actualWeek) {
                actualHuzasok.add(x);
            }

        }

        Analyzer gyakorisag = new Analyzer(actualHuzasok);
        for (Map.Entry<Integer, Integer> y:gyakorisag.sortedListByKey()){
            gyakorisagActualListView.getItems().add("#"+y.getKey()+" - " + y.getValue()+" db.");
        }
    }
    @FXML
    protected void actSzamCsokk(){
        activatePane("actualAnchor");
        List<Huzas> actualHuzasok = new ArrayList<>();
        gyakorisagActualListView.getItems().clear();

        for (Huzas x:huzasok){  // az aktuális hét húzásait külön LIST-be teszi
            if (x.getWeek()==actualWeek) {
                actualHuzasok.add(x);
            }

        }

        Analyzer gyakorisag = new Analyzer(actualHuzasok);
        for (Map.Entry<Integer, Integer> y:gyakorisag.sortedListByKeyRev()){
            gyakorisagActualListView.getItems().add("#"+y.getKey()+" - " + y.getValue()+" db.");
        }
    }
    @FXML
    protected void actElofNov(){
        activatePane("statAnchor");activatePane("actualAnchor");
        List<Huzas> actualHuzasok = new ArrayList<>();

        gyakorisagActualListView.getItems().clear();

        for (Huzas x:huzasok){  // az aktuális hét húzásait külön LIST-be teszi
            if (x.getWeek()==actualWeek) {
                actualHuzasok.add(x);
            }

        }
        Analyzer gyakorisag = new Analyzer(actualHuzasok);
        for (Map.Entry<Integer, Integer> y:gyakorisag.sortedListByValue()){
            gyakorisagActualListView.getItems().add("#"+y.getKey()+" - " + y.getValue()+" db.");
        }
    }
    @FXML
    protected void actElofCsokk(){
        activatePane("actualAnchor");
        gyakorisagActualListView.getItems().clear();
        List<Huzas> actualHuzasok = new ArrayList<>();
        for (Huzas x:huzasok){  // az aktuális hét húzásait külön LIST-be teszi
            if (x.getWeek()==actualWeek) {
                actualHuzasok.add(x);
            }

        }
        Analyzer gyakorisag = new Analyzer(actualHuzasok);
        for (Map.Entry<Integer, Integer> y:gyakorisag.sortedListByValueRev()){
            gyakorisagActualListView.getItems().add("#"+y.getKey()+" - " + y.getValue()+" db.");
        }
    }

    // ÁLTALÁNOS STATISZTIKA GOMB ESEMÉNYEK VÉGE

    /*
    public void actualWeekSuggestion(List<Huzas> actualHuzasok){
        Analyzer gyakorisag = new Analyzer(actualHuzasok);
        List<Integer> fiveRandomTipp = new ArrayList<>();
        int gyakFigyelo=0, i=-1;


        while (gyakFigyelo<5) {
            i++;
            if (i == 0) fiveRandomTipp.add(gyakorisag.sortedListByValueRev().get(i).getKey());
            else {
                if (gyakorisag.sortedListByValueRev().get(i).getValue() != gyakorisag.sortedListByValueRev().get(i - 1).getValue()) gyakFigyelo += 1;

            }
            if (gyakFigyelo<5) fiveRandomTipp.add(gyakorisag.sortedListByValueRev().get(i).getKey());

        }

        suggestLabel.setText("Javaslat a hétre: "+suggestForWeek(fiveRandomTipp));
    }*/
    /*private String suggestForWeek(List<Integer> numbers){ // NEM MŰKÖDIK
        String result="";
        Random rand = new Random();
        int rng = 0;
        for (int i = 0; i < 5; i++) {
            rng = rand.nextInt(numbers.size()-1);
            result += String.valueOf(numbers.get(rng))+" ";
            numbers.remove(rng);
        }


        return result;
    }*/
    public void ComboboxCreate(){
        comboPane.getChildren().removeAll();
        comboSaveData.getItems().removeAll();
        for (SaveData x : savedata) custom_names.add(x.getName());
        comboSaveData.setItems(custom_names);
        //comboSaveData.setItems((ObservableList<SaveData>) savedata);
        comboPane.getChildren().add(comboSaveData);
    }

    EventHandler<ActionEvent> event =
            new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {

                    for(SaveData x: savedata){
                        if (x.getName().equals(comboSaveData.getValue())){
                            textNum1.setText(x.getNumbers().get(0)+"");
                            textNum2.setText(x.getNumbers().get(1)+"");
                            textNum3.setText(x.getNumbers().get(2)+"");
                            textNum4.setText(x.getNumbers().get(3)+"");
                            textNum5.setText(x.getNumbers().get(4)+"");
                            break;
                        }
                    }
                }
            };
}