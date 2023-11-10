package pl.com.ixico.passwordmanager.view;

import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.common.ParentAware;
import pl.com.ixico.passwordmanager.utils.ViewUtils;

@Component
@RequiredArgsConstructor
public class ManagerView implements ParentAware {

    @Getter
    private VBox parent;


    @PostConstruct
    public void init() {
        parent = new VBox();
        initializeView();
        parent.getChildren().addAll(
                ViewUtils.logo(),
                ViewUtils.caption("Enter application name:"),
                ViewUtils.textFieldInput(ViewUtils.textField("Enter domain...", false), ViewUtils.button()),
                ViewUtils.separator(),
                ViewUtils.checksumInputGroup(ViewUtils.checksumLabel("8d13"), "Current session checksum"),
                ViewUtils.checksumInputGroup(ViewUtils.checksumLabel("01:53"), "Session expiration"),
                session()

        );
    }

    private void initializeView() {
        customizeRoot();
    }

    private void customizeRoot() {
        parent.setAlignment(Pos.TOP_CENTER);
        parent.setSpacing(20);
        parent.setPadding(new Insets(20));
    }

    private ProgressBar session() {
        var progressBar = new ProgressBar(0.5);
        progressBar.setPrefWidth(250);
//        progressBar.setPrefHeight(50);
        return progressBar;
    }


}
