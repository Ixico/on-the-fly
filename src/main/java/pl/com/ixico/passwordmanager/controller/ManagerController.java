package pl.com.ixico.passwordmanager.controller;

import javafx.animation.AnimationTimer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import pl.com.ixico.passwordmanager.model.ManagerModel;
import pl.com.ixico.passwordmanager.stage.StageManager;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerModel managerModel;


    private final ApplicationEventPublisher applicationEventPublisher;

    private AnimationTimer sessionTimer;

    private static final Integer SESSION_LENGTH_SECONDS = 300;

    public void initClock() {
        var sessionEndMillis = Instant.now().plusSeconds(SESSION_LENGTH_SECONDS).toEpochMilli();
        sessionTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                var remainingMillis = sessionEndMillis - Instant.now().toEpochMilli();
                if (remainingMillis < 0) {
                    applicationEventPublisher.publishEvent(StageManager.DisplayLoginViewEvent.instance());
                    stop();
                    return;
                }
                managerModel.setSessionExpiration(
                        DurationFormatUtils.formatDuration(remainingMillis, "mm:ss")
                );
                managerModel.setSessionExpirationPart(
                        (float) remainingMillis / SESSION_LENGTH_SECONDS / 1000
                );
            }
        };
        sessionTimer.start();
    }

    public void onRefreshButtonPressed() {
        sessionTimer.stop();
        initClock();
    }

    public void onLogoutButtonPressed() {
        sessionTimer.stop();
        applicationEventPublisher.publishEvent(StageManager.DisplayLoginViewEvent.instance());
    }
}
