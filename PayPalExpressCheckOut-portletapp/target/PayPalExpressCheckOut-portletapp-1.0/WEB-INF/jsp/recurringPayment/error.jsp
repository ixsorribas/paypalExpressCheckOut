<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<portlet:defineObjects />

<div class="errorClass">
	<spring:message code="mensaje.error"/>
</div>
<br>
<div align="right">
	<portlet:renderURL var="volver">
		<portlet:param name="myaction" value="myShop" />	
	</portlet:renderURL>
	<a href="${volver}"><spring:message code="boton.volver" /></a>
</div>