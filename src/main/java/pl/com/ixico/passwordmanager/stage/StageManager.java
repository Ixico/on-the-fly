package pl.com.ixico.passwordmanager.stage;

import lombok.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.ManagerController;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.view.LoginView;
import pl.com.ixico.passwordmanager.view.ManagerView;

@Component
@RequiredArgsConstructor
public class StageManager {

    private final ManagerController managerController;

    private final LoginView loginView;

    private final LoginModel loginModel;

    private final ManagerView managerView;

    private final ManagerModel managerModel;


    @EventListener
    public void onApplicationEvent(DisplayLoginViewEvent ignore) {

        loginView.update(managerView.isSilentMode());
        managerModel.clear();
        StageContext.changeView(loginView);
    }

    @EventListener
    public void onApplicationEvent(DisplayManagerViewEvent event) {
        managerModel.setPasswordChecksum(event.getPasswordChecksum());
        managerModel.setMasterKey(event.getMasterKey());
        managerController.initClock();
        loginView.close();

        managerView.update(loginView.isSilentMode());
        loginModel.clear();
        StageContext.changeView(managerView);
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class DisplayManagerViewEvent {

        private String masterKey;

        private String passwordChecksum;

    }

    @NoArgsConstructor(staticName = "instance")
    public static class DisplayLoginViewEvent {
    }
}
