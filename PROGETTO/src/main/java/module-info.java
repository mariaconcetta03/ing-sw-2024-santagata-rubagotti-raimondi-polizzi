module CODEX {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.rmi;
    requires java.sql;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires json.simple;
    requires com.ctc.wstx;
    requires javafx.media;


    opens CODEX.view.GUI to javafx.fxml, javafx.graphics;
    exports CODEX.distributed.RMI;
    exports CODEX.distributed;
    exports CODEX.utils;
    exports CODEX.Exceptions;
    exports CODEX.controller;
    exports CODEX.org.model;
    exports CODEX.view.TUI;
    exports CODEX.view.GUI;
}