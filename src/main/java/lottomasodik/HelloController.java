package lottomasodik;

import com.lottohist.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import org.jdbi.v3.core.Jdbi;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

public class HelloController {
    FinalResult fr;
    List<Huzas> huzasok = new ArrayList<>(); // EZ TARTALMAZZA AZ ÖSSZES SORSOLAST
    List<AnchorPane> anchorPaneList = new ArrayList<>();
    Popup popup = new Popup();
    Integer actualWeek;
    @FXML
    private VBox resultVbox, menuVBox;
    @FXML
    ListView gyakorisagListView,gyakorisagActualListView;
    @FXML
    private Label welcomeText, suggestLabel;
    @FXML
    private Button startButton;

    @FXML
    ComboBox orderCombo,orderComboActual;
    @FXML
    private AnchorPane menuAnchor, actualAnchor, tippAnchor, statAnchor, mainAnchor;

    @FXML
    private TextField textNum1,textNum2,textNum3,textNum4,textNum5;
    @FXML
    private RadioButton radio1Button, radio2Button, radio3Button, radio4Button, radio5Button;
    @FXML
    protected void onStartButtonClick() throws IOException, URISyntaxException {
        Calendar cal = Calendar.getInstance();
        DataBase db = new DataBase();
        db.createDb();

        actualWeek = cal.get(Calendar.WEEK_OF_YEAR);


        try {
            File myObj = new File("src/main/resources/otos.csv");
            Scanner myReader = new Scanner(myObj);
            huzasokTombbeToltese(myReader);  // SORSOLÁSOK BETÖLTÉSE a huzasok LISTÁBA
            myReader.close();
            startButton.setVisible(false); // START BUTTON KIKAPCSOLÁS
            anchorPaneList.add(tippAnchor);anchorPaneList.add(statAnchor);anchorPaneList.add(mainAnchor);anchorPaneList.add(actualAnchor);
            activatePane("menuAnchor");
            createMenu(); // MENÜ KÉSZÍTÉS

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
    private void resultFromRadio(){

    }

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

    public void createMenu(){
        Menu mTippCheck = new Menu("Tipp ellenőrzése");
        Menu mNumAppear = new Menu("Számok gyakorisága..");

        MenuItem mNumAppearGeneral = new MenuItem("Általános");
        MenuItem mNumAppearActual = new MenuItem("Aktuális hét");

        MenuItem mTippCustom = new MenuItem("Saját / Random tippek");
        mTippCustom.setOnAction(event);
        mNumAppearGeneral.setOnAction(event);
        mNumAppearActual.setOnAction(event);

    // create a menubar
        MenuBar mb = new MenuBar();
        mNumAppear.getItems().add(mNumAppearGeneral);
        mNumAppear.getItems().add(mNumAppearActual);
        mTippCheck.getItems().add(mTippCustom);
        mb.getMenus().add(mTippCheck);
        mb.getMenus().add(mNumAppear);
        menuVBox.getChildren().add(mb);
        menuVBox.setVisible(true);
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
        } else if (node.getId().equals("radio2Button")){
            eredmenyKiir(fr.getTalalatok2());
        } else if (node.getId().equals("radio3Button")){
            eredmenyKiir(fr.getTalalatok3());
        } else if (node.getId().equals("radio4Button")){
            eredmenyKiir(fr.getTalalatok4());
        } else {
            eredmenyKiir(fr.getTalalatok5());
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

    @FXML
    private void getSelectedOrder(){

    }


    @FXML
    protected void getSelectedOrder(ActionEvent event){  // ÁLTALÁNOS COMBOBOX EVENTEK

        if (orderCombo.getSelectionModel().getSelectedIndex()==0){
            gyakorisagListView.getItems().clear();
            Analyzer gyakorisag = new Analyzer(huzasok);
            for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByKey()){
                gyakorisagListView.getItems().add(x);
            }
        }
        if (orderCombo.getSelectionModel().getSelectedIndex()==1){
            gyakorisagListView.getItems().clear();
            Analyzer gyakorisag = new Analyzer(huzasok);
            for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByKeyRev()){
                gyakorisagListView.getItems().add(x);
            }
        }
        if (orderCombo.getSelectionModel().getSelectedIndex()==2){
            gyakorisagListView.getItems().clear();
            Analyzer gyakorisag = new Analyzer(huzasok);
            for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByValue()){
                gyakorisagListView.getItems().add(x);
            }
        }
        if (orderCombo.getSelectionModel().getSelectedIndex()==3){
            gyakorisagListView.getItems().clear();
            Analyzer gyakorisag = new Analyzer(huzasok);
            for (Map.Entry<Integer, Integer> x:gyakorisag.sortedListByValueRev()){
                gyakorisagListView.getItems().add(x);
            }
        }
    }

    @FXML
    protected void getSelectedOrderActual(ActionEvent event){

        List<Huzas> actualHuzasok = new ArrayList<>();
        if (orderComboActual.getSelectionModel().getSelectedIndex()==0){
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
        if (orderComboActual.getSelectionModel().getSelectedIndex()==1){
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
        if (orderComboActual.getSelectionModel().getSelectedIndex()==2){
            gyakorisagActualListView.getItems().clear();

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

    }

    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {

            if (((MenuItem) e.getSource()).getText().equals("Saját / Random tippek")) activatePane("tippAnchor");
            if (((MenuItem) e.getSource()).getText().equals("Általános")) {activatePane("statAnchor");orderCombo.getItems().add("szám növekvő");
                orderCombo.getItems().add("szám csökkenő");
                orderCombo.getItems().add("előfodulás alapján növekvő");
                orderCombo.getItems().add("előfodulás alapján csökkenő");}
            if (((MenuItem) e.getSource()).getText().equals("Aktuális hét")) {activatePane("actualAnchor");orderComboActual.getItems().add("szám szerint növekvő");
                orderComboActual.getItems().add("gyakoriság szerint növekvő");
                orderComboActual.getItems().add("gyakoriság szerint csökkenő");}


        }
    };
}