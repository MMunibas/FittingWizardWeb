package ch.unibas.fitting.web.ljfit;

import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class UiElementFactory {
    public static NumberTextField<Double> createLambdaValueField(String id, IModel<Double> model) {
        NumberTextField<Double> lambdaField = new NumberTextField<>(id, model);
        lambdaField.setRequired(true);
        lambdaField.setStep(0.05);
        lambdaField.setMinimum(0.0);
        lambdaField.setMaximum(1.0);
        lambdaField.add((IValidator<Double>) validatable -> {
            Double value = validatable.getValue();
            if (value == null || value <= 0.0 || value > 1.0)
                validatable.error(new ValidationError("Lambda spacing: 0 < λ <= 1.0"));
        });
        lambdaField.setType(Double.class);
        return lambdaField;
    }
}
