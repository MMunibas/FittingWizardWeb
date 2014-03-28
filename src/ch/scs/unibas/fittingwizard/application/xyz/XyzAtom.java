package ch.scs.unibas.fittingwizard.application.xyz;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 17:48
 */
public class XyzAtom {
    private final String name;
    private final int index;
    private final double x;
    private final double y;
    private final double z;

    public XyzAtom(String name, int index, double x, double y, double z) {

        this.name = name;
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }
    
    public int getIndex() {
    	return index;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "XyzAtom{" +
                "name='" + name + '\'' +
                "index='" + index + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
