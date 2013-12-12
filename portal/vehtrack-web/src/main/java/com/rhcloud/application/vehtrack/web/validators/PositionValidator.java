package com.rhcloud.application.vehtrack.web.validators;

import com.rhcloud.application.vehtrack.domain.Position;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Jon Brisbin <jon@jbrisbin.com>
 */
public class PositionValidator implements Validator {

  //private static final Logger LOG = LoggerFactory.getLogger(PositionValidator.class);

  @Override public boolean supports(Class<?> clazz) {
    return ClassUtils.isAssignable(clazz, Position.class);
  }

  @Override public void validate(Object target, Errors errors) {
    Position p = (Position) target;
    //LOG.debug("validating Position " + p);
    ValidationUtils.rejectIfEmpty(errors, "name", "field.name.required", "Field 'name' cannot be blank.");
  }

}
