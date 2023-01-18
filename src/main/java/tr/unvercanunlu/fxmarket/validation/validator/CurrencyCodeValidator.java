package tr.unvercanunlu.fxmarket.validation.validator;

import tr.unvercanunlu.fxmarket.model.constant.Currency;
import tr.unvercanunlu.fxmarket.validation.annotation.CurrencyCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, String> {

    @Override
    public boolean isValid(String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(Currency.values()).anyMatch(c -> c.getCode().equals(currencyCode));
    }
}
