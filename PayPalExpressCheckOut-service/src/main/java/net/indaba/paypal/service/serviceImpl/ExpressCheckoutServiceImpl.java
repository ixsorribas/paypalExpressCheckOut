package net.indaba.paypal.service.serviceImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.indaba.paypal.service.service.ExpressCheckoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service(value="payPalExpressCheckoutService")
@PropertySource("classpath:payPalExpressCheckout/config.properties")
public class ExpressCheckoutServiceImpl implements ExpressCheckoutService {
		
	@Autowired
	private Environment env;
	
	@Override
	public HashMap<String, String> SetExpressCheckoutOneTimePayment(String paymentAmount, String returnURL, String cancelURL) throws Exception {
		
		String currencyCodeType = "EUR";
		String paymentType = "Sale";
		
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&ReturnUrl=" + URLEncoder.encode( returnURL, "UTF-8" ) + "&CANCELURL=" + URLEncoder.encode( cancelURL, "UTF-8" ) + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType;
		
		// ***************************************************************************
		// Identifying Items as Digital Good
		String itemAmount = paymentAmount; // total amount of all items in the order
		
		// Único elemento que vamos a comprar
		String itemQty0 = "1";
		String itemName0 = "Kitty Antics";
		String itemCategory0 = "Digital";				
		String itemAmt0 = paymentAmount;
		
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_ITEMAMT=" + itemAmount + "&L_PAYMENTREQUEST_0_ITEMCATEGORY0=" + itemCategory0 + "&L_PAYMENTREQUEST_0_NAME0=" + itemName0 + "&L_PAYMENTREQUEST_0_QTY0=" + itemQty0 + "&L_PAYMENTREQUEST_0_AMT0=" + itemAmt0 + "&REQCONFIRMSHIPPING=0" + "&NOSHIPPING=0";
		// ***************************************************************************
				
		/*
    	Make the call to PayPal to get the Express Checkout token
    	If the API call succeded, then redirect the buyer to PayPal
    	to begin to authorize payment.  If an error occured, show the
    	resulting errors
		 */
		HashMap<String, String> nvp = httpcall("SetExpressCheckout", nvpstr);
		return nvp;
	}

	@Override
	public HashMap<String, String> SetExpressCheckoutRecurringPayments(String returnURL, String cancelURL) throws Exception {
		
		String currencyCodeType = "EUR"; // Realmente da igual, ponemos el 'currencyCodeType' en el RecurringPaymentsProfile
		
		String billingType = "RecurringPayments";
		String billingAgreementDescription = "Lost And Found subscription";
		
		String nvpstr = "&L_BILLINGTYPE0=" + billingType + "&L_BILLINGAGREEMENTDESCRIPTION0=" + billingAgreementDescription + "&ReturnUrl=" + URLEncoder.encode( returnURL, "UTF-8" ) + "&CANCELURL=" + URLEncoder.encode( cancelURL, "UTF-8" ) + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType;
				
		/*
    	Make the call to PayPal to get the Express Checkout token
    	If the API call succeded, then redirect the buyer to PayPal
    	to begin to authorize payment.  If an error occured, show the
    	resulting errors
		 */
		HashMap<String, String> nvp = httpcall("SetExpressCheckout", nvpstr);
		return nvp;
	}
	
	@Override
	public HashMap<String, String> GetExpressCheckoutDetails(String token) throws Exception {
		
		String nvpstr= "&TOKEN=" + token;

		/*
    	Make the API call and store the results in an array.  If the
    	call was a success, show the authorization details, and provide
    	an action to complete the payment.  If failed, show the error
		 */
		HashMap<String, String> nvp = httpcall("GetExpressCheckoutDetails", nvpstr);		
		return nvp;
	}

