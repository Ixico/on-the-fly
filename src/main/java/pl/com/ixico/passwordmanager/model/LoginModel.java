package pl.com.ixico.passwordmanager.model;

import javafx.beans.property.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LoginModel {

    private final StringProperty passwordHashFragment;

    private final BooleanProperty lengthRequirementFulfilled;

    private final FloatProperty passwordStrength;


    public LoginModel() {
        this.passwordHashFragment = new SimpleStringProperty();
        this.lengthRequirementFulfilled = new SimpleBooleanProperty(false);
        this.passwordStrength = new SimpleFloatProperty(0);
    }

    public void setPasswordHashFragment(String value) {
        passwordHashFragment.set(value);
    }

    public void setLengthRequirementFulfilled(boolean fulfilled) {
        lengthRequirementFulfilled.set(fulfilled);
    }


    public void setPasswordStrength(Float passwordStrength) {
        this.passwordStrength.set(passwordStrength);
    }


    public void clear() {
        passwordHashFragment.set("");
        lengthRequirementFulfilled.set(false);
    }

}
