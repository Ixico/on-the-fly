package pl.com.ixico.passwordmanager.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import java.util.HexFormat;

@Configuration
public class CryptoConfiguration {

    private static final String FIXED_SALT = "79a112db85fe49c4b9f3efad4ccdff56";

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    @Bean
    @SneakyThrows
    public SecretKeyFactory secretKeyFactory() {
        return SecretKeyFactory.getInstance(ALGORITHM);
    }
}
