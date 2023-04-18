package clientFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientFXLauncher extends Application {
    public ClientFXLauncher() {
    }

    public void start(Stage stage) throws Exception {
        ClientFXModele leModele = new ClientFXModele();
        ClientFXVue laVue = new ClientFXVue();
        new ClientFXController(leModele, laVue);
        Scene scene = new Scene(laVue, 600.0, 800.0);
        stage.setScene(scene);
        stage.setTitle("Mon MVC JavaFXCompteur");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}