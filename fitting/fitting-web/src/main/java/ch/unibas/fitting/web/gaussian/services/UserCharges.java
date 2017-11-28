package ch.unibas.fitting.web.gaussian.services;

import io.vavr.collection.List;

public class UserCharges {
    public final String moleculeName;
    public final List<AtomCharge> charges;

    public UserCharges(
            String moleculeName,
            List<AtomCharge> charges) {
        this.moleculeName = moleculeName;
        this.charges = charges;
    }
}
