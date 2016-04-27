package net.indaba.paypal.service.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public interface ExpressCheckoutService {

	/**	 
	 * Function to perform the SetExpressCheckout API call.	(One-Time Payments)	
	 * <ul>
	 * <li>paymentAmount:  	Total value of the shopping cart</li>
	 * <li>returnURL:			The page where buyers return to after they are done with the payment review on PayPal</li>
	 * <li>cancelURL:			The page where buyers return to when they cancel the payment review on PayPal</li>
	 * </ul>
	 * 
	 * @param paymentAmount
	 * @param returnURL
	 * @param cancelURL
	 * @return Returns a HashMap object containing the response from the server.
	 * @throws UnsupportedEncodingException
	 */
	HashMap<String, String> SetExpressCheckoutOneTimePayment( String paymentAmount, String returnURL, String cancelURL) throws Exception;
		
	/**	 
	 * Function to perform the SetExpressCheckout API call.	(Recurring Payments)	
	 * <ul>
	 * <li>paymentAmount:  	Total value of the shopping cart</li>
	 * <li>returnURL:			The page where buyers return to after they are done with the payment review on PayPal</li>
	 * <li>cancelURL:			The page where buyers return to when they cancel the payment review on PayPal</li>
	 * </ul>
	 * 
	 * @param paymentAmount
	 * @param returnURL
	 * @param cancelURL
	 * @return Returns a HashMap object containing the response from the server.
	 * @throws UnsupportedEncodingException
	 */
	HashMap<String, String> SetExpressCheckoutRecurringPayments(String returnURL, String cancelURL) throws Exception;
	
	/**	
	 * Function to perform the GetExpressCheckoutDetails API call.
	 * <ul>
	 * <li>token:  	Token responded by Paypal after <b>ExpressCheckout</b> API call, unique for an order.</li>	
	 * </ul>
	 * 	
	 * @param token
	 * @return Returns a HashMap object containing the response from the server.
	 */
	HashMap<String, String> GetExpressCheckoutDetails(String token) throws Exception;
	
	/**	 
	 * Function to perform the DoExpressCheckoutPayment API call
	 * <ul>
	 * <li>token:  	Token responded by Paypal after <b>ExpressCheckout</b> API call, unique for an order.</li>
	 * <li>payerID:  	uniquely identifies the customer.</li>
	 * <li>finalPaymentAmount:  	Total value of the shopping cart.</li>
	 * <li>serverName:  	Name of the server.</li>
	 * </ul>
	 *	
	 * @param token
	 * @param payerID
	 * @param finalPaymentAmount
	 * @param serverName
	 * @return Returns a HashMap object containing the response from the server.
	 */
	HashMap<String, String> ConfirmPayment( String token, String payerID, String finalPaymentAmount, String serverName) throws Exception;
		
	/**	 
	 * Function to perform the CreateRecurringPaymentsProfile API call
	 * <ul>
	 * <li>token:  	Token responded by Paypal after <b>ExpressCheckout</b> API call, unique for an order.</li>
	 * <li>payerID:  	uniquely identifies the customer.</li>
	 * <li>amount:  	The amount the buyer will pay in a payment period.</li>	
	 * </ul>
	 *	
	 * @param token
	 * @param payerID
	 * @param amount	
	 * @return Returns a HashMap object containing the response from the server.
	 */
	HashMap<String, String> CreateRecurringPaymentsProfile( String token, String payerID, String amount) throws Exception;	
	
	/**	 
	 * Function to perform the GetRecurringPaymentsProfileDetails API call
	 * <ul>	 
	 * <li>profileID:  	uniquely identifies the customer.</li>	
	 * </ul>
	 *		
	 * @param profileID	
	 * @return Returns a HashMap object containing the response from the server.
	 */
	HashMap<String, String> GetRecurringPaymentsProfile( String profileID) throws Exception;	
	
}