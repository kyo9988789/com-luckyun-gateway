package com.luckyun.getway.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 网管配置文件
 * 
 * 2019年05月22日,上午10:18 
 * {@link com.lucksoft.getway.config.GetwayBaseConfig}
 * 
 * @author yangj080
 * @version 1.0.0
 *
 */
@Configuration
@ConfigurationProperties(prefix = "common")
public class GetwayBaseConfig {

	private String authUrl;
	private List<String> noInterceptorUrl;
	
	private List<String> container;
	
	private List<String> startwith;
	
	private List<String> endwith;
	
	private String headerNotInterceptor;
	
	private String isOldService;

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public List<String> getNoInterceptorUrl() {
		return noInterceptorUrl;
	}

	public void setNoInterceptorUrl(List<String> noInterceptorUrl) {
		this.noInterceptorUrl = noInterceptorUrl;
	}

	public List<String> getContainer() {
		return container;
	}

	public void setContainer(List<String> container) {
		this.container = container;
	}

	public List<String> getStartwith() {
		return startwith;
	}

	public void setStartwith(List<String> startwith) {
		this.startwith = startwith;
	}

	public List<String> getEndwith() {
		return endwith;
	}

	public void setEndwith(List<String> endwith) {
		this.endwith = endwith;
	}

	public String getHeaderNotInterceptor() {
		return headerNotInterceptor;
	}

	public void setHeaderNotInterceptor(String headerNotInterceptor) {
		this.headerNotInterceptor = headerNotInterceptor;
	}

	public String getIsOldService() {
		return isOldService;
	}

	@Value("${luckyun.is-old-server:false}")
	public void setIsOldService(String isOldService) {
		this.isOldService = isOldService;
	}
	
	
}
