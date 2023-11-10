package pl.com.ixico.passwordmanager.utils;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class ViewUtils {

    public static ImageView logo() {
        var image = new Image("logo.png");
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        return imageView;
    }

    public static Text caption(String title) {
        var text = new Text(title);
        text.getStyleClass().add(Styles.TITLE_2);
        return text;
    }

    public static InputGroup textFieldInput(TextField textField, Button passwordButton) {
        var passwordInputGroup = new InputGroup(textField, passwordButton);
        passwordInputGroup.setAlignment(Pos.CENTER);
        return passwordInputGroup;
    }

    public static TextField textField(String prompt, boolean password) {
        TextField textField;
        if (password) {
            textField = new PasswordField();
        } else {
            textField = new TextField();
            textField.getStyleClass().add(Styles.TEXT_BOLD);
        }
        textField.setPromptText(prompt);
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(300);
        return textField;
    }

    public static Button button() {
        var button = new Button("Generate");
        button.setDefaultButton(true);
        return button;
    }

    public static Separator separator() {
        return new Separator(Orientation.HORIZONTAL);
    }

    public static Label checksumLabel() {
        var label = new Label("8d13");
        label.setMinWidth(70);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_BOLD);
        return label;
    }

    public static InputGroup checksumInputGroup(Label checksumLabel, String text) {
        var captionLabel = new Label(text);
        captionLabel.getStyleClass().addAll(Styles.TEXT_CAPTION);

        var icon = new FontIcon(Material2AL.INFO);

        var tooltip = new Tooltip("Checksum for password\nyou've provided");

        tooltip.setTextAlignment(TextAlignment.CENTER);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
        captionLabel.setTooltip(tooltip);
        captionLabel.setGraphic(icon);
        var inputGroup = new InputGroup(captionLabel, checksumLabel);
        inputGroup.setAlignment(Pos.CENTER);
        return inputGroup;
    }

}
