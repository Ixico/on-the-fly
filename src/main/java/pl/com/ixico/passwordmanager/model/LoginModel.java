package pl.com.ixico.passwordmanager.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LoginModel {

    private final StringProperty passwordHashFragment;

    private final BooleanProperty lengthRequirementFulfilled;

    private final BooleanProperty caseRequirementFulfilled;

    private final BooleanProperty complexityRequirementFulfilled;

    private final BooleanProperty noTrivialSequencesRequirementFulfilled;


    public LoginModel() {
        this.passwordHashFragment = new SimpleStringProperty();
        this.lengthRequirementFulfilled = new SimpleBooleanProperty(false);
        this.caseRequirementFulfilled = new SimpleBooleanProperty(false);
        this.complexityRequirementFulfilled = new SimpleBooleanProperty(false);
        this.noTrivialSequencesRequirementFulfilled = new SimpleBooleanProperty(false);
    }

    public void setPasswordHashFragment(String value) {
        passwordHashFragment.set(value);
    }

    public void setLengthRequirementFulfilled(boolean fulfilled) {
        lengthRequirementFulfilled.set(fulfilled);
    }

    public void setCaseRequirementFulfilled(boolean fulfilled) {
        caseRequirementFulfilled.set(fulfilled);
    }

    public void setComplexityRequirementFulfilled(boolean fulfilled) {
        complexityRequirementFulfilled.set(fulfilled);
    }

    public void setNoTrivialSequencesRequirementFulfilled(boolean fulfilled) {
        noTrivialSequencesRequirementFulfilled.set(fulfilled);
    }


    public void clear() {
        passwordHashFragment.set("");
        lengthRequirementFulfilled.set(false);
        caseRequirementFulfilled.set(false);
        complexityRequirementFulfilled.set(false);
        noTrivialSequencesRequirementFulfilled.set(false);
    }

}
