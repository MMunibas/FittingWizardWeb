package ch.unibas.fitting.web.application.algorithms.ljfit;

import ch.unibas.fitting.web.application.algorithms.mtp.Fit;
import ch.unibas.fitting.web.application.algorithms.mtp.FitResult;
import ch.unibas.fitting.web.application.algorithms.mtp.InitialQ00;
import ch.unibas.fitting.web.application.algorithms.mtp.OutputAtomType;
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

            double initialQ = initialValues.findQ00(atomTypeId);

            FitResult fitResult = new FitResult(atomTypeId,
                    moleculeNames,
                    initialQ,
                    outputAtomType);
            results.add(fitResult);
        }

        return List.ofAll(results);
    }
}
