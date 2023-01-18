package tr.unvercanunlu.fxmarket.validation.annotation;

import tr.unvercanunlu.fxmarket.validation.validator.CurrencyCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyCodeValidator.class)
public @interface CurrencyCode {

    String message() default "Currency is not available.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}