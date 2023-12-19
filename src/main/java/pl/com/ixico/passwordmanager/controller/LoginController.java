package pl.com.ixico.passwordmanager.controller;

import lombok.RequiredArgsConstructor;
import me.gosimple.nbvcxz.Nbvcxz;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.service.PasswordService;

@Component
@RequiredArgsConstructor
public class LoginController {

    private final LoginModel loginModel;

    private final PasswordService passwordService;

    private Thread calculatingThread;

    private final Nbvcxz checker = new Nbvcxz();

    private static final Integer PASSWORD_MIN_LENGTH = 16;


    public void onPasswordChanged(String password) {
        loginModel.setPasswordStrength(checker.estimate(password).getEntropy().floatValue());
        var lengthRequirementFulfilled = password.length() > PASSWORD_MIN_LENGTH;
        loginModel.setLengthRequirementFulfilled(lengthRequirementFulfilled);
        if (lengthRequirementFulfilled) {
            loginModel.setPasswordHashFragment(passwordService.calculateChecksum(password));
        } else {
            loginModel.setPasswordHashFragment("");
        }
    }

    public boolean isLengthRequirementFulfilled() {
        return loginModel.getLengthRequirementFulfilled().get();
    }

    public void onPasswordSubmitted(String password) {
        calculatingThread = new Thread(passwordService.hashMasterPassword(password));
        calculatingThread.start();
    }

    public void onCancelButtonPressed() {
        calculatingThread.interrupt();
    }
}
