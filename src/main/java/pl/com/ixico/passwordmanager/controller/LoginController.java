package pl.com.ixico.passwordmanager.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.context.StageContext;
import pl.com.ixico.passwordmanager.model.LoginModel;
import pl.com.ixico.passwordmanager.service.LoginService;
import pl.com.ixico.passwordmanager.view.ManagerView;

@Component
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final LoginModel loginModel;

    private final ManagerView managerView;


    public void onPasswordChanged(String password) {
        var hash = loginService.calculateHash(password);
        loginModel.setPasswordHashFragment(hash.substring(71));

        loginModel.setCaseRequirementFulfilled(!password.toLowerCase().equals(password));
        loginModel.setComplexityRequirementFulfilled(!StringUtils.isAlphanumeric(password));
        loginModel.setLengthRequirementFulfilled(password.length() >= 12);
        loginModel.setNotCompromisedRequirementFulfilled(true);
    }

    public void onPasswordSubmitted(String password) {
        System.out.println(1);
        if (password.length() >= 12) {
            System.out.println(2);
            StageContext.changeView(managerView);
        }
    }
}
