package pl.com.ixico.passwordmanager.stage;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.controller.ManagerController;

@Component
@RequiredArgsConstructor
public class StageManager {

    private final ManagerController managerController;

    @EventListener
    public void onApplicationEvent(DisplayManagerViewEvent ignore) {
        managerController.initClock();
    }

    @NoArgsConstructor(staticName = "instance")
    public static class DisplayManagerViewEvent {
    }
}
