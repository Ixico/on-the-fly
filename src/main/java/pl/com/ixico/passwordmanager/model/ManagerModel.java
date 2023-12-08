package pl.com.ixico.passwordmanager.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
@Getter
public class ManagerModel {

    private final SimpleStringProperty sessionExpiration;

    private final FloatProperty sessionExpirationPart;

    private String passwordChecksum;

    private String masterKey;

    public ManagerModel() {
        this.sessionExpiration = new SimpleStringProperty();
        this.sessionExpirationPart = new SimpleFloatProperty();
    }

    public void setSessionExpiration(String sessionExpiration) {
        this.sessionExpiration.set(sessionExpiration);
    }

    public void setSessionExpirationPart(Float sessionExpirationPart) {
        this.sessionExpirationPart.set(sessionExpirationPart);
    }

    public void setPasswordChecksum(String passwordChecksum) {
        this.passwordChecksum = passwordChecksum;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public void clear() {
        sessionExpiration.set("");
        sessionExpirationPart.set(0f);
        passwordChecksum = "";
        masterKey = null;
    }
}
