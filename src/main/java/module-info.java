// File: src/main/java/module-info.java
module com.example.giantprojekt {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // UI-библиотеки
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    // Apache POI для Excel (только poi-ooxml, poi API включено внутри)
    requires org.apache.poi.ooxml;

    // JSON
    requires com.google.gson;

    // JDA (Discord)
    requires net.dv8tion.jda; // соответствует артефакту JDA :contentReference[oaicite:1]{index=1}

    // Открываем главный пакет (если у вас в FXML используется классы из корня модуля)
    opens com.example.giantprojekt to javafx.fxml;
    // Открываем пакеты с контроллерами и утилитами для FXMLLoader
    opens com.example.giantprojekt.ui.controller to javafx.fxml;
    opens com.example.giantprojekt.ui.util       to javafx.fxml;
    // Открываем модели для PropertyValueFactory (JavaFX TableView)
    opens com.example.giantprojekt.model         to javafx.base;

    // Экспортируем корневой пакет, если вы запускаете main из него
    exports com.example.giantprojekt;
}
