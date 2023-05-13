module com.lottohist.lottomasodik {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens lottomasodik to javafx.fxml;
    exports lottomasodik;
}