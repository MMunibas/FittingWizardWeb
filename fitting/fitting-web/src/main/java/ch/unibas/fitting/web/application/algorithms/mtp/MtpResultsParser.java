package ch.unibas.fitting.web.application.algorithms.mtp;

import ch.unibas.fitting.web.application.calculation.JsonLoader;
import com.google.gson.JsonObject;
import io.vavr.collection.List;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MtpResultsParser {
    public double getRmseValue(JsonObject json) {
        return json.get("mtp_fit_RMSE").getAsDouble();
    }

    public List<ChargeValue> getLpunAtomTypes(File f) {
        var json = loadJson(f);
        return getCharges(json);
    }

    public List<OutputAtomType> getATomTypes(JsonObject json) {
        var charges = getCharges(json);
        return toAtomTypes(charges);
    }

    private JsonObject loadJson(File f) {
        try {
            return JsonLoader.getJsonObject(f);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to load JSON %s", f), e);
        }
    }

    private List<ChargeValue> getCharges(JsonObject root) {

        var results = root.get("mtp_fit_results").getAsJsonObject();

        var charges = List.<ChargeValue>empty();

        int index = 0;
        for (var b : results.entrySet()) {
            var parts = b.getKey().trim().split("_|\\s+");

            var id = parts[0];
            var type = parts[1];
            var value = b.getValue().getAsDouble();
            charges = charges.append(new ChargeValue(id, type, value, index));
            index++;
        }

        return charges;
    }

    private List<OutputAtomType> toAtomTypes(List<ChargeValue> chargeLines) {
        HashMap<String, OutputAtomType> typeValues = new HashMap<>();
        for (ChargeValue chargeLine : chargeLines) {
            OutputAtomType atomType = typeValues.get(chargeLine.getAtomType());
            if (atomType == null) {
                atomType = new OutputAtomType(chargeLine.getAtomType(),
                        List.of(chargeLine));
                typeValues.put(chargeLine.getAtomType(), atomType);
            } else {
                atomType.add(chargeLine);
            }
        }
        return List.ofAll(typeValues.values());
    }
}
