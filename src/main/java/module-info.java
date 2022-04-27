module org.headroyce.smartcolor.smartcolor {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.headroyce.smartcolor.smartcolor to javafx.fxml;
    exports org.headroyce.smartcolor.smartcolor;
}