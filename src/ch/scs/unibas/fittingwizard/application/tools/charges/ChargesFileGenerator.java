package ch.scs.unibas.fittingwizard.application.tools.charges;

import ch.scs.unibas.fittingwizard.application.fitting.ChargeValue;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 17:35
 */
public class ChargesFileGenerator {
    private static final Logger logger = Logger.getLogger(ChargesFileGenerator.class);

    public File generate(File destination, String chargesFileName, LinkedHashSet<ChargeValue> chargeValues) {
        destination.mkdir();
        if (destination == null || !destination.isDirectory()) {
            throw new IllegalArgumentException("Invalid destination directory.");
        }
        if (chargeValues == null || chargeValues.size() == 0) {
            throw new IllegalArgumentException("chargeValues are empty.");
        }

        File output = new File(destination, chargesFileName);
        output.delete();

        List<String> content = new ArrayList<>();
        for (ChargeValue chargeLine : chargeValues) {
            String line = String.format("%s_%s %s", chargeLine.getAtomTypeId().getName(),
                    chargeLine.getType(), String.valueOf(chargeLine.getValue()));
            content.add(line);
        }

        try {
            FileUtils.writeLines(output, content);
        } catch (IOException e) {
            throw new RuntimeException("Could not write charges file.");
        }

        return output;
    }
}
