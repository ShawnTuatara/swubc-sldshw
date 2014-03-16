package com.startupweekend.ubc.sldshw.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import com.startupweekend.ubc.sldshw.SldShwUserDetailsService;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public SldShwUserDetailsService sldShwUserDetailsService() {
		return new SldShwUserDetailsService();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/user", "/slides-list")
				.authenticated().anyRequest().permitAll();
		http.formLogin().loginPage("/login").defaultSuccessUrl("/slides-list")
				.permitAll().and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/").permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(sldShwUserDetailsService());
	}
}