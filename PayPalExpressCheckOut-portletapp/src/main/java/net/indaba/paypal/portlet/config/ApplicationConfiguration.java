package net.indaba.paypal.portlet.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"net.indaba.paypal.service.serviceImpl"})
public class ApplicationConfiguration {
	
}