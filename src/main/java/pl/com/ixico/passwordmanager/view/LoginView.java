package pl.com.ixico.passwordmanager.view;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import jakarta.annotation.PostConstruct;
import javafx.animation.Animation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.LoginController;
import pl.com.ixico.passwordmanager.model.LoginModel;

@Component
@RequiredArgsConstructor
public class LoginView extends BaseView {

    private final LoginController controller;

    private final LoginModel model;


    private final PasswordField passwordField = passwordField();

    private final Button passwordButton = passwordButton();


    private final Label lengthRequirementLabel = requirementLabel();

    private final FontIcon lengthRequirementIcon = requirementIcon();

    private final ProgressBar passwordStrength = progressBar();

    private final Label passwordStrengthLabel = progressBarLabel();


    private Alert alert;

    private Animation generatingAnimation;


    @PostConstruct
    public void init() {
        parent.getChildren().addAll(
                menuWithLogo(silentModeButton, helpButton),
                caption("Enter Master Password:"),
                passwordInput(passwordField, passwordButton),
                checksumInputGroup(checksumLabel),
                horizontalSeparator(),
                centeringHbox(
                        centeringVbox(
                                caption("Required length:"),
                                requirement(lengthRequirementLabel, lengthRequirementIcon)
                        ),
                        new Separator(Orientation.VERTICAL),
                        centeringVbox(caption("Password strength:"),
                                new StackPane(passwordStrength, passwordStrengthLabel)
                        )
                )
        );
        observeChecksum();
        observeLengthRequirement();
        observePasswordStrength();
        listenPasswordInput();
        listenGenerateButton();
        listenHelpButton();
        listenSilentModeButton();
        registerShortcuts();
    }

    public void update(boolean silentMode) {
        silentModeButton.setSelected(!silentMode);
        silentModeButton.fire();
        passwordField.setText("");
        checksumLabel.setText("");
        passwordStrength.setProgress(0);
        passwordStrengthLabel.setText("");
    }

    public void closeGeneratingMasterKeyAlert() {
        generatingAnimation.stop();
        alert.close();
    }

    private void registerShortcuts() {
        parent.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.S && key.isControlDown()) {
                silentModeButton.fire();
            }
        });
    }

    private void observeChecksum() {
        model.getPasswordHashFragment().addListener((observableValue, oldValue, newValue) -> {
            if (isSilentMode()) return;
            checksumLabel.setText(newValue);
        });
    }

    private void observePasswordStrength() {
        model.getPasswordStrength().addListener((observableValue, oldValue, newValue) ->
                updatePasswordStrength(newValue));
    }

    private void updatePasswordStrength(Number newValue) {
        if (isSilentMode()) return;
        var scaledEntropy = newValue.doubleValue() / 60;
        if (scaledEntropy < 0.25) {
            passwordStrengthLabel.setText("Very weak");
        } else if (scaledEntropy < 0.5) {
            passwordStrengthLabel.setText("Weak");
        } else if (scaledEntropy < 0.75) {
            passwordStrengthLabel.setText("Strong");
        } else {
            passwordStrengthLabel.setText("Very strong");
        }
        passwordStrength.setProgress(newValue.doubleValue() / 60);
    }

    private void observeLengthRequirement() {
        model.getLengthRequirementFulfilled().addListener((observableValue, fulfilledBefore, fulfilled) -> {
            if (isSilentMode()) return;
            updateLengthRequirement(fulfilled);
        });
    }

    private void updateLengthRequirement(boolean fulfilled) {
        if (fulfilled) {
            lengthRequirementIcon.setIconCode(Material2RoundAL.CHECK_CIRCLE);
            changeStyle(lengthRequirementIcon, Styles.SUCCESS);
        } else {
            lengthRequirementIcon.setIconCode(Material2RoundAL.ERROR);
            changeStyle(lengthRequirementIcon, Styles.WARNING);
        }
    }

    private void listenPasswordInput() {
        passwordField.setOnKeyTyped(e -> controller.onPasswordChanged(passwordField.getText()));
    }


    private void listenGenerateButton() {
        passwordButton.setOnAction(e -> {
            if (!model.getLengthRequirementFulfilled().get()) {
                flash(lengthRequirementLabel);
            }
            if (controller.isLengthRequirementFulfilled()) {
                controller.onPasswordSubmitted(passwordField.getText());
                showGeneratingMasterKeyAlert();
            }
        });
    }


    private void showGeneratingMasterKeyAlert() {
        var icon = new FontIcon(Material2MZ.REFRESH);
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.setContentText("Generating master key... It may take a while.");
        alert.setGraphic(icon);
        generatingAnimation = Animations.rotateIn(icon, Duration.seconds(2));
        generatingAnimation.setCycleCount(Animation.INDEFINITE);
        generatingAnimation.playFromStart();
        alert.showAndWait().filter(buttonType -> buttonType == ButtonType.CANCEL)
                .ifPresent(buttonType -> controller.onCancelButtonPressed());
    }


    private void flash(Node node) {
        Animations.flash(node).playFromStart();
    }


    private void listenSilentModeButton() {
        silentModeButton.setOnAction(e -> {
            if (silentModeButton.isSelected()) {
                checksumLabel.setText("****");
                passwordStrengthLabel.setText("* * * *");
                passwordStrength.setProgress(0);
                lengthRequirementIcon.setIconCode(BoxiconsSolid.HIDE);
                changeStyle(lengthRequirementIcon, Styles.ACCENT);
            } else {
                checksumLabel.setText(model.getPasswordHashFragment().get());
                updateLengthRequirement(model.getLengthRequirementFulfilled().get());
                updatePasswordStrength(model.getPasswordStrength().get());
            }
        });
    }

    private InputGroup passwordInput(PasswordField passwordField, Button passwordButton) {
        var passwordInputGroup = new InputGroup(passwordField, passwordButton);
        passwordInputGroup.setAlignment(Pos.CENTER);
        return passwordInputGroup;
    }

    private PasswordField passwordField() {
        var passwordField = new PasswordField();
        passwordField.setPromptText("Master password...");
        passwordField.setAlignment(Pos.CENTER);
        passwordField.setPrefWidth(300);
        return passwordField;
    }

    private Button passwordButton() {
        var button = new Button("Generate");
        button.setFocusTraversable(false);
        button.setDefaultButton(true);
        return button;
    }

    private HBox requirement(Label requirementLabel, FontIcon requirementIcon) {
        var hbox = new HBox();
        hbox.getChildren().addAll(requirementLabel, requirementIcon);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);
        return hbox;
    }

    private Label requirementLabel() {
        var label = new Label("Minimum 16 characters");
        label.getStyleClass().add(Styles.TEXT_CAPTION);
        return label;
    }

    private FontIcon requirementIcon() {
        var icon = new FontIcon(Material2RoundAL.ERROR);
        icon.getStyleClass().addAll(Styles.WARNING);
        return icon;
    }
}
