package pl.com.ixico.passwordmanager.view;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.ManagerController;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

@Component
@RequiredArgsConstructor
public class ManagerView extends BaseView {


    private final ManagerModel model;

    private final ManagerController controller;

    private TextField domainField;

    private Label sessionExpirationLabel = sessionExpirationLabel();

    private ProgressBar sessionExpirationBar;

    private Button generateButton;

    private Button refreshButton;

    private Button logoutButton;

    @PostConstruct
    public void init() {
        this.domainField = domainTextField();
        this.sessionExpirationBar = session();
        this.generateButton = ViewUtils.button();
        this.refreshButton = refreshButton();
        this.logoutButton = logoutButton();
        this.silentModeButton = silentMode();
        initializeView();

    }

    public void update(boolean silentMode) {
        silentModeButton.setSelected(!silentMode);
        silentModeButton.fire();
    }
// TODO: buttons and CenteringVBox
    private void initializeView() {
        parent.getChildren().addAll(
                menuWithLogo(silentModeButton, helpButton),
                caption("Enter application name:"),
                textFieldInput(domainField, generateButton),
                horizontalSeparator(),
                centeringHbox(
                        ViewUtils.centeringVbox(
                                caption("Session expiration:"),
                                new StackPane(sessionExpirationBar, sessionExpirationLabel),
                                refreshButton
                        ),
                        new Separator(Orientation.VERTICAL),
                        ViewUtils.centeringVbox(caption("Session details:"),
                                checksumInputGroup(checksumLabel),
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

    public static TextField domainTextField() {
        var textField = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                super.replaceText(start, end, text.toUpperCase());
            }
        };
        textField.getStyleClass().add(Styles.TEXT_BOLD);
        textField.setPromptText("Enter domain...");
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(300);
        return textField;
    }

    public static Label sessionExpirationLabel() {
        var label = new Label();
        label.setMinWidth(70);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().addAll(Styles.TEXT_BOLD);
        return label;
    }

    private ToggleButton silentMode() {
        var toggleButton = new ToggleButton(null, new FontIcon(BoxiconsSolid.HIDE));
        toggleButton.setFocusTraversable(false);
        toggleButton.getStyleClass().addAll(Styles.BUTTON_ICON);
        toggleButton.setTooltip(tooltip("Silent mode\nCTRL+S"));
        return toggleButton;
    }


    private void observeSessionExpiration() {
        model.getSessionExpiration().addListener((observableValue, oldValue, newValue) ->
                sessionExpirationLabel.setText(newValue));
    }

    private void observeSessionExpirationPart() {
        model.getSessionExpirationPart().addListener((observableValue, oldValue, newValue) ->
                sessionExpirationBar.setProgress(newValue.doubleValue()));
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

}
