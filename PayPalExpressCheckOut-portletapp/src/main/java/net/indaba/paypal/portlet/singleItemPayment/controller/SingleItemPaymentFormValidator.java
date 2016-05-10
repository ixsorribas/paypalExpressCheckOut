package net.indaba.paypal.portlet.singleItemPayment.controller;

import net.indaba.paypal.service.form.SingleItemPaymentForm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("singleItemPaymentFormValidator")
public class SingleItemPaymentFormValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return SingleItemPaymentFormValidator.class.isAssignableFrom(clazz);
	}

	@Override
	public
	void validate(Object target, Errors errors) {		
		
		SingleItemPaymentForm ppf = (SingleItemPaymentForm)target;
		String valor = ppf.getAmount();
		Boolean campoInformado = true;
		
		if(StringUtils.isEmpty(valor)){
			errors.rejectValue("amount", "error.campoObligatorio");
			campoInformado = false;
		}

		if(campoInformado){

			try{				
				valor = valor.replace(',', '.');
				valor = String.format("%.02f", Float.parseFloat(valor));
				valor = valor.replace(',', '.');
				
			}catch(Exception e){
				errors.rejectValue("amount", "error.debeSerNumerico");
			}		
		}
		
	}
}
