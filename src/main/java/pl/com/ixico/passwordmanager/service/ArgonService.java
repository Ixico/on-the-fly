package pl.com.ixico.passwordmanager.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.util.DigestUtils;

import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
public class ArgonService {

    private final Argon2BytesGenerator generator;

    private static final Argon2Parameters MASTER_KEY_GENERATOR_PARAMETERS = getMasterKeyGeneratorParameters();

    private final MessageDigest messageDigest;

    private static final Integer HASH_LENGTH = 32;

    @SneakyThrows
    public ArgonService() {
        messageDigest = MessageDigest.getInstance("SHA-256");
        generator = new Argon2BytesGenerator();
    }

    public String calculateMasterKey(String password) {
        generator.init(MASTER_KEY_GENERATOR_PARAMETERS);
        return hash(password);
    }

    public String calculatePassword(String masterKey, String domain) {
        var domainHash = messageDigest.digest(stringToBytes(domain));
        generator.init(getPasswordGeneratorParameters(domainHash));
        return hash(masterKey);
    }

    private String hash(String content) {
        var result = new byte[HASH_LENGTH];
        generator.generateBytes(stringToBytes(content), result, 0, result.length);
        return new String(Base64.getEncoder().encode(result));
    }

    private static Argon2Parameters getMasterKeyGeneratorParameters() {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(2)
                .withMemoryAsKB(200000)
                .withParallelism(1)
                .withSalt(new byte[0])
                .build();
    }

    private Argon2Parameters getPasswordGeneratorParameters(byte[] salt) {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(2)
                .withMemoryAsKB(100000)
                .withParallelism(1)
                .withSalt(salt)
                .build();
    }

    private byte[] stringToBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    private String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    private byte[] hexToBytes(String hexString) {
        return HexFormat.of().parseHex(hexString);
    }
}
