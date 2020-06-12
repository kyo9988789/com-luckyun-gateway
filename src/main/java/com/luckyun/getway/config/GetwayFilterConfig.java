package com.luckyun.getway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.luckyun.getway.filter.AccessAuthorFilter;

/**
 * 创建过滤器Bean对象
 * 2019年05月22日,上午9:43
 * {@link com.GetwayFilterConfig.getway.config.FilterConfig}
 * @author yangj080
 * @version 1.0.0
 *
 */
@Configuration
public class GetwayFilterConfig {

	@Bean
	public AccessAuthorFilter accessAuthorFilter() {
		return new AccessAuthorFilter();
	}
	
	@Bean
	public <T> ThreadLocal<T> threadLocal(){
		return new ThreadLocal<>();
	}
}
