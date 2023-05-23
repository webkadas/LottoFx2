module com.lottohist.lottomasodik {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;


    opens lottomasodik to javafx.fxml;
    exports lottomasodik;
}