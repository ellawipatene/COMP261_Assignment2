package comp261.assig2;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // load the strings for language support
        // currently en_NZ and mi_NZ are supported
        Locale locale = new Locale("en", "NZ");
        ResourceBundle bundle = ResourceBundle.getBundle("comp261/assig2/resources/strings", locale);

        // load the fxml file to set up the GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MapView.fxml"), bundle);
        Parent root = loader.load();

        primaryStage.setTitle(bundle.getString("title")); // set the title of the window from the bundle
        primaryStage.setScene(new Scene(root, 800, 700));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

    }

    public static void main(String[] args) {
        launch(args);
    }

}
