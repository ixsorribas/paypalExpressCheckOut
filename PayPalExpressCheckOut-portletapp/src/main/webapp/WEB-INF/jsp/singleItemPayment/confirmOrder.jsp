<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<portlet:defineObjects />

<%-- Cargamos los objetos a usar de la session --%>
<%   
	pageContext.setAttribute("AMT", renderRequest.getPortletSession().getAttribute("AMT"));
%>

${nombre}
<br>
Confirme el pago: ${AMT} EUR
<br>
<br>
<portlet:renderURL var="aceptar">
	<portlet:param name="myaction" value="confirmOrder" />		
</portlet:renderURL>
<a href="${aceptar}"><spring:message code="boton.aceptar" /></a>

<br>
<div align="right">
	<portlet:renderURL var="cancelar">
		<portlet:param name="myaction" value="cancelOrder" />	
	</portlet:renderURL>
	<a href="${cancelar}"><spring:message code="boton.cancelar" /></a>
</div>
