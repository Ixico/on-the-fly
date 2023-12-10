package pl.com.ixico.passwordmanager.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class ViewUtils {

    public static VBox centeringVbox(Node... children) {
        var vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(children);
        vbox.setPadding(new Insets(20, 0, 0, 0));
        vbox.setSpacing(20);
        return vbox;
    }


    public static Button button() {
        var button = new Button("Generate", new FontIcon(Material2AL.CONTENT_COPY));
        button.setDefaultButton(true);
        return button;
    }



}
