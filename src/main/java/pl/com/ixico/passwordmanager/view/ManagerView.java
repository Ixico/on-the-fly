package pl.com.ixico.passwordmanager.view;

import atlantafx.base.theme.Styles;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.fontawesome5.FontAwesomeBrands;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.stage.ParentAware;
import pl.com.ixico.passwordmanager.controller.ManagerController;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

import java.util.List;

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
        initializeView();

    }

    public void update() {
        checksumLabel.setText(model.getPasswordChecksum());
    }

    private void initializeView() {
        customizeRoot();
        parent.getChildren().addAll(
                ViewUtils.logo(),
                ViewUtils.caption("Enter application name:"),
                icons(),
                ViewUtils.textFieldInput(domainField, generateButton),
                ViewUtils.separator(),
                ViewUtils.caption("Session expiration:"),
//                ViewUtils.checksumInputGroup(sessionExpirationLabel, "Session expiration"),
                sessionWithRefresh(new StackPane(sessionExpirationBar, sessionExpirationLabel), refreshButton),
                new HBox(ViewUtils.checksumInputGroup(checksumLabel, "Checksum"), logoutButton) {{
                    setSpacing(20);
                    setAlignment(Pos.CENTER);
                }}
        );
        observeSessionExpiration();
        observeSessionExpirationPart();
        listenGenerateButton();
        listenDomainField();
        listenRefreshButton();
        listenLogoutButton();
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

    private void listenRefreshButton() {
        refreshButton.setOnAction(e -> {
            controller.onRefreshButtonPressed();
            sessionExpirationBar.getStyleClass().addAll(Styles.SUCCESS);
        });
    }

    private void listenLogoutButton() {
        logoutButton.setOnAction(e -> {
            controller.onLogoutButtonPressed();
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
        var button = new Button(null, new FontIcon(Material2MZ.REFRESH));
        button.setDefaultButton(false);
        return button;
    }

    private Button logoutButton() {
        var button = new Button("Logout", new FontIcon(Material2AL.LOG_OUT));
        button.setDefaultButton(false);
        return button;
    }

    private HBox sessionWithRefresh(StackPane progressBar, Button button) {
        var hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(progressBar, button);
        return hbox;
    }

    private HBox icons() {
        var hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);
        hbox.setMinHeight(50);
        hbox.getStyleClass().addAll(Styles.TITLE_1);
        parent.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

        var icons = List.of(
                new FontIcon(FontAwesomeBrands.FACEBOOK),
                new FontIcon(FontAwesomeBrands.GOOGLE),
                new FontIcon(FontAwesomeBrands.LINKEDIN),
                new FontIcon(FontAwesomeBrands.YOUTUBE),
                new FontIcon(FontAwesomeBrands.GITHUB)
        ).stream().map(icon -> new Button(null, icon)).toList();
//        icons.forEach(icon -> icon.getStyleClass().addAll("resized"));
        icons.forEach(button -> button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT, Styles.LARGE));
        hbox.getChildren().addAll(icons);
        return hbox;
    }
}
