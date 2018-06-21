package ch.unibas.fitting.shared.workflows.gaussian.fit;

import ch.unibas.fitting.shared.fitting.*;
import io.vavr.collection.List;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class CreateFit {

    public Fit createFit(int fitId,
                          int rank,
                          double rmse,
                          List<OutputAtomType> atomTypes,
                          InitialQ00 initialQ00,
                          List<String> molecleNames) {

        return new Fit(fitId,
                rmse,
                rank,
                createFitResults(molecleNames, atomTypes, initialQ00),
                DateTime.now());
    }

    private List<FitResult> createFitResults(
            List<String> moleculeNames,
            List<OutputAtomType> outputAtomTypes,
            InitialQ00 initialValues) {
        ArrayList<FitResult> results = new ArrayList<>();
        for (OutputAtomType outputAtomType : outputAtomTypes) {
            String atomTypeId = outputAtomType.getAtomType();

            double initialQ = getInialQ00(initialValues, atomTypeId);

            FitResult fitResult = new FitResult(atomTypeId,
                    moleculeNames,
                    initialQ,
                    outputAtomType);
            results.add(fitResult);
        }

        return List.ofAll(results);
    }

    private Double getInialQ00(InitialQ00 initialValues, String atomTypeId) {
        for (ChargeValue initialValue : initialValues.getChargeValues()) {
            if (initialValue.getAtomType().equals(atomTypeId))
                return initialValue.getValue();
        }
        throw new RuntimeException("Missing inital Q for atom type " + atomTypeId);
    }
}
