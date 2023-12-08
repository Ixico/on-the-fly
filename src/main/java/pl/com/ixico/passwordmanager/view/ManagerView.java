package pl.com.ixico.passwordmanager.view;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.ManagerController;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.stage.ParentAware;
import pl.com.ixico.passwordmanager.utils.Content;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

@Component
@RequiredArgsConstructor
public class ManagerView implements ParentAware {

    @Getter
    private VBox parent;

    private final ManagerModel model;

    private final ManagerController controller;

    private TextField domainField;

    private Label checksumLabel;

    private Label sessionExpirationLabel;

    private ProgressBar sessionExpirationBar;

    private Button generateButton;

    private Button refreshButton;

    private Button logoutButton;

    private ToggleButton silentModeButton;

    private Button helpButton;


    @PostConstruct
    public void init() {
        parent = new VBox();
        this.domainField = ViewUtils.textField("Enter domain...", false);
        this.checksumLabel = ViewUtils.checksumLabel("");
        this.sessionExpirationLabel = ViewUtils.checksumLabel("");
        this.sessionExpirationBar = session();
        this.generateButton = ViewUtils.button();
        this.refreshButton = refreshButton();
        this.logoutButton = logoutButton();
        this.silentModeButton = silentMode();
        this.helpButton = helpButton();
        initializeView();

    }

    public void update(boolean silentMode) {
        silentModeButton.setSelected(!silentMode);
        silentModeButton.fire();
    }

    private void initializeView() {
        customizeRoot();
        parent.getChildren().addAll(
                menuWithLogo(silentModeButton, helpButton),
                ViewUtils.caption("Enter application name:"),
//                icons(),
                ViewUtils.textFieldInput(domainField, generateButton),
                ViewUtils.separator(),

//                ViewUtils.checksumInputGroup(sessionExpirationLabel, "Session expiration"),

                ViewUtils.centeringHbox(
                        ViewUtils.centeringVbox(
                                ViewUtils.caption("Session expiration:"),
                                new StackPane(sessionExpirationBar, sessionExpirationLabel),
                                refreshButton
                        ),
                        new Separator(Orientation.VERTICAL),
                        ViewUtils.centeringVbox(ViewUtils.caption("Session details:"),
                                ViewUtils.checksumInputGroup(checksumLabel, "Checksum"),
                                logoutButton
                        )
                )
        );
        observeSessionExpiration();
        observeSessionExpirationPart();
        listenGenerateButton();
        listenDomainField();
        listenRefreshButton();
        listenLogoutButton();
        registerShortcuts();
        listenSilentModeButton();
        listenHelpButton();
    }

    private void listenSilentModeButton() {
        silentModeButton.setOnAction(e -> {
            if (isSilentMode()) {
                checksumLabel.setText("****");
            } else {
                checksumLabel.setText(model.getPasswordChecksum());
            }
        });
    }

    private void listenHelpButton() {
        helpButton.setOnAction(e -> {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText("How to use On-the-fly?");
            alert.setContentText(Content.help());
            alert.initOwner(parent.getScene().getWindow());
            alert.show();
        });
    }


    private BorderPane menuWithLogo(ToggleButton silentMode, Button help) {
        var hbox = new HBox(silentMode, help);
        hbox.setSpacing(20);
        var borderPane = new BorderPane();
        var region = new Region();
        region.prefWidthProperty().bind(hbox.widthProperty());
        borderPane.leftProperty().set(region);
        borderPane.centerProperty().set(ViewUtils.logo());
        borderPane.rightProperty().set(hbox);
        return borderPane;
    }

    private ToggleButton silentMode() {
        var toggleButton = new ToggleButton(null, new FontIcon(BoxiconsSolid.HIDE));
        toggleButton.setFocusTraversable(false);
        toggleButton.getStyleClass().addAll(Styles.BUTTON_ICON);
        toggleButton.setTooltip(ViewUtils.tooltip("Silent mode\nCTRL+S"));
        return toggleButton;
    }

    private Button helpButton() {
        var button = new Button(null, new FontIcon(Material2AL.HELP));
        button.setFocusTraversable(false);
        button.getStyleClass().addAll(Styles.BUTTON_ICON);
        button.setTooltip(ViewUtils.tooltip("Help"));
        return button;
    }

    private void observeSessionExpiration() {
        model.getSessionExpiration().addListener((observableValue, oldValue, newValue) ->
                sessionExpirationLabel.setText(newValue));
    }

    private void observeSessionExpirationPart() {
        model.getSessionExpirationPart().addListener((observableValue, oldValue, newValue) ->
                sessionExpirationBar.setProgress(newValue.doubleValue()));
    }

    private void customizeRoot() {
        parent.setAlignment(Pos.TOP_CENTER);
        parent.setSpacing(20);
        parent.setPadding(new Insets(20));
    }

    private void listenGenerateButton() {
        generateButton.setOnAction(e -> {
            generateButton.getStyleClass().addAll(Styles.SUCCESS);
            generateButton.setText("Copied");
            controller.onGeneratePressed(domainField.getText());
        });
    }

    private void listenDomainField() {
        domainField.setOnKeyTyped(e -> {
            // on enter pressed do nothing
            if (e.getCharacter().equals("\r")) {
                System.out.println("hi");
                return;
            }
            generateButton.getStyleClass().remove(Styles.SUCCESS);
            generateButton.setText("Generate");
        });
    }

    private void listenRefreshButton() {
        refreshButton.setOnAction(e -> {
            controller.onRefreshButtonPressed();
            Animations.fadeIn(sessionExpirationBar, Duration.seconds(2)).playFromStart();
        });
    }

    private void registerShortcuts() {
        parent.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.S && key.isControlDown()) {
                silentModeButton.fire();
            }
            if (key.getCode() == KeyCode.L && key.isControlDown()) {
                logoutButton.fire();
            }
            if (key.getCode() == KeyCode.R && key.isControlDown()) {
                refreshButton.fire();
            }
        });
    }

    private void listenLogoutButton() {
        logoutButton.setOnAction(e -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");
            alert.initOwner(parent.getScene().getWindow());
            alert.showAndWait().filter(buttonType -> buttonType == ButtonType.OK)
                    .ifPresent(buttonType -> controller.onLogoutButtonPressed());
        });
    }

    private ProgressBar session() {
        var progressBar = new ProgressBar(0.5);
        progressBar.getStyleClass().add(Styles.LARGE);
        progressBar.setPrefWidth(200);
//        progressBar.setPrefHeight(50);
        return progressBar;
    }

    private Button refreshButton() {
        var button = new Button("Refresh", new FontIcon(Material2MZ.REFRESH));
        button.setDefaultButton(false);
        return button;
    }

    private Button logoutButton() {
        var button = new Button("Logout", new FontIcon(Material2AL.LOG_OUT));
        button.setDefaultButton(false);
        return button;
    }

    public boolean isSilentMode() {
        return silentModeButton.isSelected();
    }
}
