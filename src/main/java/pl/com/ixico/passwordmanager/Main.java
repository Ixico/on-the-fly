package pl.com.ixico.passwordmanager;

import pl.com.ixico.passwordmanager.service.LoginService;

public class Main {

    public static void main(String[] args) {
        var service = new LoginService();
        System.out.println(service.test());
    }
}
