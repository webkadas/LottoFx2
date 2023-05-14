module com.lottohist.lottomasodik {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires org.jdbi.v3.core;


    opens lottomasodik to javafx.fxml;
    exports lottomasodik;
}