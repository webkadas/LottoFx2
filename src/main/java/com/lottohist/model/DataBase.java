package com.lottohist.model;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Handles;
import org.jdbi.v3.core.Jdbi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test");



    public void createDb(){

        try (var handle = jdbi.open()) {
            handle.execute("""
                    CREATE TABLE custom_tips (
                        numbers VARCHAR NOT NULL,
                        "year" INTEGER NOT NULL,
                        "week" INTEGER NOT NULL
                        
                    )
                    """
            );
            handle.execute("INSERT INTO custom_tips VALUES ('610-132031', 2015, 21)");
            handle.execute("INSERT INTO custom_tips VALUES ('610132031', 2015, 21)");
            handle.execute("INSERT INTO custom_tips VALUES ('610132031', 2015, 21)");
            var numOfLegoSets = handle.createQuery("SELECT COUNT(*) FROM custom_tips").mapTo(Integer.class).one();
            System.out.println("Number of LEGO sets: {}"+numOfLegoSets);
            //var totalPieces = handle.createQuery("SELECT count(year) FROM custom_tips").mapTo(Integer.class).one();
            //System.out.println("Total number of LEGO pieces: {}"+totalPieces);
        }
    }

    public void connectDb(){



    }

}
