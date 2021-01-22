package telran.logs.bugs.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import telran.logs.bugs.dto.LogDto;

public class ValidLogDtoValidator implements ConstraintValidator<ValidLogDto, LogDto> {

    @Override
    public boolean isValid(LogDto logDto, ConstraintValidatorContext context) {
	return logDto.dateTime != null;
    }

}
