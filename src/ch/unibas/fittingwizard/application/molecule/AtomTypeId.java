package ch.unibas.fittingwizard.application.molecule;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 11:58
 */
public class AtomTypeId {
    private final String name;

    public AtomTypeId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtomTypeId that = (AtomTypeId) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
