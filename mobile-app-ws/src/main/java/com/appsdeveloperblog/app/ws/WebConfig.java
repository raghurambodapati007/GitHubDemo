package com.appsdeveloperblog.app.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
		.addMapping("/**") //for all methods of all rest API's 
		.allowedMethods("GET","PUT","POST","DELETE")//For all types of HTTP methods
		.allowedOrigins("*");//Allow for all origins 
	}

}
