package com.appsdeveloperblog.app.ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


//ApplicationContextAware will be used to read data from application context
public class SpringApplicationContext implements ApplicationContextAware{
	
	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext Context) throws BeansException {
		CONTEXT=Context;
	}
	
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}
	
	
	

}
