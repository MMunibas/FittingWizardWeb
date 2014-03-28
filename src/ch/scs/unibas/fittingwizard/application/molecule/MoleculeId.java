package ch.scs.unibas.fittingwizard.application.molecule;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:14
 */
public class MoleculeId {
    private final String name;

    public MoleculeId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoleculeId that = (MoleculeId) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
