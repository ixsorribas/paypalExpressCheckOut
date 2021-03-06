<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %>

<portlet:defineObjects />

<c:forEach var="entry" items="${nvp}">
    <b>${entry.key}</b> ${entry.value}
    <br>
</c:forEach>

<br>
<div align="right">
	<portlet:renderURL var="volver">
		<portlet:param name="myaction" value="myShop" />	
	</portlet:renderURL>
	<a href="${volver}"><spring:message code="boton.volver" /></a>
</div>