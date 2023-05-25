package com.lottohist.model;


import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class SaveData {

    private String name;

    private List<Integer> numbers = new ArrayList<>();
    public SaveData(){};
    public SaveData(String name, List<Integer> numbers){
        this.name = name;
        this.numbers = numbers;
    }
    public boolean nameLengthCheck(){
        if (this.name.length()<3) return false; else return true;
    }

    public boolean numbersEqualityCheck(){

        for (int i = 0; i < this.numbers.size(); i++) if (i>0) for (int j = 0; j <i; j++)  if (this.numbers.get(i)==this.numbers.get(j)) return false;

        return true;
    }

    public boolean numbersRangeCheck(){
        for (int i = 0; i < this.numbers.size(); i++) if (this.numbers.get(i)>90 || this.numbers.get(i)<1) return false;
        return true;
    }

    public boolean numbersFormatCheck(){
        for (int i = 0; i < this.numbers.size(); i++) if (this.numbers.get(i).getClass()!=Integer.class) {
            Popup popup = new Popup();
            popup.setLabel("Csak számjegyek (1-90)","Formátum hiba");
            popup.display();
            return false;
        }
        return true;
    }



}
