package pl.com.ixico.passwordmanager.context;

import javafx.stage.Stage;
import pl.com.ixico.passwordmanager.common.ParentAware;

public class StageContext {

    private static Stage stage;

    public static void set(Stage stage) {
        StageContext.stage = stage;
    }

    public static void changeView(ParentAware parentAware) {
        stage.getScene().setRoot(parentAware.getParent());
    }

}
