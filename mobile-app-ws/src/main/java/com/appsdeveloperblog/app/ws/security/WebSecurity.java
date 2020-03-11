package com.appsdeveloperblog.app.ws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.appsdeveloperblog.app.ws.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;


@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)  // specially to use @Secured ,@PreAuthorize @PostAuthorize annotation
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	private final UserRepository userRepository;
	
	private final UserService userDetailsService;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(UserService userDetailsService,BCryptPasswordEncoder bCryptPasswordEncoder,UserRepository userRepository) {
		this.bCryptPasswordEncoder=bCryptPasswordEncoder;
		this.userDetailsService=userDetailsService;
		this.userRepository=userRepository;
	}
	
	/* needed to configure few web ends as public and few as private */
	@Override
	protected void configure(HttpSecurity http) throws Exception{
				
		System.out.println("1. In configure method of websecurity configurer ");
		
		http
		.cors().and()
		.csrf().disable()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST,SecurityConstants.SING_UP_URL)
		.permitAll()
		.antMatchers(HttpMethod.GET,SecurityConstants.VERIFICATION_EMAIL_URL)
		.permitAll()
		.antMatchers(HttpMethod.POST,SecurityConstants.PASSWORD_RESET_REQUEST_URL)
		.permitAll()
		.antMatchers(HttpMethod.POST,SecurityConstants.PASSWORD_RESET_URL)
		.permitAll()
		.antMatchers(SecurityConstants.H2_CONSOLE)
		.permitAll()
		.antMatchers("/v2/api-docs","/configuration/**","/swagger*/**","/webjars/**")
		.permitAll()
		//.antMatchers(HttpMethod.DELETE,"/users/**").hasRole("ADMIN") 
		// CAN BE USED TO DELETE BY USING hasAuthority too.
		//.antMatchers(HttpMethod.DELETE,"/users/**").hasAnyRole("ADMIN","USER")
		//.antMatchers(HttpMethod.DELETE,"/users/**").hasAnyAuthority("DELETE_AUTHORITY","DELETE_ALL_AUTHORITY")
		//.antMatchers(HttpMethod.DELETE,"/users/**").hasAuthority("DELETE_AUTHORITY")
		//below are commented because we are using authorization value for authorization purpose
		//.antMatchers(SecurityConstants.NO_AUTHORIZATION)
		// .permitAll()
		.anyRequest().authenticated().and().addFilter(getAuthenticationFilter()).addFilter(new AuthorizationFilter(authenticationManager(),userRepository));
		
		//disable frame options http headers because not to store in html frame options
		http.headers().frameOptions().disable();
		 
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	
	public AuthenticationFilter getAuthenticationFilter() throws Exception {
		
		System.out.println("2. In Get AuthenticationFilter() of webSecurity");
		final AuthenticationFilter filter =new AuthenticationFilter(authenticationManager());	
		System.out.println("6 . In Get AuthenticationFilter()");
		filter.setFilterProcessesUrl("/users/login");
		return filter;
		
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		final CorsConfiguration configuration= new CorsConfiguration();
		
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Authorization","Header"));
		
		final UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**",configuration);
		return source;
		
	}

}
