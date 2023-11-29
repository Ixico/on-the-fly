package pl.com.ixico.passwordmanager;

import pl.com.ixico.passwordmanager.service.ArgonService;
import pl.com.ixico.passwordmanager.service.LoginService;
import pl.com.ixico.passwordmanager.service.PasswordService;

import java.time.Duration;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
//        var service = new ArgonService();
//        var masterKey = service.calculateMasterKey("xd");
//        System.out.println(masterKey);
//        System.out.println(service.calculatePassword(masterKey, "facebook"));
//        var start = Instant.now();
//        System.out.println(new PasswordService().hash("xd"));
//        var end = Instant.now();
//        System.out.println(Duration.between(start, end).toMillis());
        System.out.println(new LoginService().test());
    }
}
