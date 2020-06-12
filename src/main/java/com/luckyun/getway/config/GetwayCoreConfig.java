package com.luckyun.getway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.luckyun.getway.Locator.ConfigSystemLocator;

/**
 * 使用Filter 处理跨域请求，即CORS（跨来源资源共享）
 * 
 * 2019年05月22日,上午9:43
 * {@link com.GetwayCoreConfig.getway.config.CoreConfig}
 * @author yangj080
 * @version 1.0.0
 *
 */
@Configuration
public class GetwayCoreConfig {
	
	@Autowired
    ZuulProperties zuulProperties;
	
    @Autowired
    ServerProperties server;

	/**
	 * 设置 跨域请求参数，处理跨域请求
	 * 
	 * @return
	 */
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}
	
	@Bean
	public ConfigSystemLocator configSystemLocator() {
		return new ConfigSystemLocator(server.getServerHeader(), zuulProperties);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		 requestFactory.setConnectTimeout(1000);// 设置超时  
		 requestFactory.setReadTimeout(1000);
		 return new RestTemplate(requestFactory);
	}
}
