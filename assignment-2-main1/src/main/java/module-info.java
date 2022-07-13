module comp261.assig2 {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    opens comp261.assig2 to javafx.fxml;
    exports comp261.assig2;
}
