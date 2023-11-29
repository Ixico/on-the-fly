package pl.com.ixico.passwordmanager.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.service.LoginService;
import pl.com.ixico.passwordmanager.stage.StageManager;
import pl.com.ixico.passwordmanager.view.ManagerView;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final LoginModel loginModel;

    private final ManagerView managerView;

    private final ApplicationEventPublisher applicationEventPublisher;


    public void onPasswordChanged(String password) {
        loginModel.setPasswordHashFragment(calculateChecksum(password));

        loginModel.setCaseRequirementFulfilled(!password.toLowerCase().equals(password));
        loginModel.setComplexityRequirementFulfilled(!StringUtils.isAlphanumeric(password));
        loginModel.setLengthRequirementFulfilled(password.length() >= 12);
        loginModel.setNotCompromisedRequirementFulfilled(true);
    }

    public void onPasswordSubmitted(String password) {
        var requirementsFulfilled = Stream.of(
                loginModel.getCaseRequirementFulfilled().get(),
                loginModel.getComplexityRequirementFulfilled().get(),
                loginModel.getLengthRequirementFulfilled().get(),
                loginModel.getNotCompromisedRequirementFulfilled().get()
        ).allMatch(requirement -> requirement);
        if (requirementsFulfilled) {
            applicationEventPublisher.publishEvent(
                    StageManager.DisplayManagerViewEvent.builder()
                            .masterKey(password)// TODO set MasterKey
                            .passwordChecksum(calculateChecksum(password))
                            .build()
            );
        }
    }

    private String calculateChecksum(String password) {
        return loginService.calculateHash(password).substring(70);
    }
}
