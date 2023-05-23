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
    public boolean nameLengthCheck(String name){
        if (name.length()<3) return false; else return true;
    }

    public boolean numbersEqualityCheck(List<Integer> numbers){

        for (int i = 0; i < numbers.size(); i++) if (i>0) for (int j = 0; j <i; j++)  if (numbers.get(i)==numbers.get(j)) return false;

        return true;
    }

    public boolean numbersRangeCheck(List<Integer> numbers){
        for (int i = 0; i < numbers.size(); i++) if (numbers.get(i)>90 || numbers.get(i)<1) return false;
        return true;
    }

    public boolean numbersFormatCheck(List<Integer> numbers){
        for (int i = 0; i < numbers.size(); i++) if (numbers.get(i).getClass()!=Integer.class) return false;
        return true;
    }



}
