package pl.com.ixico.passwordmanager.view;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.common.ParentAware;
import pl.com.ixico.passwordmanager.controller.LoginController;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

@Component
@RequiredArgsConstructor
public class LoginView implements ParentAware {

    private final LoginController controller;

    private final LoginModel model;

    @Getter
    private VBox parent;

    private PasswordField passwordField;

    private Button passwordButton;

    private Label checksumLabel;

    private Label lengthRequirementLabel;

    private Label caseRequirementLabel;

    private Label complexityRequiremenetLabel;

    private Label notCompromisedRequirementLabel;

    private FontIcon lengthRequirementIcon;

    private FontIcon caseRequirementIcon;

    private FontIcon complexityRequirementIcon;

    private FontIcon notCompromisedRequirementIcon;

    @PostConstruct
    public void init() {
        this.parent = new VBox();
        this.passwordField = passwordField();
        this.passwordButton = passwordButton();
        this.checksumLabel = checksumLabel();
        this.lengthRequirementLabel = requirementLabel("Minimum 12 characters");
        this.caseRequirementLabel = requirementLabel("Uppercase and lowercase letters");
        this.complexityRequiremenetLabel = requirementLabel("Numbers and symbols");
        this.notCompromisedRequirementLabel = requirementLabel("Password not compromised");
        this.lengthRequirementIcon = requirementIcon();
        this.caseRequirementIcon = requirementIcon();
        this.complexityRequirementIcon = requirementIcon();
        this.notCompromisedRequirementIcon = requirementIcon();

        initializeView();
    }

    private void initializeView() {
        customizeRoot();
        parent.getChildren().addAll(
                logo(),
                passwordCaption(),
                passwordInput(passwordField, passwordButton),
                checksumInputGroup(checksumLabel),
                requirementsSeparator(),
                requirements()
        );
        observeChecksum();
        observeRequirements();
        listenPasswordInput();
        listenGenerateButton();
    }

    private void customizeRoot() {
        parent.setAlignment(Pos.TOP_CENTER);
        parent.setSpacing(20);
        parent.setPadding(new Insets(20));
    }

    private void observeChecksum() {
        model.getPasswordHashFragment().addListener((observableValue, oldValue, newValue) -> checksumLabel.setText(newValue));
    }

    private void observeRequirements() {
        observeRequirement(model.getCaseRequirementFulfilled(), caseRequirementIcon);
        observeRequirement(model.getLengthRequirementFulfilled(), lengthRequirementIcon);
        observeRequirement(model.getComplexityRequirementFulfilled(), complexityRequirementIcon);
        observeRequirement(model.getNotCompromisedRequirementFulfilled(), notCompromisedRequirementIcon);
    }

    private void observeRequirement(BooleanProperty booleanProperty, FontIcon fontIcon) {
        booleanProperty.addListener((observableValue, fulfilledBefore, fulfilled) -> {
            if (fulfilledBefore == fulfilled) return;
            if (fulfilled) {
                fontIcon.setIconCode(Material2RoundAL.CHECK_CIRCLE);
                fontIcon.getStyleClass().remove(Styles.WARNING);
                fontIcon.getStyleClass().add(Styles.SUCCESS);
            } else {
                fontIcon.setIconCode(Material2RoundAL.ERROR);
                fontIcon.getStyleClass().remove(Styles.SUCCESS);
                fontIcon.getStyleClass().add(Styles.WARNING);
            }
        });
    }

    private void listenPasswordInput() {
        passwordField.setOnKeyTyped(e -> controller.onPasswordChanged(passwordField.getText()));
    }


    private void listenGenerateButton() {
        passwordButton.setOnAction(e -> {
            if (!model.getLengthRequirementFulfilled().get()) {
                flash(lengthRequirementLabel);
                // TODO: consider
//                passwordField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
//                passwordButton.getStyleClass().add(Styles.DANGER);
            }
            if (!model.getCaseRequirementFulfilled().get()) {
                flash(caseRequirementLabel);
            }
            if (!model.getComplexityRequirementFulfilled().get()) {
                flash(complexityRequiremenetLabel);
            }
            if (!model.getNotCompromisedRequirementFulfilled().get()) {
                flash(notCompromisedRequirementLabel);
            }
            controller.onPasswordSubmitted(passwordField.getText());
        });
    }

    private void flash(Node node) {
        Animations.flash(node).playFromStart();
    }

    private ImageView logo() {
        var image = new Image("logo.png");
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        return imageView;
    }

    private Text passwordCaption() {
        var passwordTitle = new Text("Enter Master Password:");
        passwordTitle.getStyleClass().add(Styles.TITLE_2);
        return passwordTitle;
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
        button.setDefaultButton(true);
        return button;
    }

    private Label checksumLabel() {
        var label = new Label("8d13");
        label.setMinWidth(70);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_BOLD);
        return label;
    }

    private InputGroup checksumInputGroup(Label checksumLabel) {
        var captionLabel = new Label("Checksum");
        captionLabel.getStyleClass().addAll(Styles.TEXT_CAPTION);

        var icon = new FontIcon(Material2AL.INFO);

        var tooltip = new Tooltip("Validate your password produces\ncorrect checksum");

        tooltip.setTextAlignment(TextAlignment.CENTER);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
        captionLabel.setTooltip(tooltip);
        captionLabel.setGraphic(icon);
        var inputGroup = new InputGroup(captionLabel, checksumLabel);
        inputGroup.setAlignment(Pos.CENTER);
        return inputGroup;
    }

    private Separator requirementsSeparator() {
        return new Separator(Orientation.HORIZONTAL);
    }

    private HBox requirements() {
        var hbox = new HBox();
        var leftVbox = new VBox();
        leftVbox.getChildren().addAll(
                requirement(lengthRequirementLabel, lengthRequirementIcon),
                requirement(complexityRequiremenetLabel, complexityRequirementIcon)
        );
        var rightVbox = new VBox();
        rightVbox.getChildren().addAll(
                requirement(caseRequirementLabel, caseRequirementIcon),
                requirement(notCompromisedRequirementLabel, notCompromisedRequirementIcon)
        );
        leftVbox.setSpacing(20);
        rightVbox.setSpacing(20);
        hbox.getChildren().addAll(leftVbox, rightVbox);
        hbox.setSpacing(50);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
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
