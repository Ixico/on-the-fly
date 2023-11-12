package pl.com.ixico.passwordmanager.controller;

import javafx.animation.AnimationTimer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.stereotype.Controller;
import pl.com.ixico.passwordmanager.component.ApplicationContextHolder;
import pl.com.ixico.passwordmanager.model.ManagerModel;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerModel managerModel;

    private final ApplicationContextHolder applicationContextHolder;

    private Long sessionEndMillis;

    private AnimationTimer sessionTimer;

    public void initClock() {
        sessionEndMillis = Instant.now().plusSeconds(applicationContextHolder.getSessionTimeSeconds()).toEpochMilli();
        sessionTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                var remainingMillis = sessionEndMillis - Instant.now().toEpochMilli();
                managerModel.setSessionExpiration(
                        DurationFormatUtils.formatDuration(remainingMillis, "mm:ss")
                );
                managerModel.setSessionExpirationPart(
                        (float) remainingMillis / applicationContextHolder.getSessionTimeSeconds() / 1000
                );
            }
        };
        sessionTimer.start();
    }

    public void stopClock() {
        sessionTimer.stop();
    }
}
