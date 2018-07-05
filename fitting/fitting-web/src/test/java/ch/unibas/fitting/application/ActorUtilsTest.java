package ch.unibas.fitting.application;

import ch.unibas.fitting.application.calculation.ActorUtils;
import org.junit.Assert;
import org.junit.Test;

public class ActorUtilsTest {
    @Test
    public void uniqueActorName() {
        var id = ActorUtils.generateUniqueId();
        var id2 = ActorUtils.generateUniqueId();

        Assert.assertNotEquals(id, id2);
    }
}
