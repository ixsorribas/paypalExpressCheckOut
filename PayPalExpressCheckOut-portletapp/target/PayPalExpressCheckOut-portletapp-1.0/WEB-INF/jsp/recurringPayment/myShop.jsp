<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %>

<portlet:defineObjects />

<portlet:actionURL var="payPal">
	<portlet:param name="myaction" value="setExpressCheckOut" />
</portlet:actionURL>

<div align="right">
	<portlet:renderURL var="suscriptors">
		<portlet:param name="myaction" value="getSuscriptors" />
		<portlet:param name="idPerfil" value="I-X9YU6P0PBCT1" />	
	</portlet:renderURL>
	Id. de perfil: I-X9YU6P0PBCT1
	<br>
	<a href="${suscriptors}">Ver información</a>
</div>


19.90 EUR cada mes

<form:form name="recurringPaymentForm" modelAttribute="recurringPayment" method="post" action="${payPal}">

	<form:input type="hidden" path="amount" id="amount" value="19.90"/> 
	
	<br>
	<input type='image' name='submit'
		src='https://www.paypalobjects.com/webstatic/en_US/btn/btn_subscribe_113x26.png'
		border='0' align='top' alt='Check out with PayPal' />
				
</form:form>
<a href="https://www.paypal.com/es/webapps/mpp/home" target="_blank">¿Qué es PayPal?</a>


<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function() {
		
		var pnamespace = '<portlet:namespace/>';	
		
		jQuery('input').each(function() {			
			jQuery(this).attr('id', pnamespace + this.id);
			jQuery(this).attr('name', pnamespace + this.name);
		});
		
		jQuery('select').each(function() {			
			jQuery(this).attr('id', pnamespace + this.id);
			jQuery(this).attr('name', pnamespace + this.name);
		});		
	});
</script>