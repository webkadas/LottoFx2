package com.lottohist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaveDataTest {

    SaveData saveData;

    @BeforeEach
    void setUp() {
         saveData = new SaveData();
    }

    @Test
    void nameLengthCheck() {

        assertEquals(true,saveData.nameLengthCheck("saveDataTest"));
    }

    @Test
    void numbersEqualityCheck() {
        assertEquals(true,saveData.numbersEqualityCheck(Arrays.asList(1,2,3,4,5)));
    }

    @Test
    void numbersRangeCheck() {
        assertEquals(true,saveData.numbersRangeCheck(Arrays.asList(1,2,3,4,5)));
    }

    @Test
    void numbersFormatCheck() {
        assertEquals(true,saveData.numbersFormatCheck(Arrays.asList(1,2,3,4,5)));
    }
}