	@Override
	public HashMap<String, String> ConfirmPayment(String token, String payerID, String finalPaymentAmount, String serverName) throws Exception{
		
		String currencyCodeType = "EUR";
		String paymentType = "Sale";

		String nvpstr  = "&TOKEN=" + token + "&PAYERID=" + payerID + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + finalPaymentAmount;
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&IPADDRESS=" + serverName;

		// ***************************************************************************
		// Identifying Items as Digital Good

		//		Note: Even though you passed values for these parameters in the call to SetExpressCheckout, 
		//		you must pass them again in the call to DoExpressCheckoutPayment. 
		//		Otherwise, you do not receive the discount rate for digital goods.
		
		String itemAmount = finalPaymentAmount; // total amount of all items in the order
			
		// Único elemento que vamos a comprar 
		String itemCategory0 = "Digital";
		String itemName0 = "Kitty Antics";
		String itemQty0 = "1";
		String itemAmt0 = finalPaymentAmount;
		
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_ITEMAMT=" + itemAmount + "&L_PAYMENTREQUEST_0_ITEMCATEGORY0=" + itemCategory0 + "&L_PAYMENTREQUEST_0_NAME0=" + itemName0 + "&L_PAYMENTREQUEST_0_QTY0=" + itemQty0 + "&L_PAYMENTREQUEST_0_AMT0=" + itemAmt0;
		// ***************************************************************************
				
		/*
    	Make the call to PayPal to finalize payment
    	If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = httpcall("DoExpressCheckoutPayment", nvpstr);
		return nvp;
	}
		
	@Override
	public HashMap<String, String> CreateRecurringPaymentsProfile(String token, String payerID, String amount)	throws Exception {
		
		ZonedDateTime profileStartDate = ZonedDateTime.now( ZoneOffset.UTC );
		
		String billingAgreementDescription = "Lost And Found subscription";
		
		String billingPeriod = "Month";
		String billingFrecuency = "1";		
		String maxFailedPayments = "2";	

		String currencyCodeType = "EUR";
		String countryCode = "ES";		
		
		String nvpstr  = "&TOKEN=" + token + "&PAYERID=" + payerID + "&AMT=" + amount + "&PROFILESTARTDATE=" + profileStartDate + "&DESC=" + billingAgreementDescription;
		nvpstr = nvpstr + "&BILLINGPERIOD=" + billingPeriod + "&BILLINGFREQUENCY=" + billingFrecuency + "&CURRENCYCODE=" + currencyCodeType + "&COUNTRYCODE=" + countryCode + "&MAXFAILEDPAYMENTS=" + maxFailedPayments;
		
		// ***************************************************************************
		// Identifying Items as Digital Good
		
		// Único elemento que vamos a comprar
		String itemQty0 = "1";
		String itemName0 = "Kitty Antics";
		String itemCategory0 = "Digital";				
		String itemAmt0 = amount;
		
		nvpstr = nvpstr + "&L_PAYMENTREQUEST_0_ITEMCATEGORY0=" + itemCategory0 + "&L_PAYMENTREQUEST_0_NAME0=" + itemName0 + "&L_PAYMENTREQUEST_0_QTY0=" + itemQty0 + "&L_PAYMENTREQUEST_0_AMT0=" + itemAmt0;
		// ***************************************************************************
				
		/*
    	Make the call to PayPal to create the recurring payments profile
    	If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = httpcall("CreateRecurringPaymentsProfile", nvpstr);
		return nvp;
	}
	
	@Override
	public HashMap<String, String> GetRecurringPaymentsProfile(String profileID) throws Exception {

		String nvpstr= "&PROFILEID=" + profileID;

		/*
    	Make the API call and store the results in an array.  If the
    	call was a success, show the authorization details, and provide
    	an action to complete the payment.  If failed, show the error
		 */
		HashMap<String, String> nvp = httpcall("GetRecurringPaymentsProfileDetails", nvpstr);		
		return nvp;
	}
	
	
	/*********************************************************************************
	 * httpcall: Function to perform the API call to PayPal using API signature
	 * 	@ methodName is name of API  method.
	 * 	@ nvpStr is nvp string.
	 * returns a NVP string containing the response from the server.
	 *********************************************************************************/
	private HashMap<String, String> httpcall( String methodName, String nvpStr) throws Exception{

		// String version = "2.3";
		String agent = "Mozilla/4.0";
		String respText = "";
		HashMap<String, String> nvp = null;
				
		String gv_APIUserName = env.getProperty("gv_APIUserName");
		String gv_APIPassword = env.getProperty("gv_APIPassword");
		String gv_APISignature = env.getProperty("gv_APISignature");
		
		String gv_APIEndpoint = env.getProperty("gv_APIEndpoint");
		String gv_BNCode = env.getProperty("gv_BNCode"); //BN Code is only applicable for partners
		String gv_Version = env.getProperty("gv_Version");
		
		String encodedData = "METHOD=" + methodName + "&VERSION=" + gv_Version + "&PWD=" + gv_APIPassword + "&USER=" + gv_APIUserName + "&SIGNATURE=" + gv_APISignature + nvpStr + "&BUTTONSOURCE=" + gv_BNCode;
				
		URL postURL = new URL( gv_APIEndpoint );
		HttpURLConnection conn = (HttpURLConnection)postURL.openConnection();

		// Set connection parameters. We need to perform input and output,
		// so set both as true.
		conn.setDoInput (true);
		conn.setDoOutput (true);

		// Set the content type we are POSTing. We impersonate it as
		// encoded form data
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty( "User-Agent", agent );
		
		conn.setRequestProperty( "Content-Length", String.valueOf( encodedData.length()) );
		conn.setRequestMethod("POST");

		// get the output stream to POST to.
		DataOutputStream output = new DataOutputStream( conn.getOutputStream());
		output.writeBytes( encodedData );
		output.flush();
		output.close ();

		// Read input from the input stream.		
		int rc = conn.getResponseCode();
		if ( rc != -1){
			BufferedReader is = new BufferedReader(new InputStreamReader( conn.getInputStream()));
			String _line = null;
			while(((_line = is.readLine()) !=null)){
				respText = respText + _line;
			}
			nvp = deformatNVP( respText );
		}
		
		return nvp;
	}

	/*********************************************************************************
	 * deformatNVP: Function to break the NVP string into a HashMap
	 * 	pPayLoad is the NVP string.
	 * returns a HashMap object containing all the name value pairs of the string.
	 * @throws UnsupportedEncodingException 
	 *********************************************************************************/
	private HashMap<String, String> deformatNVP( String pPayload ) throws UnsupportedEncodingException{
		
		HashMap<String, String> nvp = new HashMap<String, String>();
		StringTokenizer stTok = new StringTokenizer( pPayload, "&");
		while (stTok.hasMoreTokens()){
			
			StringTokenizer stInternalTokenizer = new StringTokenizer( stTok.nextToken(), "=");
			if (stInternalTokenizer.countTokens() == 2){
				
				String key = URLDecoder.decode( stInternalTokenizer.nextToken(), "UTF-8");
				String value = URLDecoder.decode( stInternalTokenizer.nextToken(), "UTF-8");
				nvp.put( key.toUpperCase(), value );
			}
		}
		return nvp;
	}
}