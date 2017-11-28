package ch.unibas.fitting.shared.workflows.gaussian.fit;

import ch.unibas.fitting.shared.charges.ChargesFileParser;
import ch.unibas.fitting.shared.fitting.*;
import ch.unibas.fitting.shared.molecules.AtomTypeId;
import ch.unibas.fitting.shared.tools.FitOutputParser;
import io.vavr.collection.List;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

public class CreateFit {

    private ChargesFileParser chargesFileParser;
    private FitOutputParser fitOutputParser;

    @Inject
    public CreateFit(ChargesFileParser chargesFileParser, FitOutputParser fitOutputParser) {
        this.chargesFileParser = chargesFileParser;
        this.fitOutputParser = fitOutputParser;
    }

    public Fit createFit(int fitId,
                          int rank,
                          File resultsFile,
                          File outputFile,
                          InitialQ00 initialQ00,
                          List<String> molecleNames) {

        double rmse = fitOutputParser.parseRmseValue(outputFile);
        List<OutputAtomType> outputAtomTypes = chargesFileParser.parseOutputFile(
                resultsFile);

        return new Fit(fitId,
                rmse,
                rank,
                createFitResults(molecleNames, outputAtomTypes, initialQ00),
                DateTime.now());
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
