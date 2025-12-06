module com.personalexpensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.swing;

    opens com.personalexpensetracker.controller to javafx.fxml;
    opens com.personalexpensetracker.model to javafx.base;
    opens com.personalexpensetracker to javafx.fxml;

    exports com.personalexpensetracker;
    exports com.personalexpensetracker.controller;
}
