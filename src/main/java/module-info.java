module com.example.studentsmanaged {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j.simple;


    opens com.example.studentsmanaged to javafx.fxml;
    opens com.example.studentsmanaged.controllers to javafx.fxml;
    opens com.example.studentsmanaged.models to javafx.base;

    exports com.example.studentsmanaged;
    exports com.example.studentsmanaged.controllers to javafx.fxml;

    //uses com.example.studentsmanaged.service.StudentService;
}