package net.indaba.paypal.service.form;

import java.io.Serializable;

public class SingleItemPaymentForm implements Serializable{

	private static final long serialVersionUID = 1L;

	private String amount;

	public SingleItemPaymentForm(){}
	
	public SingleItemPaymentForm(String amount) {		
		this.amount = amount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
}
