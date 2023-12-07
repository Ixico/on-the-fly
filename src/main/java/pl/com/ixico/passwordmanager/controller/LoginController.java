package pl.com.ixico.passwordmanager.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.service.LoginService;
import pl.com.ixico.passwordmanager.service.PasswordService;
import pl.com.ixico.passwordmanager.service.PasswordValidationService;
import pl.com.ixico.passwordmanager.view.ManagerView;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final LoginModel loginModel;

    private final ManagerView managerView;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final PasswordService passwordService;

    private final PasswordValidationService passwordValidationService;

    private Thread calculatingThread;


    public void onPasswordChanged(String password) {
        loginModel.setCaseRequirementFulfilled(passwordValidationService.caseRequirementFulfilled(password));
        loginModel.setComplexityRequirementFulfilled(passwordValidationService.complexityRequirementFulfilled(password));
        loginModel.setLengthRequirementFulfilled(passwordValidationService.lengthRequirementFulfilled(password));
        loginModel.setNoTrivialSequencesRequirementFulfilled(passwordValidationService.noTrivialSequencesRequirementFulfilled(password));
        if (areRequirementsFulfilled()) {
            loginModel.setPasswordHashFragment(passwordService.calculateChecksum(password));
        } else {
            loginModel.setPasswordHashFragment("");
        }
    }

    public boolean areRequirementsFulfilled() {
        return Stream.of(
                loginModel.getCaseRequirementFulfilled().get(),
                loginModel.getComplexityRequirementFulfilled().get(),
                loginModel.getLengthRequirementFulfilled().get(),
                loginModel.getNoTrivialSequencesRequirementFulfilled().get()
        ).allMatch(requirement -> requirement);
    }

    public void onPasswordSubmitted(String password) {
        calculatingThread = new Thread(passwordService.hashMasterPassword(password));
        calculatingThread.start();
    }

    private String calculateChecksum(String password) {
        return passwordService.calculateChecksum(password);
    }

    public void onCancelButtonPressed() {
        calculatingThread.interrupt();
    }
}
