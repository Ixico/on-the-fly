package pl.com.ixico.passwordmanager.view;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import lombok.Getter;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import pl.com.ixico.passwordmanager.stage.ParentAware;
import pl.com.ixico.passwordmanager.utils.Content;

public abstract class BaseView implements ParentAware {

    @Getter
    protected VBox parent = parent();

    protected Label checksumLabel = checksumLabel();

    protected ToggleButton silentModeButton = silentModeButton();

    protected Button helpButton = helpButton();


    public static Tooltip tooltip(String text) {
        var tooltip = new Tooltip(text);
        tooltip.setTextAlignment(TextAlignment.CENTER);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
        return tooltip;
    }

    public static HBox centeringHbox(Node... children) {
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(children);
        hbox.setSpacing(60);
        return hbox;
    }

    public static void changeStyle(Node node, String style) {
        node.getStyleClass().removeAll(Styles.ACCENT, Styles.SUCCESS, Styles.DANGER, Styles.WARNING);
        node.getStyleClass().add(style);
    }

    protected static InputGroup checksumInputGroup(Label checksumLabel) {
        var captionLabel = new Label("Checksum");
        captionLabel.setMinWidth(100);
        captionLabel.getStyleClass().addAll(Styles.TEXT_CAPTION);
        captionLabel.setTooltip(tooltip("Checksum for password\nyou've provided"));
        captionLabel.setGraphic(new FontIcon(Material2AL.INFO));
        var inputGroup = new InputGroup(captionLabel, checksumLabel);
        inputGroup.setAlignment(Pos.CENTER);
        return inputGroup;
    }

    public static Text caption(String title) {
        var text = new Text(title);
        text.getStyleClass().add(Styles.TITLE_2);
        return text;
    }

    public static Label checksumLabel() {
        var label = new Label();
        label.setMinWidth(70);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_BOLD);
        return label;
    }

    public static Separator horizontalSeparator() {
        return new Separator(Orientation.HORIZONTAL);
    }


    public static Button button() {
        var button = new Button("Generate", new FontIcon(Material2AL.CONTENT_COPY));
        button.setDefaultButton(true);
        return button;
    }

    public static InputGroup textFieldInput(TextField textField, Button passwordButton) {
        var passwordInputGroup = new InputGroup(textField, passwordButton);
        passwordInputGroup.setAlignment(Pos.CENTER);
        return passwordInputGroup;
    }

    public static BorderPane menuWithLogo(ToggleButton silentMode, Button help) {
        var hbox = new HBox(silentMode, help);
        hbox.setSpacing(20);
        var borderPane = new BorderPane();
        var region = new Region();
        region.prefWidthProperty().bind(hbox.widthProperty());
        borderPane.leftProperty().set(region);
        borderPane.centerProperty().set(logo());
        borderPane.rightProperty().set(hbox);
        return borderPane;
    }

    public static ImageView logo() {
        var image = new Image("logo.png");
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        return imageView;
    }

    private VBox parent() {
        var parent = new VBox();
        parent.setAlignment(Pos.TOP_CENTER);
        parent.setSpacing(20);
        parent.setPadding(new Insets(20));
        return parent;
    }

    private ToggleButton silentModeButton() {
        var toggleButton = new ToggleButton(null, new FontIcon(BoxiconsSolid.HIDE));
        toggleButton.setFocusTraversable(false);
        toggleButton.getStyleClass().addAll(Styles.BUTTON_ICON);
        toggleButton.setTooltip(tooltip("Silent mode\nCTRL+S"));
        return toggleButton;
    }

    protected void listenHelpButton() {
        helpButton.setOnAction(e -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText("How to use On-the-fly?");
            alert.setContentText(Content.help());
            alert.initOwner(parent.getScene().getWindow());
            alert.show();
        });
    }

    private Button helpButton() {
        var button = new Button(null, new FontIcon(Material2AL.HELP));
        button.setFocusTraversable(false);
        button.getStyleClass().addAll(Styles.BUTTON_ICON);
        button.setTooltip(tooltip("Help"));
        return button;
    }


    public boolean isSilentMode() {
        return silentModeButton.isSelected();
    }

}
