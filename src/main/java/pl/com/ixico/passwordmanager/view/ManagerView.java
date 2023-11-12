package pl.com.ixico.passwordmanager.view;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.common.ParentAware;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

@Component
@RequiredArgsConstructor
public class ManagerView implements ParentAware {

    @Getter
    private VBox parent;

    private final ManagerModel model;

    private TextField domainField;

    private Label checksumLabel;

    private Label sessionExpirationLabel;

    private ProgressBar sessionExpirationBar;

    private Button generateButton;


    @PostConstruct
    public void init() {
        parent = new VBox();
        this.domainField = ViewUtils.textField("Enter domain...", false);
        this.checksumLabel = ViewUtils.checksumLabel("8d13");
        this.sessionExpirationLabel = ViewUtils.checksumLabel("01:53");
        this.sessionExpirationBar = session();
        this.generateButton = ViewUtils.button();
        initializeView();

    }

    private void initializeView() {
        customizeRoot();
        parent.getChildren().addAll(
                ViewUtils.logo(),
                ViewUtils.caption("Enter application name:"),
                ViewUtils.textFieldInput(domainField, generateButton),
                ViewUtils.separator(),
                ViewUtils.checksumInputGroup(checksumLabel, "Current session checksum"),
                ViewUtils.checksumInputGroup(sessionExpirationLabel, "Session expiration"),
                sessionWithRefresh(sessionExpirationBar)
        );
        observeSessionExpiration();
        observeSessionExpirationPart();
        listenGenerateButton();
        listenDomainField();
    }

    private void observeSessionExpiration() {
        model.getSessionExpiration().addListener((observableValue, oldValue, newValue) -> {
            sessionExpirationLabel.setText(newValue);
        });
    }

    private void observeSessionExpirationPart() {
        model.getSessionExpirationPart().addListener((observableValue, oldValue, newValue) -> {
            sessionExpirationBar.setProgress(newValue.doubleValue());
        });
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
    private ProgressBar session() {
        var progressBar = new ProgressBar(0.5);
        progressBar.setPrefWidth(250);
//        progressBar.setPrefHeight(50);
        return progressBar;
    }

    private HBox sessionWithRefresh(ProgressBar progressBar) {
        var button = new Button(null, new FontIcon(Material2MZ.REFRESH));
        button.setDefaultButton(false);
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        hbox.getChildren().addAll(progressBar, button);
        return hbox;
    }


}
