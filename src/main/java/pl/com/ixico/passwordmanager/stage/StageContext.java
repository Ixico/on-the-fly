package pl.com.ixico.passwordmanager.stage;

import javafx.stage.Stage;

public class StageContext {

    private static Stage stage;

    public static void set(Stage stage) {
        StageContext.stage = stage;
    }

    public static void changeView(ParentAware parentAware) {
        stage.getScene().setRoot(parentAware.getParent());
    }

}
