package lottomasodik;

import com.lottohist.model.Huzas;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HelloController {
    List<Huzas> huzasok = new ArrayList<>();
    @FXML
    private Label welcomeText;
    @FXML
    private Button startButton;

    @FXML
    private HBox menuHBox;
    @FXML
    protected void onHelloButtonClick() throws IOException, URISyntaxException {

        try {
            File myObj = new File("src/main/resources/otos.csv");
            Scanner myReader = new Scanner(myObj);
            huzasokTombbeToltese(myReader);
            myReader.close();
            startButton.setVisible(false);
            createMenu();

        } catch (FileNotFoundException e) {
            welcomeText.setText("Nem létező vagy hibás fájl");
            e.printStackTrace();
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
            System.out.println(huzas.toString());
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
        // create a menubar
        MenuBar mb = new MenuBar();
        mNumAppear.getItems().add(mNumAppearGeneral);
        mNumAppear.getItems().add(mNumAppearActual);
        mTippCheck.getItems().add(mTippCustom);
        mb.getMenus().add(mTippCheck);
        mb.getMenus().add(mNumAppear);
        menuHBox.getChildren().add(mb);
        menuHBox.setVisible(true);
    }
}