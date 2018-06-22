package ch.unibas.fitting.application;

import ch.unibas.fitting.application.base.JsonUserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class JsonUserRepositoryTest {

    private DataRepo _repo;

    @Before
    public void setup() {
        _repo = new DataRepo();
    }


    public void saveAndLoadReturnsSameStructure() {

        DataStructure s = new DataStructure(2, "test");

        _repo.save("mhelmer", s);

        DataStructure loaded = _repo.loadAll("mhelmer").get(0);

        Assert.assertEquals(s.getName(), loaded.getName());
        Assert.assertEquals(s.getValue(), loaded.getValue());
    }

    public static class DataRepo extends JsonUserRepository<DataStructure> {}

    public static class DataStructure {
        private int value;
        private String name;

        public DataStructure(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
