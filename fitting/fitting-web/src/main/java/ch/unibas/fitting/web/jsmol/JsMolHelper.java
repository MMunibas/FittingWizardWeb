package ch.unibas.fitting.web.jsmol;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

/**
 * Created by tschmidt on 30.06.2016.
 */
public class JsMolHelper {

    public static String getXyzUrl(String username, String moleculeName) {
        return "/data/" +
                username +
                "/xyz_files/" +
                moleculeName +
                ".xyz";
    }

    public static void addAtomsHighlightingMouseEvent(Item<?> item, int index) {
        addAtomsHighlightingMouseEvent(item, new int[]{index});
    }

    public static void addAtomsHighlightingMouseEvent(Item<?> item, int[] indices) {
        String atomIdxString = createAtomSelectionString(indices);
        item.add(new AttributeAppender("onmouseover", new Model("Jmol.script(jmolApplet0,\"select " + atomIdxString + "\")"),";"));
        item.add(new AttributeAppender("onmouseout", new Model("Jmol.script(jmolApplet0,\"select none\")"), ";"));
    }

    private static String createAtomSelectionString(int[] indices) {
        String atomIdxString = "";
        for (int i = 0; i < indices.length; i++) {
            atomIdxString += "atomIndex=" + indices[i];
            if (i < indices.length - 1) {
                atomIdxString += " OR ";
            }
        }
        return atomIdxString;
    }
}
