package pl.com.ixico.passwordmanager.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKeyFactory;
import java.security.MessageDigest;

@Configuration
public class CryptoConfiguration {

    private static final String PBKDF_ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final String SHA_ALGORITHM = "SHA-256";

    @Bean
    @SneakyThrows
    public SecretKeyFactory secretKeyFactory() {
        return SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
    }

    @Bean
    @SneakyThrows
    public MessageDigest messageDigest() {
        return MessageDigest.getInstance(SHA_ALGORITHM);
    }

}
