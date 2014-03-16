package com.startupweekend.ubc.sldshw.web;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
	@Bean
	public SpringTemplateEngine springTemplateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.addDialect(new SpringSecurityDialect());
		for (ITemplateResolver templateResolver : this.templateResolvers) {
			engine.addTemplateResolver(templateResolver);
		}
		return engine;
	}

	@Autowired
	private final Collection<ITemplateResolver> templateResolvers = Collections
			.emptySet();

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("home");
		registry.addViewController("/client").setViewName("client");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/user").setViewName("user");
	}

}