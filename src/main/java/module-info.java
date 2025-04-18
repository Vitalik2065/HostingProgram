module com.example.giantprojekt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.google.gson;

    opens com.example.giantprojekt to javafx.fxml;
    exports com.example.giantprojekt;
}