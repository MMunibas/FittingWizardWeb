package ch.unibas.fitting.application.calculation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class SerializedParameter implements Serializable {
    public String type;
    public String key;
    public Object value;
    public SerializedParameter(){
        type = "Double";
        key = "";
        value = null;
    }
    public SerializedParameter(String key, Object value){
        type = value.getClass().getSimpleName();
        this.key = key;
        this.value = value;
    }
}
