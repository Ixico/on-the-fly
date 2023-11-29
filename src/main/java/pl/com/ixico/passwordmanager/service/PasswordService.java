package pl.com.ixico.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

@RequiredArgsConstructor
public class PasswordService {

    private static final String FIXED_SALT = "79a112db85fe49c4b9f3efad4ccdff56";

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private final SecretKeyFactory secretKeyFactory;

    @SneakyThrows
    public String hashMasterPassword(String masterPassword) {
        var spec = new PBEKeySpec(masterPassword.toCharArray(), HexFormat.of().parseHex(FIXED_SALT), 5_000_000, 128);
        var output = secretKeyFactory.generateSecret(spec).getEncoded();
        return new String(Base64.getEncoder().encode(output));
    }
}
