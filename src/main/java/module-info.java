module org.headroyce.smartcolor.smartcolor {
    requires javafx.controls;
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.swing;


    opens org.headroyce.smartcolor.smartcolor to javafx.fxml;
    exports org.headroyce.smartcolor.smartcolor;
}