package ch.unibas.fitting.shared.directories;

import io.vavr.Tuple3;
import org.junit.Assert;
import org.junit.Test;

import static ch.unibas.fitting.shared.directories.LjFitSessionDir.parseDirName;

public class LjFitSessionDirTests {
    @Test
    public void regexMatches() {
        Tuple3<Double, Double, Long> result = LjFitSessionDir.parseDirName("eps0.80_sig1.00_1510506963");
        Assert.assertEquals((Double)0.80, result._1);
        Assert.assertEquals((Double)1.00, result._2);
        Assert.assertEquals((Long)1510506963l, result._3);
    }
}
