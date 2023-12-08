package pl.com.ixico.passwordmanager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PasswordManagerApplication {

    public static void main(String[] args) {
        Application.launch(DesktopApplication.class, args);
    }

}
