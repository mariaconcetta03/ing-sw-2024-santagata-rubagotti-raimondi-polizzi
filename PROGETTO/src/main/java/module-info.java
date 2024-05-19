module CODEX {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.rmi;
    requires java.sql;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires json.simple;
    requires com.ctc.wstx;


    opens CODEX.view.GUI to javafx.fxml, javafx.graphics;
    exports CODEX.distributed.RMI;
    exports CODEX.view.GUI; // NOT SURE (nell'esempio a file inizializzato, dava "demo")
}