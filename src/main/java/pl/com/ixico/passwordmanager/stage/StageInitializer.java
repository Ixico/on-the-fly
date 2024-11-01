package pl.com.ixico.passwordmanager.stage;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.DesktopApplication;
import pl.com.ixico.passwordmanager.view.LoginView;

@Component
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<DesktopApplication.StageReadyEvent> {

    private final LoginView loginView;

    @Override
    public void onApplicationEvent(DesktopApplication.StageReadyEvent event) {
        var stage = event.getStage();
        StageContext.set(stage);
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        stage.setResizable(false);
        stage.setTitle("On-the-fly");
        stage.setScene(new Scene(loginView.getParent(), 800, 540));
        stage.getIcons().add(new Image("icon.png"));
        stage.show();
    }
}
