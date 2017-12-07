package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.charges.ChargeTypes;
import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitResult;
import ch.unibas.fitting.shared.presentation.gaussian.ColorCoder;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * Created by tschmidt on 17.06.2016.
 */
public class FitResultViewModel {
    private final String name;
    private HashMap<String, FitValue> values = new HashMap<>();

    public FitResultViewModel(
            ColorCoder colorCoder,
            Fit fit,
            FitResult fr) {
        this.name = fr.getAtomTypeName();
        fr.getChargeValues().forEach(chargeValue -> {
            values.put(chargeValue.getType().toLowerCase(), new FitValue(colorCoder, fit, fr, chargeValue));
        });
    }

    public FitValue getFitValueFor(String chargeType) {
        return values.get(chargeType.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public class FitValue {
        private double value;
        private String color;
        public FitValue(ColorCoder colorCoder, Fit fit, FitResult result, ChargeValue value) {
            this.value = value.getValue();
            Color col = getColor(colorCoder, value.getType(), fit, result);
            color = toRGBCode(col);
        }

        public String getColor() {
            return color;
        }

        public String getValue() {
            return getFormattedValue(value);
        }

        private String getFormattedValue(Double value) {
            String formatted = value == null ? "" : String.format("%7.4f", value);
            return formatted;
        }

        private Color getColor(ColorCoder coder, String chargeType, Fit fit, FitResult fitResult) {
            Color color = null;
            Double value = null;
            if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
                value = fitResult.getAbsDeviationOfQ();
            } else {
                value = fitResult.getChargeValue(chargeType);
            }
            if (value != null) {
                double min = fit.getAbsoluteMinValue(chargeType);
                double max = fit.getAbsoluteMaxValue(chargeType);
                color = coder.getColor(min, max, Math.abs(value));
            }

            return color;
        }

        public String toRGBCode( Color color )
        {
            return String.format( "#%02X%02X%02X",
                    (int)( color.getRed() * 255 ),
                    (int)( color.getGreen() * 255 ),
                    (int)( color.getBlue() * 255 ) );
        }
    }
}
