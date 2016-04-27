package net.indaba.paypal.portlet.singleItemPayment.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import net.indaba.paypal.service.form.SingleItemPaymentForm;
import net.indaba.paypal.service.service.ExpressCheckoutService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.WebKeys;

@Controller(value="singleItemPaymentController")
@RequestMapping(value = "VIEW")
public class SingleItemPaymentController {
	
	private Logger logger = Logger.getLogger(SingleItemPaymentController.class);
	
	@Autowired
	@Qualifier("singleItemPaymentFormValidator")
	private SingleItemPaymentFormValidator singleItemPaymentFormValidator;
	
	@Autowired
	@Qualifier("payPalExpressCheckoutService")
	private ExpressCheckoutService payPalExpressCheckoutService;

	public void setPayPalExpressCheckoutService(ExpressCheckoutService payPalExpressCheckoutService) {
		this.payPalExpressCheckoutService = payPalExpressCheckoutService;
	}

	// ************************************************
	// DEFAULT RENDER
	// ************************************************	
	@RenderMapping
	public String defaultRender(Model model, RenderRequest renderRequest) {		
		logger.debug("defaultRender");
		return "myShop";		
	}
	
	// ************************************************
	// GO TO MYSHOP
	// ************************************************	
	@RenderMapping(params = "myaction=myShop")
	public String goToMyShop(Model model, RenderRequest renderRequest) {
		logger.debug("goToMyShop");
		return "myShop";
	}
	
	// ************************************************
	// RENDER ERROR
	// ************************************************	
	@RenderMapping(params = "myaction=error")
	public String renderError(Model model, RenderRequest renderRequest) {		
		logger.debug("renderResultsError");
		return "error";
	}	
	
	// ************************************************
	// 1) SHORTCUT EXPRESS CHECKOUT
	// ************************************************
	@ActionMapping(params = "myaction=setExpressCheckOut")
	public void shortcutExpressCheckout(
			@ModelAttribute("singleItemPayment") SingleItemPaymentForm payPalForm, 
			BindingResult result,	    		
			Model model, ActionResponse response, ActionRequest actionRequest) {

		logger.debug("shortcutExpressCheckout");

		singleItemPaymentFormValidator.validate(payPalForm, result);		
		if (!result.hasErrors()){
			try{	
								
				// **************************************
				// PASO 1: SetExpressCheckout
				// **************************************
				String paymentAmount = payPalForm.getAmount();
				
				paymentAmount = paymentAmount.replace(',', '.');
				paymentAmount = String.format("%.02f", Float.parseFloat(paymentAmount));
				paymentAmount = paymentAmount.replace(',', '.');
				
				String portletId = (String) actionRequest.getAttribute(WebKeys.PORTLET_ID);
				String returnURL = "http://localhost:8080/web/guest/welcome?p_p_id=" + portletId + "&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_count=1&_" + portletId + "_myaction=getDetails";
				String cancelURL = "http://localhost:8080/web/guest/welcome?p_p_id=" + portletId + "&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_count=1&_" + portletId + "_myaction=myShop";
				
				HashMap<String, String> nvp = payPalExpressCheckoutService.SetExpressCheckoutOneTimePayment(paymentAmount, returnURL, cancelURL);
				if(nvp != null){
					
					String strAck = nvp.get("ACK").toString();
					if(strAck !=null && strAck.equalsIgnoreCase("Success")){
						
						// **************************************
						// PASO 2: PayPal Returns a Token
						// **************************************
						actionRequest.getPortletSession().setAttribute("TOKEN", nvp.get("TOKEN").toString());
						
						System.out.println("RESPONSE: TOKEN");
						Set set = nvp.entrySet();		                
						Iterator i = set.iterator();		               
						while(i.hasNext()) {
							Map.Entry me = (Map.Entry)i.next();
							System.out.print(me.getKey() + ": ");
							System.out.println(me.getValue());
						}
						System.out.println(" ");

						// **************************************
						// PASO 3: Redirect the Customer to PayPal
						// **************************************
						String paypalURL = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=" + nvp.get("TOKEN").toString();
						response.sendRedirect(response.encodeURL( paypalURL ));
					
					}else{
						
						// Display a user friendly Error on the page using any of the following error information returned by PayPal
						String ErrorCode = nvp.get("L_ERRORCODE0").toString();
						String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
						String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
						String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
												
						logger.debug(ErrorCode);
						logger.debug(ErrorShortMsg);
						logger.debug(ErrorLongMsg);
						logger.debug(ErrorSeverityCode);					
						
						response.setRenderParameter("myaction", "error");					
					}				
				}else{
					logger.debug("Algo fue mal en SetExpressCheckout API call");
					response.setRenderParameter("myaction", "error");
				}				

			} catch (Exception e){
				logger.error("[Error] ShortcutExpressCheckout", e);
				response.setRenderParameter("myaction", "error");
			}
		}else{
			try{
				logger.error("[Error] ShortcutExpressCheckout FieldErrors:");
				List<FieldError> errors = result.getFieldErrors();
				for (FieldError error : errors ) {				
					logger.error(error.getField() + " - " + error.getRejectedValue());
				}

				response.setRenderParameter("myaction", "myShop");

			} catch (Exception e){
				logger.error("[Error] ShortcutExpressCheckout", e);
				response.setRenderParameter("myaction", "error");
			}
		}
	}
	
