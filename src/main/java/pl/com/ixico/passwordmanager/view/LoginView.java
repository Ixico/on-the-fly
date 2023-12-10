package pl.com.ixico.passwordmanager.view;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import jakarta.annotation.PostConstruct;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.boxicons.BoxiconsSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.LoginController;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.utils.Content;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginView extends BaseView {

    private final LoginController controller;

    private final LoginModel model;


    private final PasswordField passwordField = passwordField();

    private final Button passwordButton = passwordButton();


    private final Label lengthRequirementLabel = requirementLabel("Minimum 16 characters");

    private final Label caseRequirementLabel = requirementLabel("Uppercase and lowercase letters");

    private final Label complexityRequiremenetLabel = requirementLabel("Numbers and symbols");

    private final Label noTrivialSequencesRequirementLabel = requirementLabel("No trivial sequences");

    private final FontIcon lengthRequirementIcon = requirementIcon();

    private final FontIcon caseRequirementIcon = requirementIcon();

    private final FontIcon complexityRequirementIcon = requirementIcon();

    private final FontIcon noTrivialSequencesRequirementIcon = requirementIcon();

    private Map<FontIcon, BooleanProperty> requirements;


    private Alert alert;

    private Animation generatingAnimation;


    @PostConstruct
    public void init() {
        this.noTrivialSequencesRequirementLabel.setTooltip(tooltip(Content.noTrivialSequencesTooltip()));
        requirements = Map.of(
                lengthRequirementIcon, model.getLengthRequirementFulfilled(),
                caseRequirementIcon, model.getCaseRequirementFulfilled(),
                complexityRequirementIcon, model.getComplexityRequirementFulfilled(),
                noTrivialSequencesRequirementIcon, model.getNoTrivialSequencesRequirementFulfilled()
        );
        initializeView();
    }

    private void initializeView() {
        parent.getChildren().addAll(
                menuWithLogo(silentModeButton, helpButton),
                caption("Enter Master Password:"),
                passwordInput(passwordField, passwordButton),
                checksumInputGroup(checksumLabel),
                horizontalSeparator(),
                caption("Master password requirements:"),
                requirements()
        );
        observeChecksum();
        observeRequirements();
        listenPasswordInput();
        listenGenerateButton();
        listenHelpButton();
        listenSilentModeButton();
        registerShortcuts();
    }


    private void registerShortcuts() {
        parent.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.S && key.isControlDown()) {
                silentModeButton.fire();
            }
        });
    }

    public void update(boolean silentMode) {
        silentModeButton.setSelected(!silentMode);
        silentModeButton.fire();
        passwordField.setText("");
        checksumLabel.setText("");
    }

    private void observeChecksum() {
        model.getPasswordHashFragment().addListener((observableValue, oldValue, newValue) -> {
            if (isSilentMode()) return;
            checksumLabel.setText(newValue);
        });
    }

    private void observeRequirements() {
        requirements.forEach(this::observeRequirement);
    }

    private void observeRequirement(FontIcon fontIcon, BooleanProperty booleanProperty) {
        booleanProperty.addListener((observableValue, fulfilledBefore, fulfilled) -> {
            if (isSilentMode()) return;
            updateRequirementState(fontIcon, booleanProperty);
        });
    }

    private void updateRequirementState(FontIcon fontIcon, BooleanProperty requirementValue) {
        if (requirementValue.get()) {
            fontIcon.setIconCode(Material2RoundAL.CHECK_CIRCLE);
            changeStyle(fontIcon, Styles.SUCCESS);
        } else {
            fontIcon.setIconCode(Material2RoundAL.ERROR);
            changeStyle(fontIcon, Styles.WARNING);
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
            if (!model.getCaseRequirementFulfilled().get()) {
                flash(caseRequirementLabel);
            }
            if (!model.getComplexityRequirementFulfilled().get()) {
                flash(complexityRequiremenetLabel);
            }
            if (!model.getNoTrivialSequencesRequirementFulfilled().get()) {
                flash(noTrivialSequencesRequirementLabel);
            }
            if (controller.areRequirementsFulfilled()) {
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

    public void closeGeneratingMasterKeyAlert() {
        generatingAnimation.stop();
        alert.close();
    }

    private void flash(Node node) {
        Animations.flash(node).playFromStart();
    }


    private void listenSilentModeButton() {
        silentModeButton.setOnAction(e -> {
            if (silentModeButton.isSelected()) {
                checksumLabel.setText("****");
                requirements.forEach(((icon, booleanProperty) -> {
                    icon.setIconCode(BoxiconsSolid.HIDE);
                    changeStyle(icon, Styles.ACCENT);
                }));
            } else {
                checksumLabel.setText(model.getPasswordHashFragment().get());
                requirements.forEach(this::updateRequirementState);
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

    private HBox requirements() {
        var leftVbox = new VBox();
        leftVbox.getChildren().addAll(
                requirement(lengthRequirementLabel, lengthRequirementIcon),
                requirement(complexityRequiremenetLabel, complexityRequirementIcon)
        );
        var rightVbox = new VBox();
        rightVbox.getChildren().addAll(
                requirement(caseRequirementLabel, caseRequirementIcon),
                requirement(noTrivialSequencesRequirementLabel, noTrivialSequencesRequirementIcon)
        );
        leftVbox.setSpacing(20);
        rightVbox.setSpacing(20);
        return centeringHbox(leftVbox, rightVbox);
    }

    private HBox requirement(Label requirementLabel, FontIcon requirementIcon) {
        var hbox = new HBox();
        hbox.getChildren().addAll(requirementLabel, requirementIcon);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);
        return hbox;
    }

    private Label requirementLabel(String text) {
        var label = new Label(text);
        label.getStyleClass().add(Styles.TEXT_CAPTION);
        return label;
    }

    private FontIcon requirementIcon() {
        var icon = new FontIcon(Material2RoundAL.ERROR);
        icon.getStyleClass().addAll(Styles.WARNING);
        return icon;
    }

}
