package pl.com.ixico.passwordmanager.component;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ApplicationContextHolder {

    private String sessionChecksum = "abdd";

    private Integer sessionTimeSeconds = 30;

}