	// ************************************************
	// 2) EXPRESS CHECKOUT DETAILS
	// ************************************************
	@RenderMapping(params = "myaction=getDetails")
	public String getExpressCheckoutDetails(Model model, RenderRequest renderRequest) {
				
		logger.debug("getExpressCheckoutDetails");		
		
		// **************************************
		// PASO 4: PayPal Redirects the Customer
		// **************************************
		try{		
			
			String token = (String) renderRequest.getPortletSession().getAttribute("TOKEN");		
			if ( token != null){
						
				// **************************************
				// PASO 5: getExpressCheckOutDetails
				// **************************************
				HashMap<String, String> nvp = payPalExpressCheckoutService.GetExpressCheckoutDetails(token);
				if(nvp != null){
						
					String strAck = nvp.get("ACK").toString();
					if(strAck != null && (strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning"))){
							
						System.out.println("RESPONSE: CUSTOMER DETAILS");				
						Set set = nvp.entrySet();		                
						Iterator i = set.iterator();		               
						while(i.hasNext()) {
							Map.Entry me = (Map.Entry)i.next();
							System.out.print(me.getKey() + ": ");
							System.out.println(me.getValue());
						}
						System.out.println(" ");
							
						String payerId = nvp.get("PAYERID").toString(); // ' Unique PayPal customer account identification number.				
						renderRequest.getPortletSession().setAttribute("PAYERID", payerId);
						
						String amt = nvp.get("AMT").toString(); 
						renderRequest.getPortletSession().setAttribute("AMT", amt);
												
						String firstName = nvp.get("FIRSTNAME").toString(); // ' Payer's first name.
						String lastName	= nvp.get("LASTNAME").toString(); // ' Payer's last name.						
						model.addAttribute("nombre", firstName + " " + lastName);
						
						return "confirmOrder";												
				
					} else { 
					
						// Display a user friendly Error on the page using any of the following error information returned by PayPal				
						String ErrorCode = nvp.get("L_ERRORCODE0").toString();
						String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
						String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
						String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();
		
						logger.debug(ErrorCode);
						logger.debug(ErrorShortMsg);
						logger.debug(ErrorLongMsg);
						logger.debug(ErrorSeverityCode);	
						
						return "error";		
					}
				}else{
					logger.debug("Algo fue mal en GetExpressCheckoutDetails API call");
					return "error";	
					
				}	
			} else {
				logger.debug("Hemos perdido el Token devuelto desde SetExpressCheckout API call");
				return "error";	
			
			}
		} catch (Exception e){	
			logger.error("[Error] getExpressCheckoutDetails", e);			
			return "error";				
		}		
	}	
	
	// **************************************
	// 3A) CONFIRM ORDER
	// **************************************
	@RenderMapping(params = "myaction=confirmOrder")
	public String confirmOrderPayPal(Model model, RenderRequest renderRequest) {
		
		logger.debug("confirmOrderPayPal");
		
		try{
			
			String token = (String) renderRequest.getPortletSession().getAttribute("TOKEN");	
			if ( token != null){
			
				// **************************************
				// PASO 6): DoExpressCheckOutPayment
				// **************************************
				String serverName =  renderRequest.getServerName();
				String payerId = (String) renderRequest.getPortletSession().getAttribute("PAYERID");
				String finalPaymentAmount = (String) renderRequest.getPortletSession().getAttribute("AMT");
			
				HashMap<String, String> nvp = payPalExpressCheckoutService.ConfirmPayment(token, payerId, finalPaymentAmount, serverName);
				if(nvp != null){
				
					String strAck = nvp.get("ACK").toString();
					if(strAck !=null && (strAck.equalsIgnoreCase("Success") || strAck.equalsIgnoreCase("SuccessWithWarning"))){
				
						System.out.println("RESPONSE: TRANSACTION RESULTS");				
						Set set = nvp.entrySet();		                
						Iterator i = set.iterator();		               
						while(i.hasNext()) {
							Map.Entry me = (Map.Entry)i.next();
							System.out.print(me.getKey() + ": ");
							System.out.println(me.getValue());
						}
						System.out.println(" ");
						
						return "thankYou";											
				
					} else {

						// Display a user friendly Error on the page using any of the following error information returned by PayPal				
						String ErrorCode = nvp.get("L_ERRORCODE0").toString();
						String ErrorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
						String ErrorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
						String ErrorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();

						logger.debug(ErrorCode);
						logger.debug(ErrorShortMsg);
						logger.debug(ErrorLongMsg);
						logger.debug(ErrorSeverityCode);	
						
						return "error";			
					}					
				}else{
					logger.debug("Algo fue mal en DoExpressCheckoutPayment API call");
					return "error";	
					
				}		
			} else {
				logger.debug("Hemos perdido el Token devuelto desde SetExpressCheckout API call");
				return "error";	
			
			}	
		} catch (Exception e){	
			logger.error("[Error] getExpressCheckoutDetails", e);
			return "error";

		}		
	}
	
	// **************************************
	// 3B) CANCEL ORDER
	// **************************************
	@RenderMapping(params = "myaction=cancelOrder")
	public String cancelOrderPayPal(Model model, RenderRequest renderRequest) {
		
		logger.debug("cancelOrderPayPal");
		
		try{
			renderRequest.getPortletSession().removeAttribute("TOKEN");
			renderRequest.getPortletSession().removeAttribute("PAYERID");
			renderRequest.getPortletSession().removeAttribute("AMT");
		
			return "myShop";
			
		} catch(Exception e){			
			logger.error("[Error] cancelOrderPayPal", e);
			return "error";			
		}
	}
		
	@ModelAttribute("singleItemPayment")
	public SingleItemPaymentForm getCommandObject() {		
		return new SingleItemPaymentForm();
	}		
}
