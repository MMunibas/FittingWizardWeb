package ch.unibas.fitting.shared.workflows.gaussian.fit;

import ch.unibas.fitting.shared.fitting.*;
import ch.unibas.fitting.shared.tools.AtomTypeId;
import com.google.gson.JsonObject;
import io.vavr.collection.List;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateFit {

    public Fit createFit(int fitId,
                          int rank,
                          JsonObject json,
                          InitialQ00 initialQ00,
                          List<String> molecleNames) {

        double rmse = json.get("mtp_fit_RMSE").getAsDouble();
        var results = json.get("mtp_fit_results").getAsJsonObject();
        List<OutputAtomType> outputAtomTypes = parseValues(results);

        return new Fit(fitId,
                rmse,
                rank,
                createFitResults(molecleNames, outputAtomTypes, initialQ00),
                DateTime.now());
    }

    public List<OutputAtomType> parseValues(JsonObject json) {
        List<ChargeValue> parsedChargeLines = parseFile(json);
        List<OutputAtomType> types = createTypes(parsedChargeLines);

        return types;
    }

    private List<ChargeValue> parseFile(JsonObject json) {
        var charges = List.<ChargeValue>empty();

        for (var b : json.entrySet()) {
            var parts = b.getKey().trim().split("_|\\s+");

            charges = charges.append(new ChargeValue(new AtomTypeId(parts[0]), parts[1], b.getValue().getAsDouble()));
        }

        return charges;
    }

    private List<OutputAtomType> createTypes(List<ChargeValue> chargeLines) {
        HashMap<AtomTypeId, OutputAtomType> typeValues = new HashMap<>();
        for (ChargeValue chargeLine : chargeLines) {
            OutputAtomType atomType = typeValues.get(chargeLine.getAtomTypeId());

            ChargeValue chargeValue = new ChargeValue(chargeLine.getAtomTypeId(), chargeLine.getType(), chargeLine.getValue());
            if (atomType == null) {
                atomType = new OutputAtomType(chargeLine.getAtomTypeId(),
                        List.of(chargeValue));
                typeValues.put(chargeLine.getAtomTypeId(), atomType);
            } else {
                atomType.add(chargeValue);
            }
        }
        return List.ofAll(typeValues.values());
    }

    private List<FitResult> createFitResults(
            List<String> moleculeNames,
            List<OutputAtomType> outputAtomTypes,
            InitialQ00 initialValues) {
        ArrayList<FitResult> results = new ArrayList<>();
        for (OutputAtomType outputAtomType : outputAtomTypes) {
            AtomTypeId atomTypeId = outputAtomType.getId();

            double initialQ = getInialQ00(initialValues, atomTypeId);

            FitResult fitResult = new FitResult(atomTypeId,
                    moleculeNames,
                    initialQ,
                    outputAtomType);
            results.add(fitResult);
        }

        return List.ofAll(results);
    }

    private Double getInialQ00(InitialQ00 initialValues, AtomTypeId atomTypeId) {
        for (ChargeValue initialValue : initialValues.getChargeValues()) {
            if (initialValue.getAtomTypeId().equals(atomTypeId))
                return initialValue.getValue();
        }
        throw new RuntimeException("Missing inital Q for atom type " + atomTypeId.getName());
    }
}
