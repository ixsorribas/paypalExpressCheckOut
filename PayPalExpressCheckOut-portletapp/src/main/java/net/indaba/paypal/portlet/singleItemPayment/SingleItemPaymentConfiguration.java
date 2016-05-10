package net.indaba.paypal.portlet.singleItemPayment;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Esta clase monta el contexto necesario para el portlet de Spring. Punto donde
 * cargar los elementos especificos para el portlet.
 * 
 * En concreto, en la implementación actual se cargan los controladores y los
 * beans necesarios para configurar el portlet.
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan("net.indaba.paypal.portlet.singleItemPayment.controller")
public class SingleItemPaymentConfiguration {

}
