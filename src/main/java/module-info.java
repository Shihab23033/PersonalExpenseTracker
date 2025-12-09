module com.personalexpensetracker {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;
    requires javafx.web;
    requires javafx.swing;

    opens com.personalexpensetracker.controller to javafx.fxml;
    opens com.personalexpensetracker.model to javafx.base;
    opens com.personalexpensetracker to javafx.fxml;

    exports com.personalexpensetracker;
    exports com.personalexpensetracker.controller;
    exports com.personalexpensetracker.model;
    exports com.personalexpensetracker.service;
    exports com.personalexpensetracker.util;
}
