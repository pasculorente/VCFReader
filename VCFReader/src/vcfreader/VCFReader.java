package vcfreader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class VCFReader extends Application {

    private static Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the stage
     */
    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add("vcfreader/default.css");
        stage.setScene(scene);
        stage.setTitle("VCF Reader");
        stage.show();
        VCFReader.stage = stage;
    }

    static void setTitle(String title) {
        getStage().setTitle("VCF Reader - " + title);
    }
}
