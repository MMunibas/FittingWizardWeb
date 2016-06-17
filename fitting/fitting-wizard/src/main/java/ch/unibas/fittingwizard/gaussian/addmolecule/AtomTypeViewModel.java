package ch.unibas.fittingwizard.gaussian.addmolecule;

import ch.unibas.fitting.shared.molecules.AtomType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.log4j.Logger;

/**
 * Represents a group of all atom types within the same molecule.
 */
public class AtomTypeViewModel {

    private static final Logger logger = Logger.getLogger(AtomTypeViewModel.class);

    private final AtomType atomType;

    private final StringProperty userCharge = new SimpleStringProperty();

    public AtomTypeViewModel(AtomType atomType) {
        this.atomType = atomType;
        String value = atomType.getUserQ00() == null ? "" : String.valueOf(atomType.getUserQ00());
        userCharge.setValue(value);
    }

    public String getName() {
        return atomType.getId().getName();
    }

    public AtomType getAtomType() {
        return atomType;
    }

    public String getUserCharge() {
        return userCharge.getValue();
    }

    public StringProperty userChargeProperty() {
        return userCharge;
    }

    public void setUserCharge(String newCharge) {
        logger.debug(String.format("Setting user charge for type %s to %s.", getName(), newCharge));

        Double parsedCharge = null;
        boolean isEmpty = newCharge == null || newCharge.isEmpty();
        if (!isEmpty) {
            parsedCharge = Double.parseDouble(newCharge);
            newCharge = String.valueOf(parsedCharge);
        }
        atomType.setUserQ0(parsedCharge);
        userCharge.setValue(newCharge);
    }
}
