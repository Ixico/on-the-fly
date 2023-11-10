package pl.com.ixico.passwordmanager.service;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final Argon2PasswordEncoder encoder;

    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;

    public LoginService() {
        this.pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("", 0, 1, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        this.encoder = new Argon2PasswordEncoder(0, 32, 1, 4096, 1);
    }

    public String calculateHash(String password) {
        return encoder.encode(password);
    }

    public String test() {
        return pbkdf2PasswordEncoder.encode("xd");
    }

}
