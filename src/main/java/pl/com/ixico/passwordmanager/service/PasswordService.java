package pl.com.ixico.passwordmanager.service;

import javafx.application.Platform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.com.ixico.passwordmanager.stage.StageManager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class PasswordService {

    private static final String FIXED_SALT = "79a112db85fe49c4b9f3efad4ccdff56";

    private final SecretKeyFactory secretKeyFactory;

    private final MessageDigest messageDigest;

    private final ApplicationEventPublisher applicationEventPublisher;

    @SneakyThrows
    public Runnable hashMasterPassword(String masterPassword) {
        return () -> {
            var event = StageManager.DisplayManagerViewEvent.builder()
                    .masterKey(calculateMasterKey(masterPassword))
                    .passwordChecksum(calculateChecksum(masterPassword))
                    .build();
            if (Thread.interrupted()) {
                return;
            }
            Platform.runLater(() -> applicationEventPublisher.publishEvent(event));
        };
    }

    @SneakyThrows
    public String calculateChecksum(String masterPassword) {
        var spec = new PBEKeySpec(masterPassword.toCharArray(), HexFormat.of().parseHex(FIXED_SALT), 1_000, 16);
        var output = secretKeyFactory.generateSecret(spec).getEncoded();
        return HexFormat.of().formatHex(output);
    }

    @SneakyThrows
    public String calculatePassword(String masterKey, String domain) {
        var domainHash = messageDigest.digest(domain.getBytes(StandardCharsets.UTF_8));
        var spec = new PBEKeySpec(masterKey.toCharArray(), domainHash, 1_000, 144);
        return new String(Base64.getEncoder().encode(secretKeyFactory.generateSecret(spec).getEncoded()));
    }

    @SneakyThrows
    private String calculateMasterKey(String masterPassword) {
        var spec = new PBEKeySpec(masterPassword.toCharArray(), HexFormat.of().parseHex(FIXED_SALT), 1_000_000, 144);
        return new String(Base64.getEncoder().encode(secretKeyFactory.generateSecret(spec).getEncoded()));
    }
}
