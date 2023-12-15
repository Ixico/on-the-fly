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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.ManagerController;
import pl.com.ixico.passwordmanager.model.ManagerModel;

@Component
@RequiredArgsConstructor
public class ManagerView extends BaseView {


    private final ManagerModel model;

    private final ManagerController controller;

    private final TextField domainField = domainTextField();

    private final Label sessionExpirationLabel = sessionExpirationLabel();

    private final ProgressBar sessionExpirationBar = sessionExpirationBar();

    private final Button generateButton = generateButton();

    private final Button refreshButton = refreshButton();

    private final Button logoutButton = logoutButton();

    @PostConstruct
    public void init() {
        initializeView();
    }

    public void update(boolean silentMode) {
        silentModeButton.setSelected(!silentMode);
        silentModeButton.fire();
    }

    private void initializeView() {
        parent.getChildren().addAll(
                menuWithLogo(silentModeButton, helpButton),
                caption("Enter application name:"),
                textFieldInput(domainField, generateButton),
                horizontalSeparator(),
                centeringHbox(
                        centeringVbox(
                                caption("Session expiration:"),
                                new StackPane(sessionExpirationBar, sessionExpirationLabel),
                                refreshButton
                        ),
                        new Separator(Orientation.VERTICAL),
                        centeringVbox(caption("Session details:"),
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

    private Button generateButton() {
        var button = new Button("Generate", new FontIcon(Material2AL.CONTENT_COPY));
        button.setDefaultButton(true);
        return button;
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

    private ProgressBar sessionExpirationBar() {
        var progressBar = new ProgressBar(0.5);
        progressBar.getStyleClass().add(Styles.LARGE);
        progressBar.setPrefWidth(200);
        return progressBar;
    }

    private Button refreshButton() {
        var button = new Button("Refresh", new FontIcon(Material2MZ.REFRESH));
        button.setDefaultButton(false);
        button.setTooltip(tooltip("CTRL+R"));
        return button;
    }

    private Button logoutButton() {
        var button = new Button("Logout", new FontIcon(Material2AL.LOG_OUT));
        button.setDefaultButton(false);
        button.setTooltip(tooltip("CTRL+L"));
        return button;
    }

}
