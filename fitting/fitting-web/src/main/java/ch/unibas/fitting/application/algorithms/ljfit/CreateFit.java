package ch.unibas.fitting.application.algorithms.ljfit;

import ch.unibas.fitting.application.algorithms.mtpfit.Fit;
import ch.unibas.fitting.application.algorithms.mtpfit.FitResult;
import ch.unibas.fitting.application.algorithms.mtpfit.InitialQ00;
import ch.unibas.fitting.application.algorithms.mtpfit.OutputAtomType;
import io.vavr.collection.List;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class CreateFit {

    public Fit createFit(String mdcmXyzFile,
                         int fitId,
                         int rank,
                         double rmse,
                         List<OutputAtomType> atomTypes,
                         InitialQ00 initialQ00,
                         List<String> molecleNames) {

        return new Fit(fitId,
                rmse,
                rank,
                createFitResults(mdcmXyzFile, String.valueOf(fitId), molecleNames, atomTypes, initialQ00),
                DateTime.now());
    }

    private List<FitResult> createFitResults(
            String mdcmXyzFile,
            String mdcmFitNum,
            List<String> moleculeNames,
            List<OutputAtomType> outputAtomTypes,
            InitialQ00 initialValues) {
        ArrayList<FitResult> results = new ArrayList<>();
        for (OutputAtomType outputAtomType : outputAtomTypes) {
            String atomTypeId = outputAtomType.getAtomType();

            double initialQ = 0.0; //initialValues.findQ00(atomTypeId);

            FitResult fitResult = new FitResult(mdcmXyzFile,
                    mdcmFitNum,
                    atomTypeId,
                    moleculeNames,
                    initialQ,
                    outputAtomType);
            results.add(fitResult);
        }

        return List.ofAll(results);
    }
}
